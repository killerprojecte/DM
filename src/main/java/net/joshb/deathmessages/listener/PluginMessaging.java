package net.joshb.deathmessages.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.config.Settings;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.logging.Level;

public class PluginMessaging implements PluginMessageListener {

    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] messageBytes) {
        if (!channel.equals("BungeeCord")) return;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(messageBytes));
            String subChannel;
            subChannel = in.readUTF();
            if (subChannel.equals("GetServer")) {
                String serverName;
                serverName = in.readUTF();
                DeathMessages.plugin.getLogger().log(Level.INFO, "Server-Name successfully initialized from Bungee! (" + serverName + ")");
                DeathMessages.bungeeServerName = serverName;
                DeathMessages.bungeeServerNameRequest = false;
            }
            if (subChannel.equals("DeathMessages")) {
                String serverName;
                serverName = in.readUTF();

                Bukkit.broadcastMessage("server name = " + serverName);

                Bukkit.broadcastMessage("Extra? = "+ in.readShort());

                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                try {
                    String message = msgin.readUTF();
                    for (Player pls : Bukkit.getServer().getOnlinePlayers()) {
                        //PlayerManager pms = PlayerManager.getPlayer(pls);
                        //if (pms.getMessagesEnabled()) {
                        pls.spigot().sendMessage(TextComponent.fromLegacyText(message.replaceAll("%server_name%", serverName)));
                        //}
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendServerNameRequest(Player p) {
        if (!Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Enabled")) return;
        DeathMessages.plugin.getLogger().log(Level.INFO, "Attempting to initialize server-name variable from Bungee...");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        p.sendPluginMessage(DeathMessages.plugin, "BungeeCord", out.toByteArray());
    }

    public static void sendPluginMSG(Player p, String msg) {

    }

    public static void sendPluginOLDMSG(Player p, String msg) {
        if (!Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Enabled")) return;
        if (Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Server-Groups.Enabled")) {
            List<String> serverList = Settings.getInstance().getConfig().getStringList("Hooks.Bungee.Server-Groups.Servers");
            for (String server : serverList) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Forward");
                out.writeUTF(server);
                out.writeUTF("DeathMessages");
                out.writeUTF(DeathMessages.bungeeServerName);

                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);
                try {
                    msgout.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());

                p.sendPluginMessage(DeathMessages.plugin, "BungeeCord", out.toByteArray());
            }
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("DeathMessages");
            out.writeUTF(DeathMessages.bungeeServerName);

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.broadcastMessage("length: " + msgbytes.toByteArray().length);

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());

            p.sendPluginMessage(DeathMessages.plugin, "BungeeCord", out.toByteArray());
        }
    }
}
