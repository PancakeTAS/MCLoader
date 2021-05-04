package de.pfannekuchen.mcloader;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class MCLoaderApp {

	public static final String MODLOADERVERSION = "1343"; // 1343 is the DATA VERSION of Minecraft 1.12.2
	
	public static void main(String[] args) {
		boolean unsupportedJava = false;
		if (!System.getProperty("java.version").startsWith("1.8")) {
			if (JOptionPane.showConfirmDialog(null, "Your Java Version is not supported, try using Java 1.8.\n Do you want to try running this anyways?", "Unsupported Java Version!", JOptionPane.OK_CANCEL_OPTION) == 2) return;
			unsupportedJava = true;
		}
		try {
			VirtualMachine vm;
			for (VirtualMachineDescriptor vmc : VirtualMachine.list()) 			
				for (Entry<Object, Object> string : (vm = VirtualMachine.attach(vmc.id())).getSystemProperties().entrySet()) 
					if (string.getKey().toString().equals("user.dir")) 
						if (string.getValue().toString().contains("minecraft")) 
							if (identifyMinecraft(string.getValue().toString(), vm)) return;
		} catch (IOException | AttachNotSupportedException | AgentLoadException | AgentInitializationException e) {
			e.printStackTrace();
			if (unsupportedJava) {
				if (System.getProperty("os.name").toLowerCase().startsWith("win")) findJRE8();
				else JOptionPane.showConfirmDialog(null, "Please make sure that this Program and Minecraft run Java 1.8.", "Couldn't attach to JVM.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
			return; // Error
		} catch (ArrayIndexOutOfBoundsException e) {
			return; // Did not select a Mod
		}
	}
	
	private static File javaexe = null;
	private static void checkDir(final File programFilesFolder, final String javaDir, final String searchTerm) {
		if (new File(programFilesFolder, javaDir).exists()) {
			new File(programFilesFolder, javaDir).listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.toLowerCase().startsWith(searchTerm)) javaexe = new File(dir, name + "\\bin\\java.exe");
					return false;
				}
			});
		}
	}
	
	private static void findJRE8() {
		File programFilesFolder = new File("C:\\Program Files\\");
		File programFilesx86Folder = new File("C:\\Program Files (x86)\\");
		
		if (programFilesFolder.exists()) {
			checkDir(programFilesFolder, "AdoptOpenJDK", "jdk-8");
			checkDir(programFilesFolder, "Java", "jre1.8");
			checkDir(programFilesFolder, "Java", "jdk1.8");
			checkDir(programFilesFolder, "Java", "jdk8");
			checkDir(programFilesFolder, "Oracle\\Java", "jre1.8");
			checkDir(programFilesFolder, "Oracle\\Java", "jdk1.8");
			checkDir(programFilesFolder, "Oracle\\Java", "jdk8");
		}
		if (programFilesx86Folder.exists()) {
			checkDir(programFilesx86Folder, "AdoptOpenJDK", "jdk-8");
			checkDir(programFilesx86Folder, "Java", "jre1.8");
			checkDir(programFilesx86Folder, "Java", "jdk1.8");
			checkDir(programFilesx86Folder, "Java", "jdk8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jre1.8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jdk1.8");
			checkDir(programFilesx86Folder, "Oracle\\Java", "jdk8");
		}
		
		if (javaexe != null) {
			if (JOptionPane.showConfirmDialog(null, "The Program couldn't launch.\nWe were able to find Java 1.8 on your PC! Do you want to\n launch via Java 1.8?", "Couldn't attach to JVM.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE) == 0) {
				try {
					String jar = new File(MCLoaderApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
					Runtime.getRuntime().exec(new String[] {javaexe.getAbsolutePath(), "-jar", jar});
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			};
		} else {
			JOptionPane.showConfirmDialog(null, "The Program couldn't launch\nand we couldn't find Java 1.8 on your PC :(", "Couldn't attach to JVM.", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
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
