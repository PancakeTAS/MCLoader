package de.pfannekuchen.mcloader;

import java.lang.instrument.Instrumentation;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Option;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;
import org.spongepowered.asm.mixin.transformer.MixinTransformerAccessor;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class MixinLoader {

	public static void init(Instrumentation instrumentation) {
		// Initialize Legacy Launch Wrapper even when using the newer Launcher
		final URLClassLoader ucl = (URLClassLoader) Launch.class.getClassLoader();
        Launch.classLoader = new LaunchClassLoader(ucl.getURLs());
        Launch.blackboard = new HashMap<String,Object>();
        Thread.currentThread().setContextClassLoader(Launch.classLoader);
        Launch.blackboard.put("Tweaks", new ArrayList<ITweaker>(1));
        Launch.blackboard.put("ArgumentList", new ArrayList<String>());
        Launch.blackboard.put("TweakClasses", new ArrayList<String>());
        // Load Mixin
        MixinBootstrap.init();
		MixinEnvironment.getDefaultEnvironment().setSide(Side.CLIENT);
		MixinEnvironment.getDefaultEnvironment().setOption(Option.DISABLE_REFMAP, true);
		// Configure Mixin and Transformer
		instrumentation.addTransformer(new MixinTransformerAccessor("mcloader.mixin.json"), true);
	}
	
}
