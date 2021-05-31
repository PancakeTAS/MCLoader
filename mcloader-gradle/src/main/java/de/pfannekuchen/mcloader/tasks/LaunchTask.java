package de.pfannekuchen.mcloader.tasks;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * Task that tries to find Minecraft and attach an agent to it.
 * @author Pancake
 */
public class LaunchTask extends DefaultTask {
	
	@TaskAction
	public boolean run() {
		/* Try to find Minecraft and load agent to it */
		try {
			VirtualMachine vm;
			for (VirtualMachineDescriptor vmc : VirtualMachine.list()) 															// Go through all running Java Instances		
				for (Entry<Object, Object> string : (vm = VirtualMachine.attach(vmc.id())).getSystemProperties().entrySet()) 	// and check their System Properties,
					if (string.getKey().toString().equals("user.dir")) 															// searching for a key called "user.dir" and
						if (string.getValue().toString().contains("minecraft")) 												// check if that contains the .minecraft Folder. (Only minecraft on OSX).
							if (identifyMinecraft(string.getValue().toString(), vm)) return true;								// If so, pass that to identifyMinecraft().
		} catch (IOException | AttachNotSupportedException | AgentLoadException | AgentInitializationException e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException("No Minecraft running!"); // Throw an Exception if no Minecraft Instance was found.
	}

	/**
	 * Try to see if options.txt gives us the wanted Version.
	 */
	private boolean identifyMinecraft(String user_dir, VirtualMachine vm) throws FileNotFoundException, IOException, AgentLoadException, AgentInitializationException {
		FileDialog dialog = new FileDialog((Frame) null, "Choose a mod to load", FileDialog.LOAD);	// Then, if it's the correct one, open a File Picker.
		/* Ask the User to select the Mod File and load it */
		dialog.setVisible(true);
		vm.loadAgent(dialog.getFiles()[0].getAbsolutePath()); // Load Agent to JVM
		vm.detach();
		return true;
	}
	
}
