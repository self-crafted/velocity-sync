plugins {
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadowJar)
    java
}

var displayName = "MQTT-sync"

group = "com.github.selfcrafted"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "viaversion"
        url = uri("https://repo.viaversion.com/everything/")
    }
}

dependencies {
    compileOnly(libs.bundles.velocity)
    annotationProcessor(libs.velocity.api)
    implementation(libs.bundles.common)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("id", project.name)
                property("name", displayName)
                property("version", version.toString())
            }
        }
    }
}

tasks {
    shadowJar {
        archiveBaseName.set(displayName)
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        mergeServiceFiles()
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }
}
