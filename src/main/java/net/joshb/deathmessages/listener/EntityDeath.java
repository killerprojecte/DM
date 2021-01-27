package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastDeathMessageEvent;
import net.joshb.deathmessages.api.events.BroadcastTamableDeathMessageEvent;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Gangs;
import net.joshb.deathmessages.config.Settings;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityDeath implements Listener {

    synchronized void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastLocation(p.getLocation());
            pm.setLastDamageCause(e.getEntity().getLastDamageCause().getCause());
            if(pm.isBlacklisted()) return;

            if (!(pm.getLastEntityDamager() instanceof LivingEntity) || pm.getLastEntityDamager() == e.getEntity()) {
                //Natural Death
                if (pm.getLastExplosiveEntity() instanceof EnderCrystal) {
                    TextComponent tx = Assets.getNaturalDeath(pm, "End-Crystal");
                    if (tx == null) return;
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, tx, getWorlds(p), false);
                    Bukkit.getPluginManager().callEvent(event);
                } else if (pm.getLastExplosiveEntity() instanceof TNTPrimed) {
                    TextComponent tx = Assets.getNaturalDeath(pm, "TNT");
                    if (tx == null) return;
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, tx, getWorlds(p), false);
                    Bukkit.getPluginManager().callEvent(event);
                } else if (pm.getLastExplosiveEntity() instanceof Firework) {
                    TextComponent tx = Assets.getNaturalDeath(pm, "Firework");
                    if (tx == null) return;
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, tx, getWorlds(p), false);
                    Bukkit.getPluginManager().callEvent(event);
                } else if (pm.getLastClimbing() != null && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
                    TextComponent tx = Assets.getNaturalDeath(pm, "Climbable");
                    if (tx == null) return;
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, tx, getWorlds(p), false);
                    Bukkit.getPluginManager().callEvent(event);
                } else {
                    TextComponent tx = Assets.getNaturalDeath(pm, Assets.getSimpleCause(pm.getLastDamage()));
                    if (tx == null) return;
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, tx, getWorlds(p), false);
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else {
                //Killed by mob
                Entity ent = pm.getLastEntityDamager();
                String mobName = ent.getType().getEntityClass().getSimpleName().toLowerCase();
                int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Radius");
                int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Amount");

                boolean gangKill = false;

                if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
                    int totalMobEntities = 0;
                    for (Entity entities : p.getNearbyEntities(radius, radius, radius)) {
                        if (entities.getType().equals(ent.getType())) {
                            totalMobEntities++;
                        }
                    }
                    if (totalMobEntities >= amount) {
                        gangKill = true;
                    }
                }
                TextComponent tx = Assets.deathMessage(pm, gangKill);
                if (tx == null) return;
                BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, (LivingEntity) pm.getLastEntityDamager(), tx, getWorlds(p), gangKill);
                Bukkit.getPluginManager().callEvent(event);
            }
        } else if (e.getEntity() instanceof Tameable) {
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player) {
                Player killer = e.getEntity().getKiller();
                PlayerManager pm = PlayerManager.getPlayer(killer);
                Tameable tameable = (Tameable) e.getEntity();
                if (tameable.getOwner() == null || tameable.getOwner().equals(e.getEntity().getKiller())) return;

                TextComponent tx = Assets.getTamable(pm, tameable);
                if (tx == null) return;
                BroadcastTamableDeathMessageEvent event = new BroadcastTamableDeathMessageEvent(killer, tameable.getOwner().getName(), tameable, tx, getWorlds(tameable));
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }

    private List<World> getWorlds(Entity e){
        List<World> broadcastWorlds = new ArrayList<>();
        if(Settings.getInstance().getConfig().getStringList("Disabled-Worlds").contains(e.getWorld().getName())){
            return broadcastWorlds;
        }
        if (Settings.getInstance().getConfig().getBoolean("Per-World-Messages")) {
            for (String groups : Settings.getInstance().getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
                List<String> worlds = Settings.getInstance().getConfig().getStringList("World-Groups." + groups);
                if(worlds.contains(e.getWorld().getName())){
                    for(String single : worlds){
                        broadcastWorlds.add(Bukkit.getWorld(single));
                    }
                }
            }
            if(broadcastWorlds.isEmpty()){
                broadcastWorlds.add(e.getWorld());
            }
        } else {
            return Bukkit.getWorlds();
        }
        return broadcastWorlds;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath_LOWEST(EntityDeathEvent e) {
        if (DeathMessages.eventPriority.equals(EventPriority.LOWEST)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath_LOW(EntityDeathEvent e) {
        if (DeathMessages.eventPriority.equals(EventPriority.LOW)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath_NORMAL(EntityDeathEvent e) {
        if (DeathMessages.eventPriority.equals(EventPriority.NORMAL)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath_HIGH(EntityDeathEvent e) {
        if (DeathMessages.eventPriority.equals(EventPriority.HIGH)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath_HIGHEST(EntityDeathEvent e) {
        if (DeathMessages.eventPriority.equals(EventPriority.HIGHEST)) {
            onEntityDeath(e);
        }
    }

}
