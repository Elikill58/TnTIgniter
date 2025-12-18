package com.elikill58.tntigniter.support;

import java.util.List;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardSupport {

	private static ApplicableRegionSet getRegionSet(Location location) {
		RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
		if (regionManager == null)
			return null;
		return regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
	}
	
	public static boolean isInAreas(Location baseLoc, List<String> list) {
		for(String s : list)
			if(isInArea(baseLoc, s))
				return true;
		return false;
	}
	
	public static boolean isInArea(Location baseLoc, String area) {
		ApplicableRegionSet region = getRegionSet(baseLoc);
		if(region == null)
			return false;
		
		for(ProtectedRegion r : region.getRegions())
			if(r.getId().equalsIgnoreCase(area))
				return true;
		return false;
	}
}
