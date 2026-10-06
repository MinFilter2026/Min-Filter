plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

val mihomoVersion = "0.3.5"
val mihomoAar = layout.buildDirectory.file("libs/libmihomo-android-v$mihomoVersion.aar")

val downloadMihomo = tasks.register("downloadMihomo") {
    inputs.property("mihomoVersion", mihomoVersion)
    outputs.file(mihomoAar)
    doLast {
        val target = mihomoAar.get().asFile
        target.parentFile.mkdirs()
        val url = "https://github.com/oviron/libmihomo-android/releases/download/v$mihomoVersion/libmihomo-android-v$mihomoVersion.aar"
        logger.lifecycle("Downloading Min Filter network engine: $url")
        uri(url).toURL().openStream().use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
    }
}

android {
    namespace="com.minfilter.app"
    compileSdk=35
    defaultConfig {
        applicationId="com.minfilter.app"
        minSdk=26
        targetSdk=35
        versionCode=9
        versionName="1.5.2"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(files(mihomoAar).builtBy(downloadMihomo))
}
