package me.Joshb.DeathMessages.Manager;

import me.Joshb.DeathMessages.Config.Settings;
import me.Joshb.DeathMessages.DeathMessages;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private Player p;
    private UUID uuid;
    private String name;
    private String displayName;
    private DamageCause damageCause;
    private Entity lastEntityDamager;
    private Location location;

    private BukkitTask lastEntityTask;

    private static List<PlayerManager> players = new ArrayList<>();

    public PlayerManager(Player p){
        this.p = p;
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.displayName = p.getDisplayName();

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

    public void setLastDamageCause(DamageCause dc){
        this.damageCause = dc;
    }

    public DamageCause getLastDamage() {
        return damageCause;
    }

    public void setLastEntityDamager(Entity e){
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

    public void setLastLocation(Location location){
        this.location = location;
    }

    public Location getLastLocation() { return location; }

    public static PlayerManager getPlayer(Player p){
        for(PlayerManager pm : players){
            if(pm.getUUID() == p.getUniqueId()){
                return pm;
            }
        }
        return null;
    }

}

