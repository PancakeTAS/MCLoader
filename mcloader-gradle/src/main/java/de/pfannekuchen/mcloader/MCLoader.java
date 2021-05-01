package de.pfannekuchen.mcloader;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import de.pfannekuchen.mcloader.tasks.LaunchTask;

public class MCLoader implements Plugin<Project> {

	public static final String MODLOADERVERSION = "1343"; // 1343 is the DATA VERSION of Minecraft 1.12.2
	public static File rootDir;
	
	@Override
	public void apply(Project project) {
		rootDir = project.getProjectDir();
		
		Task t = project.getTasks().create("launch", LaunchTask.class);
		t.setGroup("mcloader");
		t.dependsOn("jar");
	}

}
