package de.pfannekuchen.mcloader.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import de.pfannekuchen.mcloader.MCLoader;

public class LaunchTask extends DefaultTask {
	
	@TaskAction
	public boolean run() {
		try {
			VirtualMachine vm;
			for (VirtualMachineDescriptor vmc : VirtualMachine.list()) 			
				for (Entry<Object, Object> string : (vm = VirtualMachine.attach(vmc.id())).getSystemProperties().entrySet()) 
					if (string.getKey().toString().equals("user.dir")) 
						if (string.getValue().toString().contains("minecraft")) 
							if (identifyMinecraft(string.getValue().toString(), vm)) return true;
		} catch (IOException | AttachNotSupportedException | AgentLoadException | AgentInitializationException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean identifyMinecraft(String user_dir, VirtualMachine vm) throws FileNotFoundException, IOException, AgentLoadException, AgentInitializationException {
		File minecraftFolder = new File(user_dir);
		Properties props = new Properties();
		props.load(new FileReader(new File(minecraftFolder, "options.txt")));
		if (props.getProperty("version", "-1").equals(MCLoader.MODLOADERVERSION)) {
			vm.loadAgent(new File(MCLoader.rootDir, "mod.jar").getAbsolutePath());
			vm.detach();
			return true;
		}
		return false;
	}
	
}
