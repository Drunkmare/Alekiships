import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.slf4j.event.Level

plugins {
    idea
    id("net.neoforged.moddev") version "2.0.78"
}

// Mod stuff
val modID: String = "alekiships"
val modName: String = "aleki's Nifty Ships"

val datagenOutput: String = "src/generated/resources"

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "modId" to modID,
        "modName" to modName,
        "modVersion" to libs.versions.alekiShips.get(),
        "minecraftVersionRange" to "[${libs.versions.minecraft.get()},)",
        "neoForgeVersionRange" to "[${libs.versions.neforge.get()},)",
        "jeiVersionRange" to "[${libs.versions.jei.get()},)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

base {
    archivesName.set("alekiNiftyShips-FORGE-${libs.versions.minecraft.get()}")
    version = libs.versions.alekiShips.get()
    group = modID
}

// Mojang ships Java 21 to end users starting in 1.20.5, so mods should target Java 21.
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

sourceSets {
    main {
        resources {
            srcDir(datagenOutput)
            srcDir(generateModMetadata)
        }
    }
    test {
        resources {
            // Pull down our generated data for tests
            srcDir(datagenOutput)
        }
    }
    create("datagen")
}

configurations {
    // Sets up a dependency configuration called 'localRuntime'.
    // This configuration should be used instead of 'runtimeOnly' to declare
    // a dependency that will be present for runtime testing but that is
    // "optional", meaning it will not be pulled by dependents of this mod.
    get("runtimeClasspath").extendsFrom(create("localRuntime"))
    // Datagen can reference our code in main
    get("datagenCompileClasspath").extendsFrom(compileClasspath.get())
    get("datagenRuntimeClasspath").extendsFrom(runtimeClasspath.get())
    // Wtf man why isn't this done for us??
    get("testCompileClasspath").extendsFrom(compileClasspath.get())
    get("testCompileClasspath").extendsFrom(runtimeClasspath.get())
}

neoForge {
    version = libs.versions.neforge.get()
    addModdingDependenciesTo(sourceSets["datagen"])
    validateAccessTransformers = true

    parchment {
        minecraftVersion = libs.versions.parchmentMinecraft
        mappingsVersion = libs.versions.parchment
    }

    runs {
        configureEach {
            systemProperty("neoforge.logging.markers", "REGISTRIES")
            logLevel = Level.DEBUG
            systemProperty("neoforge.enabledGameTestNamespaces", modID)

            // Only JBR allows enhanced class redefinition, so ignore the option for any other JDKs
            jvmArguments.addAll("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-ea")
        }

        register("client") {
            client()
        }

        // Second client run for dev 2 player testing
        register("client2") {
            client()
            programArguments.addAll("--username", "Dev2")
        }

        register("server") {
            server()
            programArgument("--nogui")
        }

        register("datagen") {
            data()

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll(
                "--mod",
                modID,
                "--all",
                "--output",
                file(datagenOutput).absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        register("gameTest") {
            type = "gameTestServer"
        }
    }

    mods {
        create(modID) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.test.get())
            sourceSet(sourceSets["datagen"])
        }
    }

    unitTest {
        enable()
        testedMod = mods[modID]
    }

    ideSyncTask(generateModMetadata)
}

repositories {
    mavenCentral()
    mavenLocal()
    exclusiveContent {
        forRepository { maven { url = uri("https://maven.terraformersmc.com/") } }
        filter { includeGroup("dev.emi") }
    }
    exclusiveContent {
        forRepository { maven { url = uri("https://maven.blamejared.com/") } }
        filter { includeGroup("mezz.jei") }
    }
    exclusiveContent {
        forRepository { maven { url = uri("https://maven.k-4u.nl/") } }
        filter { includeGroup("mcjty.theoneprobe") }
    }
    exclusiveContent {
        forRepository { maven { url = uri("https://www.cursemaven.com") } }
        filter { includeGroup("curse.maven") }
    }
}

dependencies {
    "datagenImplementation"(sourceSets["main"].output)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    "datagenCompileOnly"(libs.lombok)
    "datagenAnnotationProcessor"(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // QOL Dev dependencies should use `localRuntime`. `runtimeOnly` is for stuff we actually want at runtime

    implementation(libs.jade)
    compileOnly(libs.top)

    // Weather 2 mod so we can compile
    compileOnly(libs.weather2)
    // Weather 2 mod so we can test at runtime
//    runtimeOnly(libs.weather2)
    // Lib mod for weather 2, we don't want to interact with this thing at all
//    "localRuntime"("curse.maven:coroutil-237749:5622966")

    // EMI
    compileOnly("dev.emi:emi-neoforge:${libs.versions.emi.get()}:api")
    //runtimeOnly("dev.emi:emi-neoforge:${libs.versions.emi.get()}")

    // JEI
    compileOnly(libs.bundles.jei.api)
    runtimeOnly(libs.jei)

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true

        val elements = arrayOf(
            "run", ".gradle", ".idea", "externals", "src/generated/resources/.cache"
        ).map { file(it) }
        excludeDirs.addAll(
            elements
        )
    }
}

tasks {
    named("neoForgeIdeSync") {
        dependsOn(generateModMetadata)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }
}