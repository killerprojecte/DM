package net.joshb.deathmessages.menu;

import net.joshb.deathmessages.api.PlayerManager;
import org.bukkit.inventory.Inventory;

public class DeathInventory {

    public DeathInventory() {
    }

    private static DeathInventory instance = new DeathInventory();

    public static DeathInventory getInstance() {
        return instance;
    }

    public Inventory getInventory(PlayerManager pm){
        return pm.getCachedInventory();//no, just to remove error
    }

}
