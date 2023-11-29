plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"

    id("net.mamoe.mirai-console") version "2.16.0"
    `maven-publish`
    signing
}

group = "online.ruin_of_future"
version = "1.5.2"

repositories {
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    api(platform("net.mamoe:mirai-bom:2.16.0"))
    api("net.mamoe:mirai-core-api")     // 编译代码使用
    runtimeOnly("net.mamoe:mirai-core") // 运行时使用
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}
