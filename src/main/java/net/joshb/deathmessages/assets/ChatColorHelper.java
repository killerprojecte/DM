package net.joshb.deathmessages.assets;

import net.joshb.deathmessages.DeathMessages;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColorHelper {

    public static String colorize(String message) {
        if (DeathMessages.majorVersion() >= 16) {

            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
            message.replace('&', ChatColor.COLOR_CHAR);
            return message;
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }
}
