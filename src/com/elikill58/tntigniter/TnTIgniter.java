package com.elikill58.tntigniter;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TnTIgniter extends JavaPlugin implements Listener {

	public static boolean isActive = true;
	public static List<String> ALLOWED_WORLD, DISABLED_PLAYER;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		isActive = getConfig().getBoolean("isActive");
		ALLOWED_WORLD = getConfig().getStringList("allowed_world");
		DISABLED_PLAYER = getConfig().getStringList("disabled_player");
		
		getCommand("tntigniter").setExecutor(new TntIgniterCommand(this));
		getCommand("tntigniter").setTabCompleter(new TntIgniterCommand(this));
		
		getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
	}

	public String getMessage(String key, String something, String replace, String ot, String otRp) {
		return getMessage(key).replaceAll(something, replace).replaceAll(ot, otRp);
	}

	public String getMessage(String key, String something, String replace) {
		return getMessage(key).replaceAll(something, replace);
	}

	public String getMessage(String key) {
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key));
	}
}
