import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.slf4j.event.Level

plugins {
    idea
    id("net.neoforged.moddev") version "2.0.78"
}

// Mappings
val parchmentVersion: String = "2024.07.07"
val parchmentMinecraftVersion: String = "1.21"

//# Mod stuff
val modID: String = "alekiships"
val modName: String = "aleki's Nifty Ships"
val modVersion: String = "1.0.0"
//# Minecraft stuff
val minecraftVersion: String = "1.21"
val neoVersion: String = "21.1.168"

// Dependency versions
val emiVersion: String = "1.1.10+1.21"
val jeiVersion: String = "19.5.2.66"
val topVersion: String = "1.21_neo-12.0.4-6"
val jadeFileID: String = "5591256"
val weather2FileId: String = "6634565"

// Dev dependencies
val lombokVersion: String = "1.18.36"

val datagenOutput: String = "src/generated/resources"

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "modId" to modID,
        "modName" to modName,
        "modVersion" to modVersion,
        "minecraftVersionRange" to "[$minecraftVersion,)",
        "neoForgeVersionRange" to "[$neoVersion,)",
        "jeiVersionRange" to "[$jeiVersion,)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

base {
    archivesName.set("alekiNiftyShips-FORGE-$minecraftVersion")
    version = modVersion
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
    version = neoVersion
    addModdingDependenciesTo(sourceSets["datagen"])
    validateAccessTransformers = true

    parchment {
        minecraftVersion.set(parchmentMinecraftVersion)
        mappingsVersion.set(parchmentVersion)
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
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    "datagenCompileOnly"("org.projectlombok:lombok:$lombokVersion")
    "datagenAnnotationProcessor"("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // QOL Dev dependencies should use `localRuntime`. `runtimeOnly` is for stuff we actually want at runtime

    implementation("curse.maven:jade-324717:$jadeFileID")
    compileOnly("mcjty.theoneprobe:theoneprobe:$topVersion")

    // Weather 2 mod so we can compile
    compileOnly("curse.maven:weather-2-237746:$weather2FileId")
    // Weather 2 mod so we can test at runtime
//    runtimeOnly("curse.maven:weather-2-237746:$weather2FileId")
    // Lib mod for weather 2, we don't want to interact with this thing at all
//    "localRuntime"("curse.maven:coroutil-237749:5622966")

    // EMI
    compileOnly("dev.emi:emi-neoforge:${emiVersion}:api")
    //runtimeOnly("dev.emi:emi-neoforge:${emiVersion}")

    // JEI
    compileOnly("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}")
    compileOnly("mezz.jei:jei-${minecraftVersion}-neoforge-api:${jeiVersion}")
    runtimeOnly("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true

        val elements = arrayOf(
            "run", ".gradle", ".idea", "gradle", "externals", "src/generated/resources/.cache"
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