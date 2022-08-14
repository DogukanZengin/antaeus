plugins {
    kotlin("jvm")
}

kotlinProject()
schedulerLibs()
dependencies {
    implementation(project(":pleo-antaeus-messaging"))
}
