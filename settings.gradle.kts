pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "pingu"
include(":app")


include(":common:permission")
include(":feature:camera")
include(":feature:settings")
include(":common:login")
include(":common:game")
include(":feature:scoreboard")
include(":common:rpsmodel")
include(":common:storage")
