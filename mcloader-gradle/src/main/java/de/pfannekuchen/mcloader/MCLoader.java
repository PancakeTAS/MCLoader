package de.pfannekuchen.mcloader;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import de.pfannekuchen.mcloader.tasks.LaunchTask;

/**
 * Gradle Plugin Class that loads a Task
 * @author Pancake
 */
public class MCLoader implements Plugin<Project> {

	/** Current Directory where Gradle Task is being executed from. */
	public static File rootDir;
	
	@Override
	public void apply(Project project) {
		rootDir = project.getProjectDir(); // Get Project Dir for LaunchTask
		
		/* Create launch Task */
		Task t = project.getTasks().create("launch", LaunchTask.class); // Create a new Task executing LaunchTask.class
		t.setGroup("mcloader");											// Set Group of 'launch' Task and
		t.dependsOn("jar");												// make sure 'jar' is being executed before that
	}

}
