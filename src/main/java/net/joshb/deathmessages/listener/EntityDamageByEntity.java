package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.manager.PlayerManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class EntityDamageByEntity implements Listener {

    public static HashMap<Entity, Entity> explosions = new HashMap<>();

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            if (e.getDamager().getType().isAlive()) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
            } else if (e.getDamager() instanceof FallingBlock) {
                pm.setLastEntityDamager(e.getDamager());
            } else if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)){
                if(e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager())) {
                    pm.setLastEntityDamager(explosions.get(e.getDamager()));
                    pm.setLastExplosiveEntity(e.getDamager());
                }
            }

        }
        if (e.getEntity() instanceof EnderCrystal) {
            if (e.getDamager().getType().isAlive()) {
                explosions.put(e.getEntity(), e.getDamager());
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                explosions.put(e.getEntity(), (LivingEntity) projectile.getShooter());
            }

        }
    }
}
