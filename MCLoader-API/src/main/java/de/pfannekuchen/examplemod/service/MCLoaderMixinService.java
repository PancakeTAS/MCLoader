package de.pfannekuchen.examplemod.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;
import org.spongepowered.asm.transformers.MixinClassReader;

public class MCLoaderMixinService extends MixinServiceAbstract implements IClassProvider, IClassBytecodeProvider {
	
	ContainerHandleVirtual container = new ContainerHandleVirtual(this.getName());
	
	@Override 
	public String getName() { 
		return "MCLauncher"; 
	}
	
	@Override 
	public CompatibilityLevel getMinCompatibilityLevel() { 
		return CompatibilityLevel.JAVA_8; 
	}
	
	@Override 
	public CompatibilityLevel getMaxCompatibilityLevel() { 
		return CompatibilityLevel.JAVA_17; 
	}
	
	@Override
	public Collection<String> getPlatformAgents() { 
		return Collections.emptyList(); 
	}
	
	@Override 
	public boolean isValid() {
		return true; 
	}
	
	@Override 
	public InputStream getResourceAsStream(String name) { 
		return MCLoaderMixinClassLoader.instance.getResourceAsStream(name); 
	}
	
	@Override
	public IClassProvider getClassProvider() {
		return this;
	}
	
	@Override
	public IClassBytecodeProvider getBytecodeProvider() {
		return this;
	}
	
	@Override
	public IContainerHandle getPrimaryContainer() {
		return container;
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return Class.forName(name, true, MCLoaderMixinClassLoader.instance);
	}
	
	@Override
	public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, MCLoaderMixinClassLoader.instance);
	}
	
	@Override
	public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, MCLoaderMixinClassLoader.instance);
	}
	
	@Override
	public ITransformerProvider getTransformerProvider() {
		return null;
	}
	
	@Override
	public IClassTracker getClassTracker() {
		return null;
	}
	
	@Override
	public IMixinAuditTrail getAuditTrail() {
		return null;
	}
	
	@Override
	public URL[] getClassPath() { 
		return MCLoaderMixinClassLoader.instance.getClassPath(); 
	}
	
	@Override
	public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new MixinClassReader(MCLoaderMixinClassLoader.instance.readClassBytes(name), name);
        classReader.accept(classNode, 0);
        return classNode;
	}
	
	@Override
	public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
		return this.getClassNode(name);
	}
	
	@Override
	public void offer(IMixinInternal internal) {
		if (internal instanceof IMixinTransformerFactory) {
			MCLoaderMixinClassLoader.transformer = ((IMixinTransformerFactory) internal).createTransformer();
		}
		super.offer(internal);
	}
	
}
