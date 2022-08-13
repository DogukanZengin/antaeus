plugins {
    kotlin("jvm")
}

kotlinProject()
messagingLibs()

dependencies {
    implementation(project(":pleo-antaeus-core"))
    implementation(project(":pleo-antaeus-models"))
}