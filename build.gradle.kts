plugins {
    alias(libs.plugins.blossom)
    java
}

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
    compileOnly(libs.viaversion.api)
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
    implementation(libs.paho.mqtt)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("name", project.name)
                property("version", version.toString())
            }
        }
    }
}
