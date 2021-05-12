package net.joshb.deathmessages.listener.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastTamableDeathMessageEvent;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.regex.Matcher;

public class BroadcastTameableDeathListener implements Listener {

    @EventHandler
    public void broadcastListener(BroadcastTamableDeathMessageEvent e) {
        if (!e.isCancelled()) {
            if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
                String message = Assets.entityDeathPlaceholders(Messages.getInstance().getConfig().getString("Console.Message"), PlayerManager.getPlayer(e.getPlayer()), e.getTameable());
                message = message.replaceAll("%message%", Matcher.quoteReplacement(e.getTextComponent().toLegacyText()));
                Bukkit.getConsoleSender().sendMessage(message);
            }

            PlayerManager pm = PlayerManager.getPlayer(e.getPlayer());
            if(pm.isInCooldown()){
                return;
            } else {
                pm.setCooldown();
            }

            boolean discordSent = false;

            boolean privateTameable = Settings.getInstance().getConfig().getBoolean("Private-Messages.Tameable");

            for(World w : e.getBroadcastedWorlds()){
                for(Player pls : w.getPlayers()){
                    if(Settings.getInstance().getConfig().getStringList("Disabled-Worlds").contains(w.getName())){
                        continue;
                    }
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
                            DeathMessages.discordSRVExtension.sendTameableDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()), e.getTameable());
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
