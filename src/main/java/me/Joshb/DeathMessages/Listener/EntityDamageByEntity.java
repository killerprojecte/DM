package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            if(e.getDamager().getType().isAlive()) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager() instanceof Projectile){
                Projectile projectile = (Projectile) e.getDamager();
                pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
            } else if (e.getDamager() instanceof FallingBlock){
                pm.setLastEntityDamager(e.getDamager());
            }
        }
    }
}
