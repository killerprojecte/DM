package net.joshb.deathmessages.listener.customlisteners;

import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastTamableDeathMessageEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BroadcastTameableDeathListener implements Listener {

    @EventHandler
    public void broadcastListener(BroadcastTamableDeathMessageEvent e) {
        if (!e.isCancelled()) {
            for(World w : e.getBroadcastedWorlds()){
                for(Player pls : w.getPlayers()){
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if (!pms.getMessagesEnabled()) {
                        continue;
                    }
                    pls.spigot().sendMessage(e.getTextComponent());
                }
            }
        }
    }
}