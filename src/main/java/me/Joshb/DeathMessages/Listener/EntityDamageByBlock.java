package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageByBlock implements Listener {


    @EventHandler
    public void onEntityDeath(EntityDamageByBlockEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Bukkit.broadcastMessage(e.getCause().name());
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastDamageCause(p.getLastDamageCause().getCause());

        }

    }

}

