package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDamage implements Listener {


    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(e.isCancelled()) return;
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastDamageCause(p.getLastDamageCause().getCause());
        }

    }

}

