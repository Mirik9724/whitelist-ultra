plugins{
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "2.0.20-Beta1"
    kotlin("kapt") version "2.0.20-Beta1"
}

group = "net.Mirik9724"
val vers = "0.9"
val build = "b1"
version = vers + "-" + build

repositories{
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo1.maven.org/maven2/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://repo.papermc.io/repository/maven-releases/") }
    maven {url = uri("https://repo.papermc.io/repository/maven-snapshots") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://libraries.minecraft.net") }
}

dependencies{
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation(kotlin("stdlib-jdk8"))

    implementation("net.kyori:adventure-api:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.14.0")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.1")

    compileOnly("com.github.Mirik9724:MirikAPI:v0.1.5.8")
}

val targetJavaVersion = 17
java{
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach{
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources{
    val props = mapOf("version" to vers)
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar{
    archiveClassifier.set("")
}

sourceSets {
    named("main"){
        kotlin{
            exclude("net/Mirik9724/whitelist_ultra/Bu")
        }
    }
}
