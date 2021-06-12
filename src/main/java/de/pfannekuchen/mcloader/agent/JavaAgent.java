package de.pfannekuchen.mcloader.agent;

import java.lang.instrument.Instrumentation;

import de.pfannekuchen.mcloader.MixinLoader;
import de.pfannekuchen.mcloader.Mod;

/**
 * Agent that is runs in Minecrafts Code
 * @author Pancake
 */
public class JavaAgent {
	
	/**
	 * premain() is being executed once the agent finished loading into the Minecraft Source-code
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		/* Run Mod Initialization */
		try {
			MixinLoader.init(inst); // Load Mixin
			((Mod) Class.forName("entry.EntryPoint").newInstance()).onInitialization(agentArgs, inst); // Create a new Instance from the EntryPoint of the Mod.
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
