package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.assets.Assets;
import org.bukkit.entity.Player;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.bukkit.Bukkit;

@Plugin(name = "Log4JAppender", category = "Core", elementType = "appender", printObject = true)
public class SupportLogger extends AbstractAppender {

    public SupportLogger() {
        super("Log4JAppender", null, null);
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    public void append(LogEvent e) {
        if (!e.getLevel().equals(Level.ERROR) || e.getLevel().equals(Level.WARN)) {
            return;
        }

        final String Title = e.getMessage().getFormattedMessage();

        if (!Title.contains("TowerDefence")) {
            // return;
        }

       for(Player p : Bukkit.getOnlinePlayers()){
           if(p.isOp()){
               p.sendMessage(Assets.formatString("&cPlugin Error &7[Start] &8---------------"));

               if (e.getThrown().getCause() != null) {
                   p.sendMessage(Assets.formatString("&5Caused By: &d" + e.getThrown().getCause().toString()));
               } else {
                   p.sendMessage(Assets.formatString("&5Caused By: &dUNKNOWN"));
               }

               p.sendMessage("");

               for (final StackTraceElement I : e.getThrown().getCause().getStackTrace()) {

                   final int Line = I.getLineNumber();
                   final String FileName = I.getFileName();
                   final String Classname = I.getClassName();
                   final String Method = I.getMethodName() + "()";

                   if (Line < 0) {
                       continue;
                   }

                   if (FileName == null) {
                       continue;
                   }

                   if (Classname.contains("net.minecraft") || Classname.contains("org.bukkit")) {
                       continue;
                   }

                   p.sendMessage(Assets.formatString("&8" + FileName + ".&2&o" + Method + "  &8[&a" + Line + "&8]"));
               }

               p.sendMessage("");
               p.sendMessage(Assets.formatString("&cPlugin Error &7[End] &8---------------"));
           }
       }
    }


}
