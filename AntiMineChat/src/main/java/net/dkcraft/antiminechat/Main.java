package net.dkcraft.antiminechat;

import net.dkcraft.antiminechat.util.Listeners;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public void loadConfiguration() {
		this.getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}

	public void onEnable() {
		
		this.getServer().getPluginManager().registerEvents(new Listeners(this), this);

		loadConfiguration();
		reloadConfig();
	}
	
	public void onDisable() {
	}
}
