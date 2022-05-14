package de.pfannekuchen.mcloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.gradle.api.file.FileTree;

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
	public static String createLaunchFile(File projectDir, File nativesDir, File assetsDir, String mainClass, File mixin, FileTree launchdeps) {
		String run = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
				+ "<launchConfiguration type=\"org.eclipse.jdt.launching.localJavaApplication\">\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.MAIN_TYPE\" value=\"%MAIN%\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.MODULE_NAME\" value=\"%PROJECT%\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.PROJECT_ATTR\" value=\"%PROJECT%\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.PROGRAM_ARGUMENTS\" value=\"--uuid null --accessToken null --userProperties {} --userType mojang --version mcloader --assetsDir &quot;%ASSETS%&quot; --assetIndex 1.12\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.VM_ARGUMENTS\" value=\"-Djava.library.path=&quot;%NATIVES%&quot; -javaagent:&quot;%MIXIN%&quot;\"/>\r\n"
				+ "    <stringAttribute key=\"org.eclipse.jdt.launching.WORKING_DIRECTORY\" value=\"${workspace_loc:%PROJECT%}/run\"/>\r\n"
				+ "    <booleanAttribute key=\"org.eclipse.jdt.launching.ATTR_USE_CLASSPATH_ONLY_JAR\" value=\"false\"/>\r\n"
				+ "    <listAttribute key=\"org.eclipse.jdt.launching.CLASSPATH\">\r\n"
				+ "        <listEntry value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;runtimeClasspathEntry containerPath=&quot;org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8/&quot; javaProject=&quot;%PROJECT%&quot; path=&quot;1&quot; type=&quot;4&quot;/&gt;&#13;&#10;\"/>\r\n"
				+ "        <listEntry value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;runtimeClasspathEntry path=&quot;3&quot; projectName=&quot;MCLoader-ExampleMod&quot; type=&quot;1&quot;/&gt;&#13;&#10;\"/>\r\n"
				+ "%CLASSPATH%"
				+ "    </listAttribute>\r\n"
				+ "    <booleanAttribute key=\"org.eclipse.jdt.launching.DEFAULT_CLASSPATH\" value=\"false\"/>"
				+ "</launchConfiguration>";
		
		String classpath = "";
		for (File lib : launchdeps)
			classpath += "        <listEntry value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;runtimeClasspathEntry externalArchive=&quot;%PATH%&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#13;&#10;\"/>\r\n".replace("%PATH%", lib.getAbsolutePath().replace("\\", "/"));
		return run.replaceAll("%PROJECT%", projectDir.getName())
				.replaceAll("%NATIVES%", nativesDir.getAbsolutePath().replace('\\', '/'))
				.replaceAll("%ASSETS%", assetsDir.getAbsolutePath().replace("\\", "/"))
				.replaceAll("%MIXIN%", mixin.getAbsolutePath().replace("\\", "/"))
				.replaceAll("%MAIN%", mainClass)
				.replaceAll("%CLASSPATH%", classpath);
	}
	
}
