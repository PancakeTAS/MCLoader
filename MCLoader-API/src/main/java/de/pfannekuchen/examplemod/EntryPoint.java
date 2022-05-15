package de.pfannekuchen.examplemod;

import java.io.File;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import de.pfannekuchen.examplemod.service.MCLoaderMixinClassLoader;

/**
 * Entry Point of the MCLoader Mod. Prepares Mixin
 * @author Pancake
 */
public class EntryPoint {
	
	/**
	 * Main Class that loads Minecraft after loading Mixin
	 * @param args Arguments Minecraft is being launched with
	 * @throws Exception Security Manager
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		String assetsDir = "";
		for (int i = 0; i < args.length; i++)
			if ("--assetsDir".equals(args[i]))
				assetsDir = args[i+1];
		final MCLoaderMixinClassLoader classloader = new MCLoaderMixinClassLoader(new File(new File(assetsDir).getParentFile(), "client/client-deobf.jar"), new File(new File(assetsDir).getParentFile(), "libraries"));
		MixinBootstrap.init();
		MixinEnvironment.getCurrentEnvironment().addConfiguration("mcloader.mixin.json");
		MixinEnvironment.getCurrentEnvironment().setSide(Side.CLIENT);
		
		Class.forName(MixinBootstrap.getPlatform().getLaunchTarget(), true, classloader).getMethod("main", String[].class).invoke(null, new Object[] {args}); // Load Minecraft with custom classloader
	}
	
}