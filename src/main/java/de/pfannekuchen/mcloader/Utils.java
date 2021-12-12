package de.pfannekuchen.mcloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.pfannekuchen.launcher.exceptions.ConnectionException;

/**
 * Utils that make life easier
 * @author Pancake
 */
public class Utils {

	/**
	 * Reads all bytes into a string from a URL
	 * @param url URL to read from
	 * @return In message interpreted as string
	 */
	public static String readAllBytesAsStringFromURL(URL url) {
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 
	        return buffer.toString();
	    } catch (IOException e) {
			throw new ConnectionException("Failed downloading: " + url.toString(), e);
		}
	}
	
	/**
	 * Creates a launch file for Minecraft
	 * @return Launch File String
	 */
	public static String createLaunchFile(File projectDir, File nativesDir, File assetsDir, String mainClass, File mixin) {
		String run = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
				+ "<launchConfiguration type=\"org.eclipse.jdt.launching.localJavaApplication\">\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.MAIN_TYPE\" value=\"%MAIN%\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.MODULE_NAME\" value=\"%PROJECT%\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.PROJECT_ATTR\" value=\"%PROJECT%\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.PROGRAM_ARGUMENTS\" value=\"--uuid null --accessToken null --userProperties {} --userType mojang --version mcloader --assetsDir &quot;%ASSETS%&quot; --assetIndex 1.12\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.VM_ARGUMENTS\" value=\"-Djava.library.path=&quot;%NATIVES%&quot; -javaagent:&quot;%MIXIN%&quot;\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.WORKING_DIRECTORY\" value=\"${workspace_loc:%PROJECT%}/run\"/>\r\n"
				+ "</launchConfiguration>";
		return run.replaceAll("%PROJECT%", projectDir.getName())
				.replaceAll("%NATIVES%", nativesDir.getAbsolutePath().replace('\\', '/'))
				.replaceAll("%ASSETS%", assetsDir.getAbsolutePath().replace("\\", "/"))
				.replaceAll("%MIXIN%", mixin.getAbsolutePath().replace("\\", "/"))
				.replaceAll("%MAIN%", mainClass);
	}
	
}
