package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.PlayerDeathMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class OnChat implements Listener {


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Assets.addingMessage.containsKey(p.getName())) {
            e.setCancelled(true);
            String args = Assets.addingMessage.get(p.getName());
            Assets.addingMessage.remove(p.getName());
            String[] spl = args.split(":");
            String mode = spl[0];
            String mobName = spl[1];
            String damageType = spl[2];
            List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + "." + mode + "." + damageType);
            list.add(e.getMessage());
            PlayerDeathMessages.getInstance().getConfig().set("Mobs." + mobName + "." + mode + "." + damageType, list);
            PlayerDeathMessages.getInstance().save();
            PlayerDeathMessages.getInstance().reload();
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Added-Message")
                    .replaceAll("%message%", e.getMessage())
                    .replaceAll("%mob_name%", mobName)
                    .replaceAll("%mode%", mode)
                    .replaceAll("%damage_type%", damageType));
        }
    }
}
