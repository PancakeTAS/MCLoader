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