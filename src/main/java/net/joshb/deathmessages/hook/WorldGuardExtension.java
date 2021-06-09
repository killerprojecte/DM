package net.joshb.deathmessages.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.entity.Player;

public class WorldGuardExtension {

    public static final StateFlag BROADCAST_PLAYER;
    public static final StateFlag BROADCAST_MOBS;
    public static final StateFlag BROADCAST_NATURAL;
    public static final StateFlag BROADCAST_TAMEABLE;

    private final WorldGuard worldGuard;
    private RegionContainer regionContainer;

    static {
        BROADCAST_PLAYER = new StateFlag("broadcast-deathmessage-player", true);
        BROADCAST_MOBS = new StateFlag("broadcast-deathmessage-mobs", true);
        BROADCAST_NATURAL = new StateFlag("broadcast-deathmessage-natural", true);
        BROADCAST_TAMEABLE = new StateFlag("broadcast-deathmessage-tameable", true);
    }

    public WorldGuardExtension() {
        this.worldGuard = WorldGuard.getInstance();
    }

    public void enable() {
        this.regionContainer = worldGuard.getPlatform().getRegionContainer();
    }

    public void register() {
        final FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            registry.register(BROADCAST_PLAYER);
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
        try {
            registry.register(BROADCAST_MOBS);
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
        try {
            registry.register(BROADCAST_NATURAL);
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
        try {
            registry.register(BROADCAST_TAMEABLE);
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
    }

    public StateFlag.State getRegionState(final Player p, MessageType messageType) {
        if (this.regionContainer == null) {
            return StateFlag.State.ALLOW;
        }
        final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        final RegionQuery query = regionContainer.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(loc);
        final LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
        if (messageType.equals(MessageType.PLAYER)) {
            return set.queryState(lp, BROADCAST_PLAYER);
        } else if (messageType.equals(MessageType.MOB)) {
            return set.queryState(lp, BROADCAST_MOBS);
        } else if (messageType.equals(MessageType.NATURAL)) {
            return set.queryState(lp, BROADCAST_NATURAL);
        } else if (messageType.equals(MessageType.TAMEABLE)) {
            return set.queryState(lp, BROADCAST_TAMEABLE);
        }
        return StateFlag.State.ALLOW;
    }

    public boolean isInRegion(Player p, String regionID) {
        if (this.regionContainer == null) {
            return false;
        }
        final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        final RegionQuery query = regionContainer.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(loc);
        final LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
        for (ProtectedRegion region : set) {
            if (region.getId().equals(regionID)) {
                return true;
            }
        }
        return false;
    }

}
