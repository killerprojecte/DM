package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.ExplosionManager;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.DMBlockExplodeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (b.getType().equals(Material.AIR)) return;
        World.Environment environment = b.getWorld().getEnvironment();
        if (environment.equals(World.Environment.NETHER) || environment.equals(World.Environment.THE_END)) {
            if (b.getType().name().contains("BED") && !b.getType().equals(Material.BEDROCK)) {
                List<UUID> effected = new ArrayList<>();
                for (Player p : e.getClickedBlock().getWorld().getPlayers()) {
                    PlayerManager effect = PlayerManager.getPlayer(p);
                    if (p.getLocation().distanceSquared(b.getLocation()) < 100) {
                        effected.add(p.getUniqueId());
                        effect.setLastEntityDamager(e.getPlayer());
                    }
                }
                new ExplosionManager(e.getPlayer().getUniqueId(), b.getType(), b.getLocation(), effected);
                DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(e.getPlayer(), b);
                Bukkit.getPluginManager().callEvent(explodeEvent);
            }
        } else if (b.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            if (DeathMessages.majorVersion() >= 16) {
                if (b.getType().equals(Material.RESPAWN_ANCHOR)) {
                    RespawnAnchor anchor = (RespawnAnchor) b.getBlockData();

                    if (!(anchor.getCharges() == anchor.getMaximumCharges())) return;
                    List<UUID> effected = new ArrayList<>();
                    for (Player p : e.getClickedBlock().getWorld().getPlayers()) {
                        PlayerManager effect = PlayerManager.getPlayer(p);
                        if (p.getLocation().distanceSquared(b.getLocation()) < 100) {
                            effected.add(p.getUniqueId());
                            effect.setLastEntityDamager(e.getPlayer());
                        }
                    }
                    new ExplosionManager(e.getPlayer().getUniqueId(), b.getType(), b.getLocation(), effected);
                    DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(e.getPlayer(), b);
                    Bukkit.getPluginManager().callEvent(explodeEvent);
                }
            }
        }
    }
}
