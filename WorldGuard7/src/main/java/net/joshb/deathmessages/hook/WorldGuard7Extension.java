package net.joshb.deathmessages.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.entity.Player;

public final class WorldGuard7Extension implements WorldGuardExtension{

    public StateFlag.State getRegionState(final Player p, MessageType messageType) {
        final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final ApplicableRegionSet set = rc.createQuery().getApplicableRegions(loc);
        final LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
        if (messageType.equals(MessageType.PLAYER)) {
            return set.queryState(lp, BROADCAST_PLAYER);
        } else if (messageType.equals(MessageType.MOB)) {
            return set.queryState(lp, BROADCAST_MOBS);
        } else if (messageType.equals(MessageType.NATURAL)) {
            return set.queryState(lp, BROADCAST_NATURAL);
        } else if (messageType.equals(MessageType.ENTITY)) {
            return set.queryState(lp, BROADCAST_ENTITY);
        }
        return StateFlag.State.ALLOW;
    }

    public boolean isInRegion(Player p, String regionID) {
        final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final ApplicableRegionSet set = rc.createQuery().getApplicableRegions(loc);
        for (ProtectedRegion region : set) {
            if (region.getId().equals(regionID)) {
                return true;
            }
        }
        return false;
    }
}