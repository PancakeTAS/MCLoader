package de.pfannekuchen.mcloader;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * Main MCLoader Configuration Extension.
 * Holds User configured values.
 * @author Pancake
 */
public abstract class MCLoaderExtension {

	/**
	 * Id of the mod
	 * @return Mod Id
	 */
	public abstract Property<String> getId();
	
	/**
	 * Version of the mod
	 * @return Mod Version
	 */
	public abstract Property<String> getVersion();
	
	/**
	 * Name of the mod
	 * @return Mod Name
	 */
	public abstract Property<String> getName();
	
	/**
	 * Group of the mod
	 * @return Mod Group
	 */
	public abstract Property<String> getGroup();
	
	/**
	 * Description of the mod
	 * @return Mod Description
	 */
	public abstract Property<String> getDescription();
	
	/**
	 * Source of the mod
	 * @return Mod Source Code
	 */
	public abstract Property<String> getSource();
	
	/**
	 * Website of the mod
	 * @return Mod Website
	 */
	public abstract Property<String> getWebsite();
	
	/**
	 * Minecraft Version of the mod
	 * @return Minecraft Version
	 */
	public abstract Property<String> getMcversion();
	
	/**
	 * Authors of the mod
	 * @return Authors
	 */
	public abstract ListProperty<String> getAuthors();
	
}
