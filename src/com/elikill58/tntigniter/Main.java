package com.elikill58.tntigniter;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

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
		getServer().getPluginManager().registerEvents(this, this);
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

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		Block bp = e.getBlockPlaced();
		if (bp == null || isDisabledPlayer(e.getPlayer()))
			return;
		if (bp.getType().equals(Material.TNT) && isActive && (hasPerm(e.getPlayer()))
				&& ALLOWED_WORLD.contains(bp.getWorld().getName())) {
			bp.setType(Material.AIR);
			bp.getWorld().spawnEntity(bp.getLocation(), EntityType.PRIMED_TNT);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (isActive && p.getItemInHand().getType().equals(Material.TNT) && isDisabledPlayer(p)) {
			Entity et = p.getWorld().spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
			et.setVelocity(divide(p.getEyeLocation().subtract(p.getLocation()), 2).toVector());
		}
	}
	
	private boolean hasPerm(Player p) {
		String ignitePerm = getConfig().getString("ignite_perm");
		if (ignitePerm.equalsIgnoreCase("") || ignitePerm.equalsIgnoreCase(" "))
			return true;
		else if (p.isOp() || p.hasPermission(ignitePerm))
			return true;
		return false;
	}
	
	private Location divide(Location loc, int divide) {
		Location next = loc.clone();
		next.setX(loc.getX() / divide);
		next.setY(loc.getY() / divide);
		next.setZ(loc.getZ() / divide);
		return next;
	}
	
	public boolean isDisabledPlayer(Player p) {
		for(String s : DISABLED_PLAYER)
			if(p.getName().equalsIgnoreCase(s) || p.getDisplayName().equalsIgnoreCase(s) || p.getUniqueId().toString().equalsIgnoreCase(s))
				return true;
		return false;
	}
}
