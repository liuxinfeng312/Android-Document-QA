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

//        flatDir {
//            dirs("app/libs") // 指定包含 AAR 文件的目录
//        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{ url = uri("https://jitpack.io") }

    }
}

rootProject.name = "Demo"
include(":app")
//include(":wBankAi")
//include(":app:libs")
//include(":PaddleOCR4Android")

