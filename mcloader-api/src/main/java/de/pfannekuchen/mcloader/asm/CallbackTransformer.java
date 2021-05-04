package de.pfannekuchen.mcloader.asm;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.javatuples.Triplet;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Callback Transformer transforms Classes so that they can run an attached Runnable.
 * @author Pancake
 */
public class CallbackTransformer implements ClassFileTransformer {
	
	/** List of Callbacks */
	public static LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable> callbacks = new LinkedHashMap<>();
	/** List of Runnables with index TODO: There's gotta be a better way to do this! */
	private static ArrayList<Runnable> references = new ArrayList<>();
	
	/** Class Pool for Javassist */
	private static final ClassPool cp;
	static {
		cp = ClassPool.getDefault(); 											// Obtain Default Class Pool
		cp.importPackage("de.pfannekuchen.mcloader.asm.CallbackTransformer");	// and import this Class into it.
	}
	
	/**
	 * Transform a Class to make it run an attached Runnable
	 */
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		/* Loop through every Callback and transform the correct ones */
		for (Entry<Triplet<Class<?>, String, Boolean>, Runnable> entry : callbacks.entrySet()) {
			if (entry.getKey().getValue0().equals(classBeingRedefined)) { // Check is looped callback is the currently redefined one.
				/* Try to retransform a Method and attach a Runnable to it */
				try {
					CtClass cc = cp.get(className); 															// Get the Class
					CtMethod cm = cc.getDeclaredMethod(entry.getKey().getValue1());								// and then the Method to attach
					references.add(entry.getValue()); // Put runnable into list to get index
					String lineOfCode = "CallbackTransformer.onCallback(" + (references.size() - 1) + ");";		// and attach this line of Code which executes the Callback Method
					if (entry.getKey().getValue2()) 															// to either 
						cm.insertBefore(lineOfCode);															// before
					else 																						// or
						cm.insertAfter(lineOfCode);																// after 
					classfileBuffer = cc.toBytecode();															// the Method and build the Class again.
					cc.detach(); // Detach Class to free Resources
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return classfileBuffer; // Return new Class-File
	}
	
	/**
	 * This is being called by the Transformed Method.
	 * 
	 * Run an attached Runnable
	 */
	public static void onCallback(int index) {
		references.get(index).run(); // Obtain and Run indexed Runnable
	}
	
}
