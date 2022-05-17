package de.pfannekuchen.mcloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.jetbrains.java.decompiler.main.collectors.VarNamesCollector;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import com.google.gson.Gson;

import de.pfannekuchen.launcher.JsonDownloader;
import de.pfannekuchen.launcher.json.VersionJson;
import de.pfannekuchen.mcloader.manifest.MinecraftVersionManifest;
import de.pfannekuchen.mcloader.manifest.Version;
import de.pfannekuchen.mcloader.remapper.JarRemapper;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.provider.ClassLoaderProvider;
import net.md_5.specialsource.provider.JarProvider;
import net.md_5.specialsource.provider.JointProvider;

/**
 * Main MCLoader Plugin.
 * Applies the Project to the Gradle Workspace.
 * @author Pancake
 */
public class MCLoaderPlugin implements Plugin<Project> {

	/**
	 * MCLoader User Home Cache Directory
	 */
	private File cache;
	
	/**
	 * Game File Cache for all game files of the current version
	 */
	private File gameCache;

	/**
	 * User Configuration Extension for MCLoader settings
	 */
	private MCLoaderExtension config;
	
	/**
	 * Ran when the plugin is being applied.
	 * Prepares:
	 *  - Task List
	 *  - Main Extension
	 *  - Game Files
	 *  - Decompilation and Deobfuscation
	 *  - Compile Properties
	 *  - Launch Files
	 */
	@Override
	public void apply(Project project) {
		// Apply Java Plugin
		project.getPluginManager().apply("java-library");
		// Change source Compatibility
		project.getExtensions().getByType(JavaPluginExtension.class).setSourceCompatibility(JavaVersion.VERSION_1_8);
		project.getExtensions().getByType(JavaPluginExtension.class).setTargetCompatibility(JavaVersion.VERSION_1_8);
		/* Do a lot of stuff after evaluation */
		project.afterEvaluate(_p -> {
			// Load Config
			config = project.getExtensions().getByType(MCLoaderExtension.class);
			if (!config.getAuthors().isPresent()) System.err.println("Authors Property missing!");
			if (!config.getDescription().isPresent()) System.err.println("Description Property missing!");
			if (!config.getGroup().isPresent()) System.err.println("Group Property missing!");
			if (!config.getId().isPresent()) System.err.println("Id Property missing!");
			if (!config.getMcversion().isPresent()) System.err.println("MCVersion Property missing!");
			if (!config.getName().isPresent()) System.err.println("Name Property missing!");
			if (!config.getVersion().isPresent()) System.err.println("Version Property missing!");
			if (!config.getSource().isPresent()) config.getSource().set("");
			if (!config.getWebsite().isPresent()) config.getWebsite().set("");
			
			cache = new File(project.getGradle().getGradleUserHomeDir(), "mcloader");
			gameCache = new File(cache, "minecraft_" + config.getMcversion().get());
			if (!gameCache.exists()) {
				cache.mkdir();
				// Download Game if the cache folder does not exist
				downloadGame();
				// Deobfuscate and decompile client if the cache folder does not exist
				deobfuscateGame(project);
			}
			// Update MCLoader-API
			try {
				Files.copy(MCLoaderPlugin.class.getResourceAsStream("/MCLoader-API.jar"), new File(gameCache, "mcloaderapi.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Add Dependencies
			project.getRepositories().flatDir(flat -> {
				flat.setName("Local Maven");
				flat.setDirs(new HashSet<File>() {{ add(new File(gameCache, "client")); }});
			});
			project.getRepositories().mavenCentral();
			project.getRepositories().maven(maven -> {
				try {
					maven.setName("SpongePowered Mixin Maven");
					maven.setUrl(new URI("https://repo.spongepowered.org/repository/maven-public/"));
				} catch (URISyntaxException e) { e.printStackTrace(); }
			});
			project.getConfigurations().getByName("implementation").extendsFrom(project.getConfigurations().create("mcloader"));
			project.getDependencies().add("compileOnly", "net.minecraft.client:client-deobf:");
			project.getDependencies().add("compileOnly", project.files((Object[]) new File(gameCache, "libraries").listFiles()));
			project.getDependencies().add("mcloader", project.files((Object[]) new File[] { new File(gameCache, "mcloaderapi.jar") }));
			project.getDependencies().add("mcloader", "org.spongepowered:mixin:0.8.5");
			project.getDependencies().add("mcloader", "org.spongepowered:mixin:0.8.5");
			project.getDependencies().add("mcloader", "com.google.code.gson:gson:2.2.4");
			project.getDependencies().add("mcloader", "com.google.guava:guava:21.0");
			project.getDependencies().add("mcloader", "org.ow2.asm:asm-tree:9.2");
			project.getDependencies().add("mcloader", "org.ow2.asm:asm-commons:9.2");
			project.getDependencies().add("mcloader", "org.ow2.asm:asm-util:9.2");
			
			// Export javadoc and sources jar
			project.getExtensions().getByType(JavaPluginExtension.class).withJavadocJar();
			project.getExtensions().getByType(JavaPluginExtension.class).withSourcesJar();
			
			// Add Task Hider after evaluation
			project.getAllTasks(true).forEach((_p2, taskSet) -> taskSet.forEach(task -> task.setGroup(null))); // after evaluate -> foreach project -> foreach task
			project.getTasksByName("build", true).forEach(t -> t.setGroup("mcloader"));
			
			// Create Launch files
			File launchFile = new File(project.getProjectDir(), "Minecraft.launch");
			if (!launchFile.exists()) {
				try {
					// Prepare Launch Files
					Files.write(launchFile.toPath(), Utils.createLaunchFile(project.getProjectDir(), new File(gameCache, "natives"), new File(gameCache, "assets"), config.getGroup().get() + "." + config.getId().get() + ".EntryPoint", new File(gameCache, "mixin-0.8.5.jar"), project.getConfigurations().getByName("mcloader").getAsFileTree()).getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// Add Extension
		project.getExtensions().add("mcloader", MCLoaderExtension.class);
	}

	/**
	 * Deobfuscates and decompiles the client jar
	 */
	@SuppressWarnings("deprecation") // toURL() is deprecated for some reason
	private void deobfuscateGame(Project p) {
		try {
			// Download mappings
			System.out.println("[MCLoader] Downloading Mappings");
			
			File mappings = new File(gameCache, config.getMcversion().get() + "-deobf.srg");
			Files.copy(new URL("https://backend.mgnet.work/mcp-srg/" + config.getMcversion().get() + "-deobf.srg").openStream(), mappings.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			// Deobfuscate
			System.out.println("[MCLoader] Deobfuscating Minecraft");
			
			File in_jar = new File(gameCache, "client.jar");
			File deobf_jar = new File(gameCache, "client-deobf.jar");
			File decomp_jar = new File(gameCache, "client");
			
			// Load Mappings
			JarMapping jarmapping = new JarMapping();
			jarmapping.loadMappings(mappings);
			
			VarNamesCollector.params = jarmapping.params;
			
			JarRemapper jarremapper = new JarRemapper(jarmapping);
			
			// Classpath for deobfuscation
			List<URL> url = new ArrayList<>();
			url.add(in_jar.toURL());
			for (File lib : new File(gameCache, "libraries").listFiles())  url.add(lib.toURL());

			URLClassLoader classLoader = null;
			try (Jar input = Jar.init(in_jar)) {
				// Enable dependencies
				JointProvider inheritanceProviders = new JointProvider();
				inheritanceProviders.add(new JarProvider(input));
				inheritanceProviders.add(new ClassLoaderProvider(classLoader = new URLClassLoader(url.toArray(new URL[url.size()]))));
				jarmapping.setFallbackInheritanceProvider(inheritanceProviders);
				
				// Remap Jar
				jarremapper.remapJar(input, deobf_jar);
			} finally {
				if (classLoader != null) classLoader.close();
			}
			
			// Prepare Decompiler
			System.out.println("[MCLoader] Decompiling Minecraft");
			
	        Map<String, Object> mapOptions = new HashMap<String, Object>();
	        mapOptions.put(IFernflowerPreferences.DECOMPILE_INNER, "1");
	        mapOptions.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1");
	        mapOptions.put(IFernflowerPreferences.ASCII_STRING_CHARACTERS, "1");
	        mapOptions.put(IFernflowerPreferences.THREADS, "" + Runtime.getRuntime().availableProcessors());
	        mapOptions.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1");
	        mapOptions.put(IFernflowerPreferences.REMOVE_SYNTHETIC, "1");
	        mapOptions.put(IFernflowerPreferences.REMOVE_BRIDGE, "1");
	        mapOptions.put(IFernflowerPreferences.LITERALS_AS_IS, "0");
	        mapOptions.put(IFernflowerPreferences.UNIT_TEST_MODE, "0");
	        mapOptions.put(IFernflowerPreferences.MAX_PROCESSING_METHOD, "0");
	        
	        // Create the Decompiler Wrapper
	        ConsoleDecompiler decompiler = new ConsoleDecompiler(decomp_jar, mapOptions, new PrintStreamLogger());
	        decompiler.addSource(deobf_jar);
	        for (File library : new File(gameCache, "libraries").listFiles()) {
	        	decompiler.addLibrary(library);
	        }
	        
			// Decompile
			decompiler.decompileContext();
			
			// Move Stuff around
			new File(decomp_jar, "client-deobf.jar").renameTo(new File(decomp_jar, "client-deobf-sources.jar"));
			deobf_jar.renameTo(new File(decomp_jar, "client-deobf.jar"));
		} catch (Exception e) {
			System.err.println("Unable to deobfuscate/decompile the client");
			e.printStackTrace();
		}
	}

	/**
	 * Downloads all necessary game files
	 */
	private void downloadGame() {
		try {
			// Download version manifest
			Gson gson = new Gson();
			MinecraftVersionManifest version_manifest = gson.fromJson(Utils.readAllBytesAsStringFromURL(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json")), MinecraftVersionManifest.class);
			
			// Find the json for the right version
			VersionJson version_json = null;
			for (Version version : version_manifest.versions) 
				if (config.getMcversion().get().equals(version.id)) 
					version_json = gson.fromJson(Utils.readAllBytesAsStringFromURL(new URL(version.url)), VersionJson.class);
			
			if (version_json == null)
				throw new Exception("Version not found");
			
			JsonDownloader.downloadDeps(gameCache, version_json);
			
			// Download Mixin
			Files.copy(new URL("https://repo.spongepowered.org/repository/maven-public/org/spongepowered/mixin/0.8.5/mixin-0.8.5-processor.jar").openStream(), new File(gameCache, "mixin-0.8.5-processor.jar").toPath());
			Files.copy(new URL("https://repo.spongepowered.org/repository/maven-public/org/spongepowered/mixin/0.8.5/mixin-0.8.5.jar").openStream(), new File(gameCache, "mixin-0.8.5.jar").toPath());
		} catch (Exception e) {
			System.err.println("Unable to download the game");
			e.printStackTrace();
		}
	}
	
}
