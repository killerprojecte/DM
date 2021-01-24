package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Gangs;
import net.joshb.deathmessages.manager.PlayerManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

    synchronized void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastLocation(p.getLocation());
            pm.setLastDamageCause(e.getEntity().getLastDamageCause().getCause());

            if (!(pm.getLastEntityDamager() instanceof LivingEntity) || pm.getLastEntityDamager() == e.getEntity()) {
                //Natural Death
                if(pm.getLastExplosiveEntity() instanceof EnderCrystal){
                    for (Player pls : Bukkit.getOnlinePlayers()) {
                        PlayerManager pms = PlayerManager.getPlayer(pls);
                        if(!pms.getMessagesEnabled()){
                            continue;
                        }
                        TextComponent tx = Assets.getNaturalDeath(pm, "End-Crystal");
                        if (tx == null) return;
                        pls.spigot().sendMessage(tx);
                    }
                } else if(pm.getLastExplosiveEntity() instanceof TNTPrimed){
                    for (Player pls : Bukkit.getOnlinePlayers()) {
                        PlayerManager pms = PlayerManager.getPlayer(pls);
                        if(!pms.getMessagesEnabled()){
                            continue;
                        }
                        TextComponent tx = Assets.getNaturalDeath(pm, "TNT");
                        if (tx == null) return;
                        pls.spigot().sendMessage(tx);
                    }
                } else if(pm.getLastExplosiveEntity() instanceof Firework){
                    for (Player pls : Bukkit.getOnlinePlayers()) {
                        PlayerManager pms = PlayerManager.getPlayer(pls);
                        if(!pms.getMessagesEnabled()){
                            continue;
                        }
                        TextComponent tx = Assets.getNaturalDeath(pm, "Firework");
                        if (tx == null) return;
                        pls.spigot().sendMessage(tx);
                    }
                } else if(pm.getLastClimbing() != null){
                    //Bukkit.broadcastMessage(p.getName() + " fell off " + pm.getLastClimbing().name() + " and died");
                } else {
                    for (Player pls : Bukkit.getOnlinePlayers()) {
                        PlayerManager pms = PlayerManager.getPlayer(pls);
                        if(!pms.getMessagesEnabled()){
                            continue;
                        }
                        TextComponent tx = Assets.getNaturalDeath(pm, Assets.getSimpleCause(pm.getLastDamage()));
                        if (tx == null) return;
                        pls.spigot().sendMessage(tx);
                    }
                }
            } else {
                //Killed by mob
                Entity ent = pm.getLastEntityDamager();
                String mobName = ent.getType().getEntityClass().getSimpleName().toLowerCase();
                int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Radius");
                int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Amount");
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if(!pms.getMessagesEnabled()){
                        continue;
                    }
                    if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
                        int totalMobEntities = 0;
                        for (Entity entities : p.getNearbyEntities(radius, radius, radius)) {
                            if (entities.getType().equals(ent.getType())) {
                                totalMobEntities++;
                            }
                        }
                        if (totalMobEntities >= amount) {
                            TextComponent tx = Assets.deathMessage(pm, true);
                            if (tx == null) return;
                            pls.spigot().sendMessage(tx);
                            return;
                        }
                    }
                    TextComponent tx = Assets.deathMessage(pm, false);
                    if (tx == null) return;
                    pls.spigot().sendMessage(tx);
                }
            }
        } else if (e.getEntity() instanceof Tameable){
            if(e.getEntity().getKiller() instanceof Player || e.getEntity().getKiller() != null){
                Player killer = e.getEntity().getKiller();
                PlayerManager pm = PlayerManager.getPlayer(killer);
                Tameable tameable = (Tameable) e.getEntity();
                if(tameable.getOwner() == null || tameable.getOwner().equals(e.getEntity().getKiller())) return;
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if(!pms.getMessagesEnabled()){
                        continue;
                    }
                    TextComponent tx = Assets.getTamable(pm, tameable);
                    if (tx == null) return;
                    pls.spigot().sendMessage(tx);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath_LOWEST(EntityDeathEvent e){
        if(DeathMessages.eventPriority.equals(EventPriority.LOWEST)){
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath_LOW(EntityDeathEvent e){
        if(DeathMessages.eventPriority.equals(EventPriority.LOW)){
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath_NORMAL(EntityDeathEvent e){
        if(DeathMessages.eventPriority.equals(EventPriority.NORMAL)){
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath_HIGH(EntityDeathEvent e){
        if(DeathMessages.eventPriority.equals(EventPriority.HIGH)){
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath_HIGHEST(EntityDeathEvent e){
        if(DeathMessages.eventPriority.equals(EventPriority.HIGHEST)){
            onEntityDeath(e);
        }
    }

}
