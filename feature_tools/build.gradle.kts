plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android { namespace = "com.wall.feature.tools"; compileSdk = 36; defaultConfig { minSdk = 28 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }; kotlinOptions { jvmTarget = "17" }; buildFeatures { compose = true } }
dependencies {
    implementation(project(":core_wallpaper"))
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.compose.foundation:foundation-layout:1.7.8")
}
