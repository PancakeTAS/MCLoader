package de.pfannekuchen.mcloader;

import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import de.pfannekuchen.mcloader.tasks.LaunchTask;

public class LaunchTest {

	@Test
	public void runTest() {
		LaunchTask task = ProjectBuilder.builder().build().getTasks().create("launch", LaunchTask.class);
		Assert.assertFalse(task.run());
	}
	
}
