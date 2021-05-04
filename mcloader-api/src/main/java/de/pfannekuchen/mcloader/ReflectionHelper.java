package de.pfannekuchen.mcloader;

import java.lang.reflect.Field;

/**
 * Helps Mod Developers in Reflection One-Liners by making every Field already accessible.
 * @author Pancake
 */
public final class ReflectionHelper {

	/**
	 * Obtains a static variable value
	 */
	public static final Object getStaticInstance(final Field field) throws IllegalArgumentException, IllegalAccessException {
		return field.get(null);
	}
	
	/**
	 * Returns a Private Field from a Class
	 */
	public static final Field getDeclaredField(final String clazz, final String field) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return makeAccessible(getClass(clazz).getDeclaredField(field));
	}
	
	/**
	 * Returns a Field from a Class
	 */
	public static final Field getField(final String clazz, final String field) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return makeAccessible(getClass(clazz).getField(field));
	}
	
	/**
	 * Makes any Field accessible and returns it again
	 */
	private static final Field makeAccessible(final Field field) {
		field.setAccessible(true);
		return field;
	}
	
	/**
	 * Exact same as Class.forName(). There is absolutely no need for this!
	 */
	public static final Class<?> getClass(final String clazz) throws ClassNotFoundException {
		return Class.forName(clazz);
	}
	
}
