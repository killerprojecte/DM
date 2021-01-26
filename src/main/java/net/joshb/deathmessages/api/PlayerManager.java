package net.joshb.deathmessages.api;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.config.UserData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private final Player p;
    private final UUID uuid;
    private final String name;
    private final String displayName;
    private boolean messagesEnabled;
    private boolean isBlacklisted;
    private DamageCause damageCause;
    private Entity lastEntityDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastProjectileEntity;
    private Material climbing;
    private Location location;

    private BukkitTask lastEntityTask;

    private static List<PlayerManager> players = new ArrayList<>();

    public PlayerManager(Player p){
        this.p = p;
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.displayName = p.getDisplayName();

        if(!UserData.getInstance().getConfig().contains(p.getUniqueId().toString())){
            UserData.getInstance().getConfig().set(p.getUniqueId().toString() + ".username", p.getName());
            UserData.getInstance().getConfig().set(p.getUniqueId().toString() + ".messages-enabled", true);
            UserData.getInstance().getConfig().set(p.getUniqueId().toString() + ".is-blacklisted", false);
            UserData.getInstance().save();
        }
        messagesEnabled = UserData.getInstance().getConfig().getBoolean(p.getUniqueId().toString() + ".messages-enabled");
        isBlacklisted = UserData.getInstance().getConfig().getBoolean(p.getUniqueId().toString() + ".is-blacklisted");
        players.add(this);
    }

    public Player getPlayer(){ return p; }

    public UUID getUUID(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public String getDisplayName() { return displayName; }

    public boolean getMessagesEnabled() { return messagesEnabled; }

    public void setMessagesEnabled(boolean b){
        this.messagesEnabled = b;
        UserData.getInstance().getConfig().set(p.getUniqueId().toString() + ".messages-enabled", b);
        UserData.getInstance().save();
    }

    public boolean isBlacklisted() { return isBlacklisted; }

    public void setBlacklisted(boolean b){
        this.isBlacklisted = b;
    }

    public void setLastDamageCause(DamageCause dc){
        this.damageCause = dc;
    }

    public DamageCause getLastDamage() {
        return damageCause;
    }

    public void setLastEntityDamager(Entity e){
        setLastExplosiveEntity(null);
        setLastProjectileEntity(null);
        this.lastEntityDamager = e;
        if(e == null) return;
        if(lastEntityTask != null){
            lastEntityTask.cancel();
        }
        lastEntityTask = new BukkitRunnable(){
            @Override
            public void run() {
                setLastEntityDamager(null);
            }
        }.runTaskLater(DeathMessages.plugin, Settings.getInstance().getConfig().getInt("Expire-Last-Damage.Expire-Mob") * 20);
    }

    public Entity getLastEntityDamager() {
        return lastEntityDamager;
    }

    public void setLastExplosiveEntity(Entity e){
        this.lastExplosiveEntity = e;
    }

    public Entity getLastExplosiveEntity() {
        return lastExplosiveEntity;
    }

    public Projectile getLastProjectileEntity() {
        return lastProjectileEntity;
    }

    public void setLastProjectileEntity(Projectile lastProjectileEntity){
        this.lastProjectileEntity = lastProjectileEntity;
    }

    public Material getLastClimbing() {
        return climbing;
    }

    public void setLastClimbing(Material climbing){
        this.climbing = climbing;
    }

    public void setLastLocation(Location location){
        this.location = location;
    }

    public Location getLastLocation() { return location; }

    public static PlayerManager getPlayer(Player p){
        for(PlayerManager pm : players){
            if(pm.getUUID().equals(p.getUniqueId())){
                return pm;
            }
        }
        return null;
    }

    public static PlayerManager getPlayer(UUID uuid){
        for(PlayerManager pm : players){
            if(pm.getUUID().equals(uuid)){
                return pm;
            }
        }
        return null;
    }

}

