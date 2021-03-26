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
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.awt.*;
import java.lang.reflect.Field;
import java.time.Instant;

public class DiscordSRVExtension {

    public DiscordSRVExtension() {

    }


    public void sendDiscordMessage(PlayerManager pm, MessageType messageType, String message){
        String channelID = DiscordAssets.getInstance().getChannelID(messageType);
        if(DiscordAssets.getInstance().getChannelID(messageType).equals("0")) return;
        Guild g = DiscordUtil.getJda().getGuilds().get(0);
        TextChannel textChannel = g.getTextChannelById(channelID);
        if(textChannel == null) return;
        if(getMessages().getBoolean("Discord.DeathMessage.Remove-Plugin-Prefix")){
            String prefix = Assets.colorize(getMessages().getString("Prefix"));
            prefix = ChatColor.stripColor(prefix);
            prefix = prefix.replaceAll("[\\[\\](){}]","");
            message = message.replaceAll(prefix, "");
        }
        if(getMessages().getString("Discord.DeathMessage.Text").equalsIgnoreCase("")){
            textChannel.sendMessage(deathMessageToDiscordMessage(pm, message)).queue();
        } else {
            String[] spl = getMessages().getString("Discord.DeathMessage.Text").split("\\\\n");
            StringBuilder sb = new StringBuilder();
            for(String s : spl){
                sb.append(s + "\n");
            }
            if (pm.getLastEntityDamager() instanceof FallingBlock) {
                textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                        null).replaceAll("%message%", message)).queue();
            } else {
                textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                        (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message)).queue();
            }
        }
    }

    public void sendTameableDiscordMessage(PlayerManager pm, MessageType messageType, String message, Tameable tameable){
        String channelID = DiscordAssets.getInstance().getChannelID(messageType);
        if(DiscordAssets.getInstance().getChannelID(messageType).equals("0")) return;
        Guild g = DiscordUtil.getJda().getGuilds().get(0);
        TextChannel textChannel = g.getTextChannelById(channelID);
        if(textChannel == null) return;
        if(getMessages().getBoolean("Discord.DeathMessage.Remove-Plugin-Prefix")){
            String prefix = Assets.colorize(getMessages().getString("Prefix"));
            prefix = ChatColor.stripColor(prefix);
            prefix = prefix.replaceAll("[\\[\\](){}]","");
            message = message.replaceAll(prefix, "");
        }
        if(getMessages().getString("Discord.DeathMessage.Text").equalsIgnoreCase("")){
            textChannel.sendMessage(deathMessageToDiscordMessage(pm, message, tameable)).queue();
        } else {
            String[] spl = getMessages().getString("Discord.DeathMessage.Text").split("\\\\n");
            StringBuilder sb = new StringBuilder();
            for(String s : spl){
                sb.append(s + "\n");
            }
            if (pm.getLastEntityDamager() instanceof FallingBlock) {
                textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                        null).replaceAll("%message%", message)).queue();
            } else {
                textChannel.sendMessage(Assets.playerDeathPlaceholders(sb.toString(), pm,
                        (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message)).queue();
            }
        }
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
        String name = getMessages().getString("Discord.DeathMessage.Author.Name").replaceAll("%message%", message);
        String url = getMessages().getString("Discord.DeathMessage.Author.URL").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        String iconURL = getMessages().getString("Discord.DeathMessage.Author.Icon-URL").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        if(!url.startsWith("http") && iconURL.startsWith("http")){
            eb.setAuthor(name, null, iconURL);
        } else if(url.startsWith("http") && !iconURL.startsWith("http")){
            eb.setAuthor(name, url);
        } else if(!url.startsWith("http") && !iconURL.startsWith("http")){
            eb.setAuthor(name);
        } else if(name.equalsIgnoreCase("")){

        } else {
            eb.setAuthor(name, url, iconURL);
        }

        if(getMessages().getString("Discord.DeathMessage.Image").startsWith("http")){
            eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image").replaceAll("%uuid%",
                    pm.getUUID().toString()).replaceAll("%username%", pm.getName()));
        }
        String title = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Title"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        if(!title.equalsIgnoreCase("")){
            eb.setTitle(title);
        }
        String description = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        if(!description.equalsIgnoreCase("")){
            eb.setDescription(description);
        }
        String footerText = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Text"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        String footerIcon = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Icon-URL"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message).replaceAll("%uuid%", pm.getUUID().toString());
        if(!footerText.equalsIgnoreCase("") && footerIcon.startsWith("http")){
            eb.setFooter(footerText, footerIcon);
        } else if(!footerText.equalsIgnoreCase("") && !footerIcon.startsWith("http")){
            eb.setFooter(footerText);
        }
        boolean timeStamp = getMessages().getBoolean("Discord.DeathMessage.Timestamp");
        if(timeStamp){
            eb.setTimestamp(Instant.now());
        }
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
        String name = getMessages().getString("Discord.DeathMessage.Author.Name").replaceAll("%message%", message);
        String url = getMessages().getString("Discord.DeathMessage.Author.URL").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        String iconURL = getMessages().getString("Discord.DeathMessage.Author.Icon-URL").replaceAll("%uuid%",
                pm.getUUID().toString()).replaceAll("%username%", pm.getName());
        if(!url.startsWith("http") && iconURL.startsWith("http")){
            eb.setAuthor(name, null, iconURL);
        } else if(url.startsWith("http") && !iconURL.startsWith("http")){
            eb.setAuthor(name, url);
        } else if(!url.startsWith("http") && !iconURL.startsWith("http")){
            eb.setAuthor(name);
        } else if(name.equalsIgnoreCase("")){

        } else {
            eb.setAuthor(name, url, iconURL);
        }

        if(getMessages().getString("Discord.DeathMessage.Image").startsWith("http")){
            eb.setThumbnail(getMessages().getString("Discord.DeathMessage.Image").replaceAll("%uuid%",
                    pm.getUUID().toString()).replaceAll("%username%", pm.getName()));
        }
        String title = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Title"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        if(!title.equalsIgnoreCase("")){
            eb.setTitle(title);
        }
        String description = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Description"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        if(!description.equalsIgnoreCase("")){
            eb.setDescription(description);
        }
        String footerText = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Text"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message);
        String footerIcon = Assets.playerDeathPlaceholders(getMessages().getString("Discord.DeathMessage.Footer.Icon-URL"), pm,
                (LivingEntity) pm.getLastEntityDamager()).replaceAll("%message%", message).replaceAll("%uuid%", pm.getUUID().toString());
        if(!footerText.equalsIgnoreCase("") && footerIcon.startsWith("http")){
            eb.setFooter(footerText, footerIcon);
        } else if(!footerText.equalsIgnoreCase("") && !footerIcon.startsWith("http")){
            eb.setFooter(footerText);
        }
        boolean timeStamp = getMessages().getBoolean("Discord.DeathMessage.Timestamp");
        if(timeStamp){
            eb.setTimestamp(Instant.now());
        }
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
