package com.elikill58.tntigniter;

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

public class BlockEvents implements Listener {
	
	private final TnTIgniter pl;
	
	public BlockEvents(TnTIgniter pl) {
		this.pl = pl;
	}
	
	private boolean hasPerm(Player p) {
		String ignitePerm = pl.getConfig().getString("ignite_perm");
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
		for(String s : TnTIgniter.DISABLED_PLAYER)
			if(p.getName().equalsIgnoreCase(s) || p.getDisplayName().equalsIgnoreCase(s) || p.getUniqueId().toString().equalsIgnoreCase(s))
				return true;
		return false;
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		Block bp = e.getBlockPlaced();
		if (bp == null || !TnTIgniter.isActive() || isDisabledPlayer(e.getPlayer()))
			return;
		if (bp.getType().equals(Material.TNT) && (hasPerm(e.getPlayer()))
				&& TnTIgniter.ALLOWED_WORLD.contains(bp.getWorld().getName())) {
			bp.setType(Material.AIR);
			bp.getWorld().spawnEntity(bp.getLocation(), EntityType.PRIMED_TNT);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (TnTIgniter.isActive() && p.getItemInHand().getType().equals(Material.TNT) && isDisabledPlayer(p)) {
			Entity et = p.getWorld().spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
			et.setVelocity(divide(p.getEyeLocation().subtract(p.getLocation()), 2).toVector());
		}
	}
}
