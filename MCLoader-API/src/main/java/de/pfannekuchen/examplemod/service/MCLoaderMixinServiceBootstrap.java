package de.pfannekuchen.examplemod.service;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class MCLoaderMixinServiceBootstrap implements IMixinServiceBootstrap {

	@Override
	public String getName() {
		return "MCLauncher";
	}

	@Override
	public String getServiceClassName() {
		return "de.pfannekuchen.launcher.mixin.MCLauncherMixinService";
	}

	@Override
	public void bootstrap() {
		MCLoaderMixinClassLoader.instance.hashCode();
	}

}
