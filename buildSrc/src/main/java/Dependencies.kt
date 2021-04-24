@file:JvmName("Deps")

object Versions {
    const val targetSdk = 30
    const val minSdk = 21
    const val kotlin = "1.4.32"
    const val core = "1.3.2"
}

object Dependencies {
    object Kotlin {
        private fun kotlin(module: String) =
            "org.jetbrains.kotlin:kotlin-$module:${Versions.kotlin}"

        val reflect: String get() = kotlin("reflect")

        val gradlePlugin: String get() = kotlin("gradle-plugin")

        object Stdlib {
            val common: String get() = kotlin("stdlib-common")
            val jdk8: String get() = kotlin("stdlib-jdk8")
            val jdk7: String get() = kotlin("stdlib-jdk7")
            val jdk6: String get() = kotlin("stdlib")
        }
    }

    const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2"

    object Android {
        const val gradlePlugin = "com.android.tools.build:gradle:4.1.3"
    }

    object Google {
        const val material = "com.google.android.material:material:1.3.0"
    }

    object AndroidX {
        object Core {
            const val runtime = "androidx.core:core:${Versions.core}"
            const val ktx = "androidx.core:core-ktx:${Versions.core}"
        }
    }

    const val mavenPublish = "com.vanniktech:gradle-maven-publish-plugin:0.13.0"
    const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:9.4.1"
    const val detekt = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.14.2"

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val androidJunit = "androidx.test.ext:junit:1.1.2"
        const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
    }
}