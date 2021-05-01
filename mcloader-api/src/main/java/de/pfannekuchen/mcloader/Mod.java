package de.pfannekuchen.mcloader;

import java.lang.instrument.Instrumentation;

public interface Mod {
	
	public void onInitialization(String agentArgs, Instrumentation inst);
	
}
