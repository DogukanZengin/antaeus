
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

const val junitVersion = "5.6.0"

/**
 * Configures the current project as a Kotlin project by adding the Kotlin `stdlib` as a dependency.
 */
fun Project.kotlinProject() {
    dependencies {
        // Kotlin libs
        "implementation"(kotlin("stdlib"))

        // Logging
        "implementation"("org.slf4j:slf4j-simple:1.7.30")
        "implementation"("io.github.microutils:kotlin-logging:1.7.8")

        // Mockk
        "testImplementation"("io.mockk:mockk:1.9.3")

        // JUnit 5
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:$junitVersion")
        "runtime"("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }
}

/**
 * Configures data layer libs needed for interacting with the DB
 */
fun Project.dataLibs() {
    dependencies {
        "implementation"("org.jetbrains.exposed:exposed:0.17.7")
        "implementation"("org.xerial:sqlite-jdbc:3.30.1")
    }
}

/**
 * Configures scheduler libs for scheduling the invoice payment task
 */
fun Project.schedulerLibs(){
    dependencies {
        "implementation"("it.justwrote:kjob-core:0.2.0")
        "implementation"( "it.justwrote:kjob-mongo:0.2.0") // for mongoDB persistence
        "implementation"( "it.justwrote:kjob-inmem:0.2.0")
    }
}

/**
 * Configures async messaging related libs
 */
fun Project.messagingLibs(){
    dependencies {
        "implementation"("com.rabbitmq:amqp-client:5.9.0")
    }
}