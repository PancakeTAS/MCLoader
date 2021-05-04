package de.pfannekuchen.mcloader.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.javatuples.Triplet;

import de.pfannekuchen.mcloader.Mod;
import de.pfannekuchen.mcloader.asm.CallbackTransformer;

public class JavaAgent {
	
	public static void agentmain(String agentArgs, Instrumentation inst) {
		// Call Client Entry Points
		try {
			Mod instance = (Mod) Class.forName("entry.EntryPoint").newInstance();
			instance.onInitialization(agentArgs, inst);
			CallbackTransformer.callbacks = instance.getCallbacks(new LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable>());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Callbacks couldn't be registered");
			e.printStackTrace();
		}
		
		// Transforming
		inst.addTransformer(new CallbackTransformer(), true);
		List<Class<?>> classes = new ArrayList<>();
		for (Triplet<Class<?>, String, Boolean> entry : CallbackTransformer.callbacks.keySet()) {
			if (!classes.contains(entry.getValue0())) {
				try {
					inst.retransformClasses(entry.getValue0());
				} catch (UnmodifiableClassException e) {
					e.printStackTrace();
				}
				classes.add(entry.getValue0());
			}
		}
	}
	
}
