package net.joshb.deathmessages.command;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.*;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.entity.Player;

public class CommandReload extends DeathMessagesCommand {


    @Override
    public String command() {
        return "reload";
    }

    @Override
    public void onCommand(Player p, String[] args) {
        if(!p.hasPermission(Permission.DEATHMESSAGES_COMMAND_RELOAD.getValue())){
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        EntityDeathMessages.getInstance().reload();
        Gangs.getInstance().reload();
        Messages.getInstance().reload();
        PlayerDeathMessages.getInstance().reload();
        Settings.getInstance().reload();
        p.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Reload.Reloaded"));
    }
}
