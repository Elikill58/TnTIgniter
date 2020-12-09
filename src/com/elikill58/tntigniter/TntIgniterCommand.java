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

public class TntIgniterCommand  implements CommandExecutor, TabCompleter {

	private Main pl;

	public TntIgniterCommand(Main pl) {
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (p.hasPermission("tntigniter.edit") || p.isOp()) {
			if (arg.length == 0) {
				Main.isActive = !Main.isActive;
				pl.getConfig().set("isActive", Main.isActive);
				pl.saveConfig();
				if (Main.isActive)
					p.sendMessage(pl.getMessage("active_enable"));
				else
					p.sendMessage(pl.getMessage("active_disable"));
			} else {
				if (arg[0].equalsIgnoreCase("enable")) {
					if (Main.isActive)
						p.sendMessage(pl.getMessage("already_enabled"));
					else {
						Main.isActive = true;
						p.sendMessage(pl.getMessage("active_enable"));
					}
				} else if (arg[0].equalsIgnoreCase("disable")) {
					if (!Main.isActive)
						p.sendMessage(pl.getMessage("already_disabled"));
					else {
						Main.isActive = false;
						p.sendMessage(pl.getMessage("active_disable"));
					}
				} else if (arg[0].equalsIgnoreCase("world")) {
					if (arg.length == 1) {
						p.sendMessage(pl.getMessage("world_allowed", "%world%",
								Main.ALLOWED_WORLD.toString().replaceAll("\\[", "").replaceAll("\\]", "")));
					} else {
						World world = Bukkit.getWorld(arg[1]);
						if (world == null) {
							p.sendMessage(pl.getMessage("unknow_world", "%arg%", arg[1]));
							return false;
						}
						boolean state = true;
						if (Main.ALLOWED_WORLD.contains(world.getName())) {
							Main.ALLOWED_WORLD.remove(world.getName());
							state = false;
						} else
							Main.ALLOWED_WORLD.add(world.getName());
						pl.getConfig().set("allowed_world", Main.ALLOWED_WORLD);
						pl.saveConfig();
						p.sendMessage(pl.getMessage("world_state", "%state%",
								pl.getMessage("state_" + (state ? "added" : "removed")), "%world%",
								world.getName()));
					}
				}
			}
		} else
			p.sendMessage(pl.getMessage("not_permission"));
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> tab = new ArrayList<>();
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase();
		if (arg.length == 1 || (arg.length == 2 && (arg[0].equalsIgnoreCase(prefix) || prefix.isEmpty() || prefix.equalsIgnoreCase(""))))
			if(arg[0].equalsIgnoreCase("world"))
				for(World w : Bukkit.getWorlds())
					if(w.getName().startsWith(prefix) || prefix.isEmpty() || prefix.equalsIgnoreCase(""))
						tab.add(w.getName());
		return tab;
	}
}
