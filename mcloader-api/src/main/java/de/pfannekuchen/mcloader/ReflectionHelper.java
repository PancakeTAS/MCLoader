package de.pfannekuchen.mcloader;

import java.lang.reflect.Field;

public final class ReflectionHelper {

	public static final Object getStaticInstance(final Field field) throws IllegalArgumentException, IllegalAccessException {
		return field.get(null);
	}
	
	public static final Field getDeclaredField(final String clazz, final String field) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return makeAccessible(getClass(clazz).getDeclaredField(field));
	}
	
	public static final Field getField(final String clazz, final String field) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return makeAccessible(getClass(clazz).getField(field));
	}
	
	private static final Field makeAccessible(final Field field) {
		field.setAccessible(true);
		return field;
	}
	
	public static final Class<?> getClass(final String clazz) throws ClassNotFoundException {
		return Class.forName(clazz);
	}
	
}
