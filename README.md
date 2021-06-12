# MCLoader
MCLoader is a small Project, that allows Users, to install Mods without installing a Loader.

## How does it work?
MCLoader uses Instrumentation, to attach a Java-Agent to your Minecraft Instance.

## How can I use it

### Developer
Download and decompile an obfuscated version of the Game. Then download the Example Mod, and set it up.
Now you can access Minecraft, by using Mixin

### Player
Put your mod into the .minecraft folder and add following to the java arguments: `-javaagent:modnamehere.jar`

## Contributing

If you want to contribute you can always do so with a Pull-Request.
Import the Project as usual but select Gradle 7 instead of Gradle-Wrapper. 
