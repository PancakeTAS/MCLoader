package de.pfannekuchen.mcloader.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.javatuples.Triplet;

import de.pfannekuchen.mcloader.Mod;
import de.pfannekuchen.mcloader.asm.CallbackTransformer;
import de.pfannekuchen.mcloader.asm.RedirectTransformer;

/**
 * Agent that is runs in Minecrafts Code
 * @author Pancake
 */
public class JavaAgent {
	
	/**
	 * agentmain() is being executed once the agent finished loading into the Minecraft Source-code
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {
		/* Run Mod Initialization */
		try {
			Mod instance = (Mod) Class.forName("entry.EntryPoint").newInstance(); // Create a new Instance from the EntryPoint of the Mod.
			instance.onInitialization(agentArgs, inst);
			CallbackTransformer.callbacks = instance.getCallbacks(new LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable>()); // Get all Callbacks for the Mod
			RedirectTransformer.redirects = instance.getRedirects(new LinkedHashMap<Triplet<Class<?>, String, String>, String>()); // Get all Redirects for the Mod
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Callbacks/Redirects couldn't be registered");
			e.printStackTrace();
		}
		
		/* Register Callbacks/Redirects using Transformer */
		inst.addTransformer(new CallbackTransformer(), true); // Add a new CallbackTransformer
		inst.addTransformer(new RedirectTransformer(), true); // Add a new RedirectTransformer
		List<Class<?>> classes = new ArrayList<>();
		for (Triplet<Class<?>, String, Boolean> entry : CallbackTransformer.callbacks.keySet()) { 	// Visit all Callback Classes
			if (!classes.contains(entry.getValue0())) {												// and if they are not already retransformed
				try {																				// then
					inst.retransformClasses(entry.getValue0());										// retransform them
				} catch (UnmodifiableClassException e) {
					e.printStackTrace();
				}
				classes.add(entry.getValue0());														// and add them to the already-retransformed classes list.
			}
		}
		for (Triplet<Class<?>, String, String> entry : RedirectTransformer.redirects.keySet()) { 	// Visit all Redirect Classes
			if (!classes.contains(entry.getValue0())) {												// and if they are not already retransformed
				try {																				// then
					inst.retransformClasses(entry.getValue0());										// retransform them
				} catch (UnmodifiableClassException e) {
					e.printStackTrace();
				}
				classes.add(entry.getValue0());														// and add them to the already-retransformed classes list.
			}
		}
	}
	
}
