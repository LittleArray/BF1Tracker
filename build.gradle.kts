plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
    application
}

group = "top.ffshaozi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("ch.qos.logback:logback-core:1.4.7")
}

tasks.test {
    useJUnitPlatform()
}
tasks.jar {
    enabled = true
    manifest {
        attributes(mapOf("Main-Class" to "MainKt"))
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach {
        println("add from sources: ${it.name}")
    }
    from(sourcesMain.output)
    exclude(
        "META-INF/*",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/versions/9/module-info.class",
        "META-INF/LICENSE","module-info.class",
        "META-INF/*.txt",
        "META-INF/services/org.eclipse.jetty.webapp.Configuration",
        "META-INF/services/jakarta.servlet.ServletContainerInitializer"
    )
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}