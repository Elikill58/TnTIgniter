package com.elikill58.tntigniter;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.tntigniter.support.WorldGuardSupport;

@SuppressWarnings("deprecation")
public class BlockEvents implements Listener {

	private static final EntityType PRIMED_TNT = loadPrimedTntEntityType();

	public static EntityType loadPrimedTntEntityType() {
		for (String name : Arrays.asList("PRIMED_TNT", "TNT")) {
			try {
				return EntityType.valueOf(name);
			} catch (Exception e) {
			}
		}
		return null;
	}

	private final TnTIgniter pl;

	public BlockEvents(TnTIgniter pl) {
		this.pl = pl;
	}

	private boolean hasPerm(Player p) {
		String ignitePerm = pl.getConfig().getString("ignite_perm");
		return ignitePerm.isEmpty() || ignitePerm.equalsIgnoreCase(" ") || p.isOp() || p.hasPermission(ignitePerm);
	}

	public boolean isDisabledPlayer(Player p) {
		if (TnTIgniter.worldGuardSupport && WorldGuardSupport.isInAreas(p.getLocation(), TnTIgniter.DISABLED_AREA))
			return true;
		for (String s : TnTIgniter.DISABLED_PLAYER)
			if (p.getName().equalsIgnoreCase(s) || p.getDisplayName().equalsIgnoreCase(s) || p.getUniqueId().toString().equalsIgnoreCase(s))
				return true;
		return false;
	}
	
	public void tryRemoveTntFromInventory(Player p) {
		if(pl.getConfig().getBoolean("keep_tnt_in_inventory", false))
			return; // nothing to remove
		int slot = p.getInventory().first(Material.TNT);
		ItemStack item = p.getInventory().getItem(slot);
		if(item.getAmount() == 1)
			p.getInventory().setItem(slot, null);
		else
			item.setAmount(item.getAmount() - 1);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlaceBlock(BlockPlaceEvent e) {
		Block bp = e.getBlockPlaced();
		if (bp == null || !TnTIgniter.isActive() || isDisabledPlayer(e.getPlayer()) || e.isCancelled())
			return;
		if (bp.getType().equals(Material.TNT) && (hasPerm(e.getPlayer())) && TnTIgniter.ALLOWED_WORLD.contains(bp.getWorld().getName())) {
			bp.setType(Material.AIR);
			bp.getWorld().spawnEntity(bp.getLocation(), PRIMED_TNT);
			tryRemoveTntFromInventory(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!TnTIgniter.isActive() || !TnTIgniter.launcherActive || p.getItemInHand() == null || e.isCancelled())
			return;
		String actName = e.getAction().name(), click = TnTIgniter.launchClick, with = TnTIgniter.launchWith;
		if (!(click.equalsIgnoreCase("both") || actName.contains(click.toUpperCase())))
			return;
		if (!(with.equalsIgnoreCase("both") || actName.contains(with.toUpperCase())))
			return;

		if (p.getItemInHand().getType().equals(Material.TNT) && !isDisabledPlayer(p)) {
			Entity et = p.getWorld().spawnEntity(p.getLocation().clone().add(0, 1, 0), PRIMED_TNT);
			et.setVelocity(p.getLocation().getDirection().add(TnTIgniter.tntVector).normalize());
			e.setCancelled(true);
			tryRemoveTntFromInventory(e.getPlayer());
		}
	}
}
