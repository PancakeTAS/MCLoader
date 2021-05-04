package de.pfannekuchen.mcloader;

import java.lang.instrument.Instrumentation;
import java.util.LinkedHashMap;

import org.javatuples.Triplet;

public interface Mod {
	
	public void onInitialization(String agentArgs, Instrumentation inst);
	default LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable> getCallbacks(LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable> callbacks) throws Exception {
		return callbacks;
	}
	
}
