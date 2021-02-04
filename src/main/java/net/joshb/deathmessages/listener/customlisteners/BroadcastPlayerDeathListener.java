package net.joshb.deathmessages.listener.customlisteners;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastDeathMessageEvent;
import net.joshb.deathmessages.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BroadcastPlayerDeathListener implements Listener {

    @EventHandler
    public void broadcastListener(BroadcastDeathMessageEvent e) {
        if (!e.isCancelled()) {
            if(Messages.getInstance().getConfig().getBoolean("Console.Enabled")){
                Bukkit.getConsoleSender().sendMessage(e.getTextComponent().toLegacyText());
            }
            boolean discordSent = false;
            for(World w : e.getBroadcastedWorlds()){
                for(Player pls : w.getPlayers()){
                    if(DeathMessages.worldGuardExtension != null){
                        if(!DeathMessages.worldGuardExtension.inBroadcastRegion(pls, e.getMessageType())){
                            continue;
                        }
                    }
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if(DeathMessages.discordBotAPIExtension != null && !discordSent){
                        DeathMessages.discordBotAPIExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                        discordSent = true;
                    }
                    if(DeathMessages.discordSRVExtension != null && !discordSent){
                        DeathMessages.discordSRVExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                        discordSent = true;
                    }
                    if (!pms.getMessagesEnabled()) {
                        continue;
                    }
                    pls.spigot().sendMessage(e.getTextComponent());
                }
            }
        }
    }
}
