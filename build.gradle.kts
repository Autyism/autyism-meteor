plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT"
}

fun prop(name: String): String = project.property(name).toString()

version = prop("mod_version")
group = prop("maven_group")
base { archivesName.set(prop("archives_base_name")) }

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net") { name = "FabricMC" }
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_version")}")

    // Meteor Client：运行时依赖（不打包）。仓库里只有用户实例中的同一版本 jar。
    modCompileOnly(files("libs/meteor-client-1.21.11-86.jar"))
    compileOnly(files("libs/orbit-0.2.4.jar"))
    if (providers.gradleProperty("amGameTest").isPresent) {
        modLocalRuntime(files("libs/meteor-client-1.21.11-86.jar"))
        // 开发环境不会展开 Meteor 的内嵌 jar：手动加到运行时（Fabric API 自己的模块除外）
        modLocalRuntime(fileTree("libs/meteor-nested") { include("*.jar"); exclude("fabric-*.jar") })
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to prop("mod_id"),
        "mod_name" to prop("mod_name"),
        "mod_version" to prop("mod_version"),
        "minecraft_version" to prop("minecraft_version"),
    )
    inputs.properties(props)
    filesMatching(listOf("fabric.mod.json")) { expand(props) }
}

// 客户端 GameTest：./gradlew runClientGameTest -PamGameTest
if (providers.gradleProperty("amGameTest").isPresent) {
    fabricApi {
        configureTests {
            createSourceSet.set(true)
            modId.set("autyism-meteor-gametest")
            enableGameTests.set(false)
            enableClientGameTests.set(true)
            eula.set(true)
            clearRunDirectory.set(true)
            username.set("AMGameTest")
        }
    }
}
