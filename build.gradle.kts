@file:Suppress("UnstableApiUsage")

buildscript {
    dependencies {
        classpath(Dependencies.Android.gradlePlugin)
        classpath(Dependencies.Kotlin.gradlePlugin)
        classpath(Dependencies.mavenPublish)
        classpath(Dependencies.dokka)
        classpath(Dependencies.ktlint)
        classpath(Dependencies.detekt)
    }

    repositories {
        mavenCentral()
        jcenter()
        google()
        gradlePluginPortal()
    }
}

subprojects {

    repositories {
        mavenCentral()
        jcenter()
        google()
        gradlePluginPortal()
    }

    buildDir = File(rootProject.buildDir, name)

    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        failFast = false // fail build on any finding
        ignoreFailures = false
        buildUponDefaultConfig = true // preconfigure defaults

        reports {
            html.enabled = true // observe findings in your browser with structure and code snippets
            xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
            txt.enabled =
                true // similar to the console output, contains issue signature to manually edit baseline files
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        jvmTarget = "1.8"
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // Configuration documentation: https://github.com/JLLeitschuh/ktlint-gradle#configuration
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        // Prints the name of failed rules
        verbose.set(true)
        // Default "plain" reporter is actually harder to read.
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
        }
        disabledRules.set(
            setOf(
                // IntelliJ refuses to sort imports correctly.
                // This is a known issue: https://github.com/pinterest/ktlint/issues/527
                "import-ordering"
            )
        )
    }

    plugins.withId("org.jetbrains.kotlin.android") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-progressive")
            }
        }

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-progressive")
            }
        }

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    @Suppress("DEPRECATION")
    plugins.withType<com.android.build.gradle.BasePlugin>().configureEach {
        getExtension().compileOptions {
            sourceCompatibility("1.8")
            targetCompatibility("1.8")
        }
    }

    afterEvaluate {
        tasks.findByName("check")?.dependsOn("detekt")

        configurations.configureEach {
            // There could be transitive dependencies in tests with a lower version. This could cause
            // problems with a newer Kotlin version that we use.
            resolutionStrategy.force(Dependencies.Kotlin.reflect)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.common)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.jdk8)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.jdk7)
            resolutionStrategy.force(Dependencies.Kotlin.Stdlib.jdk6)
        }
    }
}