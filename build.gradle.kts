import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.slf4j.event.Level

plugins {
    idea
    alias(libs.plugins.modDevGradle)
}

// Mod stuff
val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project
val modIssueTracker: String by project

val datagenOutput: String = "src/generated/resources"

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to modVersion,
        "mod_license" to modLicense,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "mod_issue_tracker" to modIssueTracker,
        "minecraft_version_range" to "[${libs.versions.minecraft.get()}]",
        "loader_version_range" to "[1,)",
        "neo_version_range" to "[${libs.versions.neoforge.get()},)",
        "jei_version_range" to "[${libs.versions.jei.get()},)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

base {
    archivesName.set("$modName-mc${libs.versions.minecraft.get()}")
    version = modVersion
    group = modGroupId
}

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
}

val datagen: SourceSet by sourceSets.creating

/**
 * Sets up a dependency configuration called 'localRuntime'.
 * This configuration should be used instead of 'runtimeOnly' to declare
 * a dependency that will be present for runtime testing but that is
 * "optional", meaning it will not be pulled by dependents of this mod.
 */
val localRuntime: Configuration by configurations.creating

val datagenImplementation: Configuration = configurations.getByName(datagen.implementationConfigurationName)

configurations.runtimeClasspath.configure {
    extendsFrom(localRuntime, datagenImplementation)
}

configurations {
    getByName(datagen.compileClasspathConfigurationName).extendsFrom(compileClasspath.get())
    getByName(datagen.runtimeClasspathConfigurationName).extendsFrom(runtimeClasspath.get())
    getByName(datagen.annotationProcessorConfigurationName).extendsFrom(annotationProcessor.get())
}

neoForge {
    version = libs.versions.neoforge.get()
    addModdingDependenciesTo(datagen)
    validateAccessTransformers = true

    parchment {
        minecraftVersion = libs.versions.parchmentMinecraft
        mappingsVersion = libs.versions.parchment
    }

    runs {
        configureEach {
            systemProperty("neoforge.logging.markers", "REGISTRIES")
            logLevel = Level.DEBUG
            systemProperty("neoforge.enabledGameTestNamespaces", modId)

            // Only JBR allows enhanced class redefinition, so ignore the option for any other JDKs
            jvmArguments.addAll("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-ea")
        }

        register("client") {
            client()
            gameDirectory = file("run/client")
        }

        register("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
        }

        register("datagen") {
            data()
            gameDirectory = file("run/datagen")

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file(datagenOutput).path,
                "--existing",
                file("src/main/resources/").path
            )
        }

        register("gameTest") {
            type = "gameTestServer"
            gameDirectory = file("run/game_test")
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.test.get())
            sourceSet(datagen)
        }
    }

    unitTest {
        enable()
        testedMod = mods[modId]
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
    // datagen can use mod code
    datagenImplementation(sourceSets.main.get().output)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
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
//    localRuntime("curse.maven:coroutil-237749:5622966")

    // EMI
    compileOnly(libs.emi) { artifact { classifier = "api" } }
//    runtimeOnly(libs.emi)

    // JEI
    compileOnly(libs.bundles.jei.api)
    runtimeOnly(libs.jei)

    testImplementation(datagen.output)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true

        excludeDirs.addAll(
            arrayOf(
                "run", ".gradle", ".idea", "externals", "src/generated/resources/.cache"
            ).map { file(it) })
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