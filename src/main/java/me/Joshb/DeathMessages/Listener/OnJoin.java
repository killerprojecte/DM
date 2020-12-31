package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(PlayerManager.getPlayer(p) == null) new PlayerManager(p);
    }
}
