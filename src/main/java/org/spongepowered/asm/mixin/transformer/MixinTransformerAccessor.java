package org.spongepowered.asm.mixin.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import org.spongepowered.asm.mixin.MixinEnvironment;

import de.pfannekuchen.mcloader.ReflectionHelper;

/**
 * Transformer for Private Mixin Packages
 * @author Pancake
 */
public class MixinTransformerAccessor implements ClassFileTransformer {

	private MixinTransformer transformer;
	
	public MixinTransformerAccessor(String config) {
		try {
			transformer = (MixinTransformer) ReflectionHelper.getStaticInstance(ReflectionHelper.getDeclaredField("org.spongepowered.asm.mixin.transformer.Proxy", "transformer"));
			@SuppressWarnings("unchecked") List<MixinConfig> configs = (List<MixinConfig>) ReflectionHelper.getDeclaredField("org.spongepowered.asm.mixin.transformer.MixinTransformer", "configs").get(transformer);
			configs.add(MixinConfig.create(config, MixinEnvironment.getDefaultEnvironment()).get());
			configs.get(0).prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		return transformer.transformClassBytes(null, className, classfileBuffer);
	}
	
}
