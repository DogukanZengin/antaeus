plugins {
    application
    kotlin("jvm")
}

kotlinProject()

dataLibs()

messagingLibs()

schedulerLibs()

application {
    mainClass.set("io.pleo.antaeus.app.AntaeusApp")
}

dependencies {
    implementation(project(":pleo-antaeus-data"))
    implementation(project(":pleo-antaeus-rest"))
    implementation(project(":pleo-antaeus-core"))
    implementation(project(":pleo-antaeus-models"))
    implementation(project(":pleo-antaeus-messaging"))
    implementation(project(":pleo-antaeus-scheduler"))
}