package de.pfannekuchen.mcloader;

import java.lang.instrument.Instrumentation;
import java.util.LinkedHashMap;

import org.javatuples.Triplet;

/**
 * Interface to access Mod without direct access
 * @author Pancake
 */
public interface Mod {
	
	/**
	 * Runs when the Agent is being attached
	 */
	public void onInitialization(String agentArgs, Instrumentation inst);
	
	/**
	 * List of Callbacks to Transform
	 */
	default LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable> getCallbacks(LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable> callbacks) throws Exception {
		return callbacks;
	}
	
}
