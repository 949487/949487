pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Bizhi"
include(
    ":app",
    ":feature_home",
    ":feature_tools",
    ":feature_settings",
    ":core_network",
    ":core_wallpaper",
    ":core_xpath"
)
