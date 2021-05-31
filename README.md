# MCLoader
MCLoader is a small Project, that allows Users, to install Mods without installing a Loader.

## How does it work?
MCLoader uses the Attach API and Instrumentation, to attach a Java-Agent to your running Minecraft Instance.

## How can I use it

### Developer
Download and decompile an obfuscated version of the Game. Then download the Example Mod, and set it up.
Now you can access Minecraft, by using Reflection, or rewrite Classes using ASM.

### Player
You can download the standalone jar file from the Releases Page. Now, open your Minecraft, and when it finishes loading, double click the standalone jar and select a Mod to load.

## Troubleshooting

### The Mod does not work
You can only add 1 Mod, to a running instance. If you want to change the Mod, you need to restart your Game.

### The where I select my mod does not show
This means, that MCLoader couldn't find a running Minecraft Instance. Try starting Minecraft without Forge or any modifications.

### Contributing

If you want to contribute you can always do so with a Pull-Request.
This Project uses Gradle 7, and Subprojects. If you are using Eclipse, open the Project Explorer instead of the Package Explorer!
Import the Project as usual but select Gradle 7 instead of Gradle-Wrapper. 

mcloader-api: This is the API that is going to be visible for the Modder, please keep this Code as simple as possible and hide Classes/Methods/Fields if not ment to be accessed.
mcloader-gradle: This is the Gradle Plugin that is used to launch the Client.
mcloader-standalone: This is the Program that the End-User will launch. Please keep the UI User-Friendly and Simple. Focus on Compatibility, for example, with a Java Check, that searches for Compatible Java Installations.

Current Dev-Branch Build status:

[![Java CI with Gradle](https://github.com/MCPfannkuchenYT/MCLoader/actions/workflows/gradle.yml/badge.svg?branch=dev)](https://github.com/MCPfannkuchenYT/MCLoader/actions/workflows/gradle.yml)