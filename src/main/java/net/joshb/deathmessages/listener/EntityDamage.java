package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.api.PlayerManager;
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
            pm.setLastDamageCause(e.getCause());
           // for fall large if ppl want it float dist = e.getEntity().getFallDistance();
        }
    }

}

