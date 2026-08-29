plugins {
    kotlin("jvm") version "2.2.10"
    application
}

group = "com.quispe.cronograma"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.quispe.cronograma.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
