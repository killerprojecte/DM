package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.api.PlayerManager;
import org.bukkit.Bukkit;
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
        if (e.getEntity() instanceof Player && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)){
                if(e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager())) {
                    pm.setLastEntityDamager(explosions.get(e.getDamager()));
                    pm.setLastExplosiveEntity(e.getDamager());
                } else if (e.getDamager() instanceof TNTPrimed){
                    TNTPrimed tnt = (TNTPrimed) e.getDamager();
                    if(tnt.getSource() instanceof LivingEntity){
                        pm.setLastEntityDamager(tnt.getSource());
                    }
                    pm.setLastExplosiveEntity(e.getDamager());
                } else if (e.getDamager() instanceof Firework){
                    Firework firework = (Firework) e.getDamager();
                    try{
                        if(firework.getShooter() instanceof LivingEntity){
                            pm.setLastEntityDamager((LivingEntity) firework.getShooter());
                        }
                        pm.setLastExplosiveEntity(e.getDamager());
                    } catch (NoSuchMethodError ignored){
                        //McMMO ability
                    }
                } else {
                    pm.setLastEntityDamager(e.getDamager());
                    pm.setLastExplosiveEntity(e.getDamager());
                }
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
                pm.setLastProjectileEntity(projectile);
            } else if (e.getDamager() instanceof FallingBlock) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager().getType().isAlive()) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager() instanceof EvokerFangs){
                EvokerFangs evokerFangs = (EvokerFangs) e.getDamager();
                pm.setLastEntityDamager(evokerFangs.getOwner());
            }

        }
        if (e.getEntity() instanceof EnderCrystal) {
            if (e.getDamager().getType().isAlive()) {
                explosions.put(e.getEntity(), e.getDamager());
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                if(projectile.getShooter() instanceof LivingEntity){
                    explosions.put(e.getEntity(), (LivingEntity) projectile.getShooter());
                }
            }

        }
    }
}
