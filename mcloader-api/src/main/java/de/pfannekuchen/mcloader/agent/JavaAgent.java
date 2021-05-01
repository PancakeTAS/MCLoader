package de.pfannekuchen.mcloader.agent;

import java.lang.instrument.Instrumentation;

import de.pfannekuchen.mcloader.Mod;

public class JavaAgent {
	
	public static void agentmain(String agentArgs, Instrumentation inst) {
		try {
			((Mod) Class.forName("EntryPoint").newInstance()).onInitialization(agentArgs, inst);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
