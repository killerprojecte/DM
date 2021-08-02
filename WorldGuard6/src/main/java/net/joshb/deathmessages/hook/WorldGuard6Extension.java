  
/*
 * BanItem - Lightweight, powerful & configurable per world ban item plugin
 * Copyright (C) 2020 André Sustac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.joshb.deathmessages.hook;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.entity.Player;

public final class WorldGuard6Extension implements WorldGuardExtension {

    public StateFlag.State getRegionState(final Player p, MessageType messageType) {
        final RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        final RegionManager regions = container.get(p.getWorld());
        if (regions == null) return null;
        final ApplicableRegionSet set = regions.getApplicableRegions(p.getLocation());
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

    @Override
    public boolean isInRegion(Player p, String regionID) {
        final RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        final RegionManager regions = container.get(p.getWorld());
        if (regions == null) return false;
        final ApplicableRegionSet applicableRegionSet = regions.getApplicableRegions(p.getLocation());
        for (ProtectedRegion region : applicableRegionSet) {
            if (region.getId().equals(regionID)) {
                return true;
            }
        }
        return false;
    }
}