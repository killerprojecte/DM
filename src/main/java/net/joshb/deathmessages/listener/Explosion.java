package net.joshb.deathmessages.listener;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class Explosion implements Listener {


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
        //Bukkit.broadcastMessage("EntityExplode: " + e.getEntity().toString());
    }

    @EventHandler
    public void onEntityExplode(BlockExplodeEvent e){
       // Bukkit.broadcastMessage("BlockExplode: " + e.getBlock().toString());
    }

    @EventHandler
    public void interact(ExplosionPrimeEvent e){
        if(e.getEntity() instanceof EnderCrystal){
        //    Bukkit.broadcastMessage("end crystalLLL");
        }
    }
}
