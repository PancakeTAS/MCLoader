package de.pfannekuchen.mcloader.asm;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.javatuples.Triplet;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class RedirectTransformer implements ClassFileTransformer {
	
	/** List of Callbacks */
	public static LinkedHashMap<Triplet<Class<?>, String, String>, String> redirects = new LinkedHashMap<>();
	
	/** Class Pool for Javassist */
	private static final ClassPool cp = ClassPool.getDefault();
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		/* Loop through every Redirect and transform the correct ones */
		for (Entry<Triplet<Class<?>, String, String>, String> entry : redirects.entrySet()) {
			if (entry.getKey().getValue0().equals(classBeingRedefined)) { // Check is looped redirect is the currently redefined one.
				/* Try to retransform a Method and redirect a call from it */
				try {
					CtClass cc = cp.get(className); 															// Get the Class
					CtMethod cm = cc.getDeclaredMethod(entry.getKey().getValue1());								// and then the Method to redirect in
					cm.instrument(new ExprEditor() {
						@Override
						public void edit(MethodCall m) throws CannotCompileException {
							if (m.getClassName().equalsIgnoreCase(entry.getKey().getValue2().split("#")[0]) && m.getMethodName().equalsIgnoreCase(entry.getKey().getValue2().split("#")[1])) {
								cp.importPackage(entry.getValue().split("#")[0].replaceFirst("\\+", "\\."));
								m.replace(entry.getValue().split("\\+")[1].replaceFirst("#", "\\.") + ";");
							}
							super.edit(m);
						}
					});
					classfileBuffer = cc.toBytecode();															// the Method and build the Class again.
					cc.detach(); // Detach Class to free Resources
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return classfileBuffer; // Return new Class-File
	}

}
