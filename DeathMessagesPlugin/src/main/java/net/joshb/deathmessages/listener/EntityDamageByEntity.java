package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.EntityManager;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.config.EntityDeathMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.UUID;

public class EntityDamageByEntity implements Listener {

    public static HashMap<UUID, Entity> explosions = new HashMap<>();

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager().getUniqueId())) {
                    pm.setLastEntityDamager(explosions.get(e.getDamager().getUniqueId()));
                    pm.setLastExplosiveEntity(e.getDamager());
                } else if (e.getDamager() instanceof TNTPrimed) {
                    TNTPrimed tnt = (TNTPrimed) e.getDamager();
                    if (tnt.getSource() instanceof LivingEntity) {
                        pm.setLastEntityDamager(tnt.getSource());
                    }
                    pm.setLastExplosiveEntity(e.getDamager());
                } else if (e.getDamager() instanceof Firework) {
                    Firework firework = (Firework) e.getDamager();
                    try {
                        if (firework.getShooter() instanceof LivingEntity) {
                            pm.setLastEntityDamager((LivingEntity) firework.getShooter());
                        }
                        pm.setLastExplosiveEntity(e.getDamager());
                    } catch (NoSuchMethodError ignored) {
                        //McMMO ability
                    }
                } else {
                    pm.setLastEntityDamager(e.getDamager());
                    pm.setLastExplosiveEntity(e.getDamager());
                }
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
                }
                pm.setLastProjectileEntity(projectile);
            } else if (e.getDamager() instanceof FallingBlock) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (e.getDamager().getType().isAlive()) {
                pm.setLastEntityDamager(e.getDamager());
            } else if (DeathMessages.majorVersion() >= 11) {
                if (e.getDamager() instanceof EvokerFangs) {
                    EvokerFangs evokerFangs = (EvokerFangs) e.getDamager();
                    pm.setLastEntityDamager(evokerFangs.getOwner());
                }
            }
        } else {
            for (String listened : EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities")
                    .getKeys(false)) {
                if(listened.contains(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase())) {
                    EntityManager em;
                    if (EntityManager.getEntity(e.getEntity().getUniqueId()) == null) {
                        em = new EntityManager(e.getEntity(), e.getEntity().getUniqueId());
                    } else {
                        em = EntityManager.getEntity(e.getEntity().getUniqueId());
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                        if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager())) {
                            if (explosions.get(e.getDamager().getUniqueId()) instanceof Player) {
                                em.setLastPlayerDamager(PlayerManager.getPlayer((Player) explosions.get(e.getDamager().getUniqueId())));
                                em.setLastExplosiveEntity(e.getDamager());
                            }
                        } else if (e.getDamager() instanceof TNTPrimed) {
                            TNTPrimed tnt = (TNTPrimed) e.getDamager();
                            if (tnt.getSource() instanceof Player) {
                                em.setLastPlayerDamager(PlayerManager.getPlayer((Player) tnt.getSource()));
                            }
                            em.setLastExplosiveEntity(e.getDamager());
                        } else if (e.getDamager() instanceof Firework) {
                            Firework firework = (Firework) e.getDamager();
                            try {
                                if (firework.getShooter() instanceof Player) {
                                    em.setLastPlayerDamager(PlayerManager.getPlayer((Player) firework.getShooter()));
                                }
                                em.setLastExplosiveEntity(e.getDamager());
                            } catch (NoSuchMethodError ignored) {
                                //McMMO ability
                            }
                        } else {
                            em.setLastPlayerDamager(PlayerManager.getPlayer((Player) e.getDamager()));
                            em.setLastExplosiveEntity(e.getDamager());
                        }
                    } else if (e.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) e.getDamager();
                        if (projectile.getShooter() instanceof Player) {
                            em.setLastPlayerDamager(PlayerManager.getPlayer((Player) projectile.getShooter()));
                        }
                        em.setLastProjectileEntity(projectile);
                    } else if (e.getDamager() instanceof Player) {
                        em.setLastPlayerDamager(PlayerManager.getPlayer((Player) e.getDamager()));
                    }
                }
            }
        }
        if (e.getEntity() instanceof EnderCrystal) {
            if (e.getDamager().getType().isAlive()) {
                explosions.put(e.getEntity().getUniqueId(), e.getDamager());
            } else if (e.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    explosions.put(e.getEntity().getUniqueId(), (LivingEntity) projectile.getShooter());
                }
            }

        }
    }
}
