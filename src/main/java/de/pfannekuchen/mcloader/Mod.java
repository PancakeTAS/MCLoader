package de.pfannekuchen.mcloader;

import java.lang.instrument.Instrumentation;

/**
 * Interface to access Mod without direct access
 * @author Pancake
 */
public interface Mod {
	
	/**
	 * Runs when the Agent is being attached
	 */
	public void onInitialization(String agentArgs, Instrumentation inst);
}
