package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;

public class CommandSalt extends DeathMessagesCommand {


    @Override
    public String command() {
        return "saltIsABitch";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_RELOAD.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }

    }
}
