package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Assets.Assets;
import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

            for(Player pls : Bukkit.getOnlinePlayers()){
                pls.spigot().sendMessage(Assets.deathMessage(pm, false));
            }
        }

    }
}
