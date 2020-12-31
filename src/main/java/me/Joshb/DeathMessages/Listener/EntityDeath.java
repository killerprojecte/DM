package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Assets.Assets;
import me.Joshb.DeathMessages.Config.Gangs;
import me.Joshb.DeathMessages.Manager.PlayerManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {


    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastLocation(p.getLocation());
            pm.setLastDamageCause(e.getEntity().getLastDamageCause().getCause());

            if (!(pm.getLastEntityDamager() instanceof LivingEntity) || pm.getLastEntityDamager() == e.getEntity()) {
                //Natural Death
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    TextComponent tx = Assets.getNaturalDeath(pm);
                    if (tx == null) return;
                    pls.spigot().sendMessage(tx);
                }
            } else {
                //Killed by mob
                Entity ent = pm.getLastEntityDamager();
                String mobName = ent.getType().getEntityClass().getSimpleName().toLowerCase();
                int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Radius");
                int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Amount");
                for (Player pls : Bukkit.getOnlinePlayers()) {
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
            if(e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player){
                Player killer = e.getEntity().getKiller();
                PlayerManager pm = PlayerManager.getPlayer(killer);
                Tameable tameable = (Tameable) e.getEntity();
                if(tameable.getOwner().equals(e.getEntity().getKiller())) return;
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    TextComponent tx = Assets.getTamable(pm, tameable);
                    if (tx == null) return;
                    pls.spigot().sendMessage(tx);
                }
            }
        }

    }
}
