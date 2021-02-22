package net.joshb.deathmessages.listener.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastTamableDeathMessageEvent;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BroadcastTameableDeathListener implements Listener {

    @EventHandler
    public void broadcastListener(BroadcastTamableDeathMessageEvent e) {
        if (!e.isCancelled()) {
            if(Messages.getInstance().getConfig().getBoolean("Console.Enabled")){
                Bukkit.getConsoleSender().sendMessage(e.getTextComponent().toLegacyText());
            }

            boolean discordSent = false;

            boolean privateTameable = Settings.getInstance().getConfig().getBoolean("Private-Messages.Tameable");

            for(World w : e.getBroadcastedWorlds()){
                for(Player pls : w.getPlayers()){
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if(privateTameable && pms.getUUID().equals(e.getPlayer().getUniqueId())){
                        if (pms.getMessagesEnabled()) {
                            pls.spigot().sendMessage(e.getTextComponent());
                        }
                    } else {
                        if(DeathMessages.discordBotAPIExtension != null && !discordSent){
                            DeathMessages.discordBotAPIExtension.sendTameableDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()), e.getTameable());
                            discordSent = true;
                        }
                        if(DeathMessages.discordSRVExtension != null && !discordSent){
                            DeathMessages.discordSRVExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                            discordSent = true;
                        }
                        if (pms.getMessagesEnabled()) {
                            if(DeathMessages.worldGuardExtension != null){
                                if(DeathMessages.worldGuardExtension.getRegionState(pls, e.getMessageType()).equals(StateFlag.State.DENY)){
                                    return;
                                }
                            }
                            pls.spigot().sendMessage(e.getTextComponent());
                        }
                    }
                }
            }
        }
    }
}
