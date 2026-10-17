package me.pajic.accessorify;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.pajic.accessorify.config.ModClientConfig;
import net.minecraft.resources.Identifier;

public class AccessorifyClient {
	public static final Identifier CONFIG_RL = Accessorify.id("client_config");
	public static ModClientConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModClientConfig::new, RegisterType.CLIENT);

	public static void init() {}
}
