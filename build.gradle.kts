import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
    id("java") // Nödvändig för Kotlin
    id("org.jetbrains.kotlin.jvm") version "1.9.23" // Kotlin-version
    id("org.jetbrains.intellij") version "1.17.3"   // IntelliJ SDK-plugin
}

group = "com.minorg.figmabridge"
version = "0.1.0"

repositories {
    mavenCentral()
}

// Konfigurera IntelliJ-pluginet
intellij {
    // Vår mål-IDE. 'PC' står för PyCharm Community Edition
    type.set("PC")
    // Version av PyCharm vi bygger mot.
    version.set("2024.1") 
    
    // Vi måste explicit ange att vårt plugin beror på
    // Pythons kärnmoduler, annars kan det inte laddas i PyCharm.
    plugins.set(listOf("com.intellij.modules.python"))
}

// Se till att källkoden kompileras med rätt Java-version
tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

// Se till att Kotlin-kompilatorn också använder Java 17
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

// Denna uppgift uppdaterar automatiskt <version> i plugin.xml
// baserat på versionen som är satt högst upp i denna fil.
tasks.withType<PatchPluginXmlTask> {
    changeNotes.set("Initial version.")
    pluginDescription.set("Ett plugin för att ansluta Figma-designer till PyCharm-projekt.")
}
