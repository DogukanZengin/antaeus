
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
        "implementation"("com.sksamuel.hoplite:hoplite-core:2.5.2")
        "implementation"("com.sksamuel.hoplite:hoplite-yaml:2.5.2")
        "implementation"("org.apache.commons:commons-lang3:3.12.0")
        // Mockk
        "testImplementation"("io.mockk:mockk:1.9.3")

        // JUnit 5
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:$junitVersion")
        "runtimeOnly"("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }
}

/**
 * Configures data layer libs needed for interacting with the DB
 */
fun Project.dataLibs() {
    dependencies {
        "implementation"("org.jetbrains.exposed:exposed-core:0.39.2")
        "implementation"("org.jetbrains.exposed:exposed-dao:0.39.2")
        "implementation"("org.jetbrains.exposed:exposed-jdbc:0.39.2")
        "implementation"("org.jetbrains.exposed:exposed-java-time:0.39.2")
        "implementation"("org.xerial:sqlite-jdbc:3.30.1")
    }
}

/**
 * Configures scheduler
 */
fun Project.schedulerLibs(){
    dependencies {
        "implementation"("com.github.kagkarlsson:db-scheduler:11.2")
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