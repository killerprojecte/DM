package net.joshb.deathmessages.hook;

import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.configuration.file.FileConfiguration;

public class DiscordAssets {

    public DiscordAssets() {
    }

    private static DiscordAssets instance = new DiscordAssets();

    public static DiscordAssets getInstance() {
        return instance;
    }

    public String getChannelID(MessageType messageType) {
        switch (messageType) {
            case PLAYER:
                return getSettings().getString("Hooks.Discord.Channels.Player.Channel-ID");
            case MOB:
                return getSettings().getString("Hooks.Discord.Channels.Mob.Channel-ID");
            case NATURAL:
                return getSettings().getString("Hooks.Discord.Channels.Natural.Channel-ID");
            case TAMEABLE:
                return getSettings().getString("Hooks.Discord.Channels.Tameable.Channel-ID");
            default:
                return null;
        }
    }

    private FileConfiguration getSettings() {
        return Settings.getInstance().getConfig();
    }
}
