package com.elikill58.tntigniter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TnTIgniter extends JavaPlugin implements Listener {

	private static boolean active = true;
	public static boolean worldGuardSupport = false, launcherActive = true;
	public static Vector tntVector;
	public static String launchClick = "right", launchWith = "both";

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean active) {
		TnTIgniter.active = active;
	}

	public static final List<String> ALLOWED_WORLD = new ArrayList<>(), DISABLED_PLAYER = new ArrayList<>(),
			DISABLED_AREA = new ArrayList<>();

	@Override
	public void onEnable() {
		saveDefaultConfig();

		load(this);

		PluginCommand pluginCmd = getCommand("tntigniter");
		TntIgniterCommand tntCmd = new TntIgniterCommand(this);
		pluginCmd.setExecutor(tntCmd);
		pluginCmd.setTabCompleter(tntCmd);

		getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
	}

	public static void load(TnTIgniter pl) {
		ALLOWED_WORLD.clear();
		DISABLED_PLAYER.clear();
		DISABLED_AREA.clear();

		FileConfiguration config = pl.getConfig();
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
			worldGuardSupport = true;
			pl.getLogger().info("Loading support for WorldGuard.");
		}
		active = config.getBoolean("isActive");
		ALLOWED_WORLD.addAll(config.getStringList("allowed_world"));
		DISABLED_PLAYER.addAll(config.getStringList("disabled_player"));
		DISABLED_AREA.addAll(config.getStringList("disabled_area"));
		
		ConfigurationSection section = config.getConfigurationSection("launch");
		if(section == null) {
			pl.getLogger().warning("Cannot load TNT launcher feature because we cannot find the configuration for it.");
			launcherActive = false;
			return;
		}
		launchClick = section.getString("click", "right");
		launchWith = section.getString("with", "both");
		tntVector = new Vector(section.getDouble("vector.x", 0.0), section.getDouble("vector.y", 0.5), section.getDouble("vector.z", 0.0));
	}

	public void sendMessage(Player p, String key, Object... placeholders) {
		p.sendMessage(getMessage(key, placeholders));
	}

	public String getMessage(String key, Object... placeholders) {
		String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString(key));
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replace(String.valueOf(placeholders[index]), String.valueOf(placeholders[index + 1]));
		return message;
	}

	public String getMessage(String key) {
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key));
	}
}
