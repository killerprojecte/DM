package net.joshb.deathmessages.api;

import net.joshb.deathmessages.DeathMessages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExplosionManager {

    private final UUID pyro;
    private final Material material;
    private Location location;
    private final List<UUID> effected;

    public static List<ExplosionManager> explosions = new ArrayList<>();

    public ExplosionManager(UUID pyro, Material material, Location location, List<UUID> effected) {
        this.pyro = pyro;
        this.material = material;
        this.location = location;
        this.effected = effected;
        explosions.add(this);
        //Destroys class. Wont need the info anymore
        new BukkitRunnable(){
            @Override
            public void run() {
                destroy();
            }
        }.runTaskLater(DeathMessages.plugin, 5 * 20);
    }

    public UUID getPyro() {
        return this.pyro;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void addEffected(Player p) {
        this.effected.add(p.getUniqueId());
    }

    public List<UUID> getEffected() {
        return this.effected;
    }

    public static ExplosionManager getExplosion(UUID pyro) {
        for (ExplosionManager ex : explosions) {
            if (ex.getPyro().equals(pyro)) {
                return ex;
            }
        }
        return null;
    }

    public static ExplosionManager getExplosion(Location location) {
        for (ExplosionManager ex : explosions) {
            if (ex.getLocation().equals(location)) {
                return ex;
            }
        }
        return null;
    }

    public static ExplosionManager getManagerIfEffected(Player p) {
        for (ExplosionManager ex : explosions) {
            if (ex.getEffected().contains(p.getUniqueId())) {
                return ex;
            }
        }
        return null;
    }

    private void destroy(){
        explosions.remove(this);
    }
}
