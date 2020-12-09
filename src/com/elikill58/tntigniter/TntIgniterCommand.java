package com.elikill58.tntigniter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TntIgniterCommand implements CommandExecutor, TabCompleter {

	private TnTIgniter pl;

	public TntIgniterCommand(TnTIgniter pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (!(p.hasPermission("tntigniter.edit") || p.isOp())) {
			pl.sendMessage(p, "not_permission");
			return false;
		}
		if (arg.length == 0) {
			TnTIgniter.setActive(!TnTIgniter.isActive());
			pl.getConfig().set("isActive", TnTIgniter.isActive());
			pl.saveConfig();
			pl.sendMessage(p, TnTIgniter.isActive() ? "active_enable" : "active_disable");
		} else {
			if (arg[0].equalsIgnoreCase("reload")) {
				TnTIgniter.load(pl);
				pl.sendMessage(p, "reloaded");
			} else if (arg[0].equalsIgnoreCase("enable")) {
				if (TnTIgniter.isActive())
					pl.sendMessage(p, "already_enabled");
				else {
					TnTIgniter.setActive(true);
					pl.sendMessage(p, "active_enable");
				}
			} else if (arg[0].equalsIgnoreCase("disable")) {
				if (!TnTIgniter.isActive())
					pl.sendMessage(p, "already_disabled");
				else {
					TnTIgniter.setActive(false);
					pl.sendMessage(p, "active_disable");
				}
			} else if (arg[0].equalsIgnoreCase("world")) {
				if (arg.length == 1) {
					pl.sendMessage(p, "world_allowed", "%world%",
							TnTIgniter.ALLOWED_WORLD.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
				} else {
					World world = Bukkit.getWorld(arg[1]);
					if (world == null) {
						pl.sendMessage(p, "unknow_world", "%arg%", arg[1]);
						return false;
					}
					boolean state = true;
					if (TnTIgniter.ALLOWED_WORLD.contains(world.getName())) {
						TnTIgniter.ALLOWED_WORLD.remove(world.getName());
						state = false;
					} else
						TnTIgniter.ALLOWED_WORLD.add(world.getName());
					pl.getConfig().set("allowed_world", TnTIgniter.ALLOWED_WORLD);
					pl.saveConfig();
					pl.sendMessage(p, "world_state", "%state%", pl.getMessage("state_" + (state ? "added" : "removed")),
							"%world%", world.getName());
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> tab = new ArrayList<>();
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase();
		if (arg.length == 1 || (arg.length == 2
				&& (arg[0].equalsIgnoreCase(prefix) || prefix.isEmpty() || prefix.equalsIgnoreCase(""))))
			if (arg[0].equalsIgnoreCase("world"))
				for (World w : Bukkit.getWorlds())
					if (w.getName().startsWith(prefix) || prefix.isEmpty() || prefix.equalsIgnoreCase(""))
						tab.add(w.getName());
		return tab;
	}
}
