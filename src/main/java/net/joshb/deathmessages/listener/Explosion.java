package net.joshb.deathmessages.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class Explosion implements Listener {


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
    }

    @EventHandler
    public void onEntityExplode(BlockExplodeEvent e){
    }

    @EventHandler
    public void interact(ExplosionPrimeEvent e){
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e){
    }
}
