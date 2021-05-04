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

public class CallbackTransformer implements ClassFileTransformer {
	
	public static LinkedHashMap<Triplet<Class<?>, String, Boolean>, Runnable> callbacks = new LinkedHashMap<>();
	private static ArrayList<Runnable> references = new ArrayList<>();
	
	private static final ClassPool cp;
	static {
		cp = ClassPool.getDefault();
		cp.importPackage("de.pfannekuchen.mcloader.asm.CallbackTransformer");
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		for (Entry<Triplet<Class<?>, String, Boolean>, Runnable> entry : callbacks.entrySet()) {
			if (entry.getKey().getValue0().equals(classBeingRedefined)) {
				try {
					CtClass cc = cp.get(className);
					CtMethod cm = cc.getDeclaredMethod(entry.getKey().getValue1());
					references.add(entry.getValue());
					String lineOfCode = "CallbackTransformer.onCallback(" + (references.size() - 1) + ");";
					if (entry.getKey().getValue2()) 
						cm.insertBefore(lineOfCode);
					else 
						cm.insertAfter(lineOfCode);
					classfileBuffer = cc.toBytecode();
					cc.detach();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return classfileBuffer;
	}

	public static void onCallback(int index) {
		references.get(index).run();
	}
	
}
