package de.pfannekuchen.examplemod.service;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class MCLoaderPropertyService implements IGlobalPropertyService {

	class Key implements IPropertyKey {

		private final String key;

		Key(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return this.key;
		}
	}
	
	public static Map<String,Object> blackboard = new HashMap<>();

	public MCLoaderPropertyService() {
		MCLoaderMixinClassLoader.instance.hashCode();
	}

	@Override
	public IPropertyKey resolveKey(String name) {
		return new Key(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <T> T getProperty(IPropertyKey key) {
		return (T)blackboard.get(key.toString());
	}

	@Override
	public final void setProperty(IPropertyKey key, Object value) {
		blackboard.put(key.toString(), value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <T> T getProperty(IPropertyKey key, T defaultValue) {
		Object value = blackboard.get(key.toString());
		return value != null ? (T)value : defaultValue;
	}

	@Override
	public final String getPropertyString(IPropertyKey key, String defaultValue) {
		Object value = blackboard.get(key.toString());
		return value != null ? value.toString() : defaultValue;
	}

}