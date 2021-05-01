package de.pfannekuchen.mcloader;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class MCLoaderApp {

	public static final String MODLOADERVERSION = "1343"; // 1343 is the DATA VERSION of Minecraft 1.12.2
	
	public static void main(String[] args) {
		try {
			VirtualMachine vm;
			for (VirtualMachineDescriptor vmc : VirtualMachine.list()) 			
				for (Entry<Object, Object> string : (vm = VirtualMachine.attach(vmc.id())).getSystemProperties().entrySet()) 
					if (string.getKey().toString().equals("user.dir")) 
						if (string.getValue().toString().contains("minecraft")) 
							if (identifyMinecraft(string.getValue().toString(), vm)) return;
		} catch (IOException | AttachNotSupportedException | AgentLoadException | AgentInitializationException e) {
			e.printStackTrace();
			return; // Error
		}
		return; // No MC Found
	}

	private static boolean identifyMinecraft(String user_dir, VirtualMachine vm) throws FileNotFoundException, IOException, AgentLoadException, AgentInitializationException {
		File minecraftFolder = new File(user_dir);
		Properties props = new Properties();
		props.load(new FileReader(new File(minecraftFolder, "options.txt")));
		if (props.getProperty("version", "-1").equals(MCLoaderApp.MODLOADERVERSION)) {
			FileDialog dialog = new FileDialog((Frame) null, "Choose a mod to load", FileDialog.LOAD);
			dialog.setVisible(true);
			vm.loadAgent(dialog.getFiles()[0].getAbsolutePath());
			vm.detach();
			return true;
		}
		return false;
	}

}
