package de.pfannekuchen.mcloader;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * This is the standalone Application that is being ran by end-users to apply their Mod to Unmodified Vanilla Minecraft
 * @author Pancake
 */
public class MCLoaderApp {

	/**
	 * Main-Method
	 * 
	 * Try to find Minecraft and attach the Mod.
	 */
	public static void main(String[] args) throws IOException, URISyntaxException, ArrayIndexOutOfBoundsException {
		
		/* Check Java Version to be Java 1.8 */
		boolean unsupportedJava = false;
		if (!System.getProperty("java.version").startsWith("1.8")) {
			// Display a Warning when using the wrong Version of Java
			if (JOptionPane.showConfirmDialog(null, "Your Java Version is not supported, try using Java 1.8.\n Do you want to try running this anyways?", "Unsupported Java Version!", JOptionPane.OK_CANCEL_OPTION) == 2) 
				return; // Return if the User selected Cancel
			unsupportedJava = true;
		}
		
		/* Try to find identify Minecraft VMs */
		try {
			VirtualMachine vm;
			for (VirtualMachineDescriptor vmc : VirtualMachine.list()) 															// Go through all running Java Instances		
				for (Entry<Object, Object> string : (vm = VirtualMachine.attach(vmc.id())).getSystemProperties().entrySet()) 	// and check their System Properties,
					if (string.getKey().toString().equals("user.dir")) 															// searching for a key called "user.dir" and
						if (string.getValue().toString().contains("minecraft")) 												// check if that contains the .minecraft Folder. (Only minecraft on OSX).
							if (identifyMinecraft(string.getValue().toString(), vm)) return;									// If so, pass that to identifyMinecraft().
		} catch (IOException | AttachNotSupportedException | AgentLoadException | AgentInitializationException e) {
			/* Something went wrong */
			e.printStackTrace();
			if (unsupportedJava) { // User is using an unsupported version of Java
				/* If the User is on Windows, try to find a supported Java Installation. Or Else display an Error Messsage */
				if (System.getProperty("os.name").toLowerCase().startsWith("win")) findJRE8();
				else JOptionPane.showConfirmDialog(null, "Please make sure that this Program and Minecraft run Java 1.8.", "Couldn't attach to JVM.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Try to see if options.txt gives us the wanted Version.
	 */
	private static boolean identifyMinecraft(String user_dir, VirtualMachine vm) throws FileNotFoundException, IOException, AgentLoadException, AgentInitializationException {
		FileDialog dialog = new FileDialog((Frame) null, "Choose a mod to load", FileDialog.LOAD);	// Then, if it's the correct one, open a File Picker.
		/* Ask the User to select the Mod File and load it */
		dialog.setVisible(true);
		vm.loadAgent(dialog.getFiles()[0].getAbsolutePath()); // Load Agent to JVM
		vm.detach();
		return true;
	}
	
	/** Detected Java 1.8 java.exe from {@link #findJRE8()} */
	private static File javaexe = null;
	
	/**
	 * Go through known Java Installation Locations and try to find Java 1.8
	 */
	private static void findJRE8() throws IOException, URISyntaxException {
		File programFilesFolder = new File("C:\\Program Files\\");
		File programFilesx86Folder = new File("C:\\Program Files (x86)\\");
		
		if (programFilesFolder.exists()) {
			// Check all known Dirs
			checkDir(programFilesFolder, "AdoptOpenJDK", "jdk-8");
			checkDir(programFilesFolder, "Java", "jre1.8");
			checkDir(programFilesFolder, "Java", "jdk1.8");
			checkDir(programFilesFolder, "Java", "jdk8");
			checkDir(programFilesFolder, "Oracle\\Java", "jre1.8");
			checkDir(programFilesFolder, "Oracle\\Java", "jdk1.8");
			checkDir(programFilesFolder, "Oracle\\Java", "jdk8");
		}
		if (programFilesx86Folder.exists()) {
			// Check all known Dirs in the 32-bit Program Files Folder
			checkDir(programFilesx86Folder, "AdoptOpenJDK", "jdk-8");
			checkDir(programFilesx86Folder, "Java", "jre1.8");
			checkDir(programFilesx86Folder, "Java", "jdk1.8");
			checkDir(programFilesx86Folder, "Java", "jdk8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jre1.8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jdk1.8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jdk8");
		}
		
		/* Check whether search was successful */
		if (javaexe != null) 
			/* Ask the User to relaunch automatically with the supported Java Installation */
			if (JOptionPane.showConfirmDialog(null, "The Program couldn't launch.\nWe were able to find Java 1.8 on your PC! Do you want to\n launch via Java 1.8?", "Couldn't attach to JVM.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE) == 0)
				/* The User agreed to relaunch using Java 1.8 now try to launch with Java 1.8  */
				Runtime.getRuntime().exec(new String[] {javaexe.getAbsolutePath(), "-jar", new File(MCLoaderApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath()}); // Run current JAR-File with the found 1.8 JVM
		else 
			/* Display an Error Message that the Program didn't work with the unsupported Java version, and the Program couldn't find one installed on the PC */
			JOptionPane.showConfirmDialog(null, "The Program couldn't launch\nand we couldn't find Java 1.8 on your PC :(", "Couldn't attach to JVM.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		
	}
	
	/**
	 * Check a given Directory for Java 1.8 using a search term
	 */
	private static void checkDir(final File programFilesFolder, final String javaDir, final String searchTerm) {
		if (new File(programFilesFolder, javaDir).exists()) 
			new File(programFilesFolder, javaDir).listFiles((dir, name) -> { 										// Go through all Files and
				if (name.toLowerCase().startsWith(searchTerm)) javaexe = new File(dir, name + "\\bin\\java.exe");	// check if it starts with the searchTerm
				return false;
			});
	}

}
