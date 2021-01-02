package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

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

