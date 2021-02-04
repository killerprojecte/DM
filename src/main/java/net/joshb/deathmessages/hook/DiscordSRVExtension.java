package net.joshb.deathmessages.hook;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.awt.*;
import java.lang.reflect.Field;

public class DiscordSRVExtension {

    public DiscordSRVExtension() {

    }


    public void sendDiscordMessage(PlayerManager pm, MessageType messageType, String message){
        String channelID = DiscordAssets.getInstance().getChannelID(messageType);
        if(DiscordAssets.getInstance().getChannelID(messageType).equals("0")) return;
        Guild g = DiscordUtil.getJda().getGuilds().get(0);
        TextChannel textChannel = g.getTextChannelById(channelID);
        if(textChannel == null) return;
        textChannel.sendMessage(deathMessageToDiscordMessage(pm, message)).queue();
    }

    public void sendTameableDiscordMessage(PlayerManager pm, MessageType messageType, String message, Tameable tameable){
        String channelID = DiscordAssets.getInstance().getChannelID(messageType);
        if(DiscordAssets.getInstance().getChannelID(messageType).equals("0")) return;
        Guild g = DiscordUtil.getJda().getGuilds().get(0);
        TextChannel textChannel = g.getTextChannelById(channelID);
        if(textChannel == null) return;
        textChannel.sendMessage(deathMessageToDiscordMessage(pm, message, tameable)).queue();
    }

    public MessageEmbed deathMessageToDiscordMessage(PlayerManager pm, String message) {
        EmbedBuilder eb = new EmbedBuilder();
        Color color;
        try {
            Field field = Class.forName("java.awt.Color").getField(getMessages().getString("Discord.DeathMessage.Color"));
            color = (Color) field.get(null);
        } catch (Exception e) {
            color = null;
        }
        eb.setColor(color);
        eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName()));
        eb.setTitle(Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Title"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message));
        eb.setDescription(Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message));
        String footer = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        eb.setFooter(footer);
        for (String s : getMessages().getStringList("Discord.DeathMessage.Content")) {
            String[] conSpl = s.split("\\|");
            if (s.startsWith("break")) {
                boolean b = Boolean.parseBoolean(conSpl[1]);
                eb.addBlankField(b);
            } else {
                boolean b = Boolean.parseBoolean(conSpl[2]);
                String header = Assets.playerDeathPlaceholders(conSpl[0], pm,
                        (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
                String subHeader = Assets.playerDeathPlaceholders(conSpl[1], pm,
                        (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
                eb.addField(header, subHeader, b);
            }
        }
        return eb.build();
    }

    public MessageEmbed deathMessageToDiscordMessage(PlayerManager pm, String message, Tameable tameable) {
        EmbedBuilder eb = new EmbedBuilder();
        Color color;
        try {
            Field field = Class.forName("java.awt.Color").getField(getMessages().getString("Discord.DeathMessage.Color"));
            color = (Color) field.get(null);
        } catch (Exception e) {
            color = null;
        }
        eb.setColor(color);
        eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName()));
        eb.setTitle(Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Title"), pm,
                tameable).replaceAll("%message%", message));
        eb.setDescription(Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), pm,
                tameable).replaceAll("%message%", message));
        String footer = Assets.entityDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer"), pm,
                tameable).replaceAll("%message%", message);
        eb.setFooter(footer);
        for (String s : getMessages().getStringList("Discord.DeathMessage.Content")) {
            String[] conSpl = s.split("\\|");
            if (s.startsWith("break")) {
                boolean b = Boolean.parseBoolean(conSpl[1]);
                eb.addBlankField(b);
            } else {
                boolean b = Boolean.parseBoolean(conSpl[2]);
                String header = Assets.entityDeathPlaceholders(conSpl[0], pm,
                        tameable).replaceAll("%message%", message);
                String subHeader = Assets.entityDeathPlaceholders(conSpl[1], pm,
                        tameable).replaceAll("%message%", message);
                eb.addField(header, subHeader, b);
            }
        }
        return eb.build();
    }

    private FileConfiguration getMessages() {
        return Messages.getInstance().getConfig();
    }
}
