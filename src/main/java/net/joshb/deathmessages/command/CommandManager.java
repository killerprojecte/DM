package net.joshb.deathmessages.command;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final List<DeathMessagesCommand> commands = new ArrayList<>();

    public void initializeSubCommands(){
        commands.add(new CommandToggle());
        commands.add(new CommandReload());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Player-Only-Command"));
            return false;
        }
        Player p = (Player) sender;
        if(!p.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())){
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        if(args.length == 0){
            for(String s : Assets.formatMessage(
                    Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help"))){
                p.sendMessage(s);
            }
        } else {
            DeathMessagesCommand cmd = get(args[0]);
            if (!(cmd == null)) {
                ArrayList<String> a = new ArrayList<String>(Arrays.asList(args));
                a.remove(0);
                args = a.toArray(new String[a.size()]);
                cmd.onCommand(p, args);
                return false;
            }

        }
        return false;
    }

    private DeathMessagesCommand get(String name) {
        for (DeathMessagesCommand cmd : commands) {
            if (cmd.command().equalsIgnoreCase(name))
                return cmd;
        }
        return null;
    }
}
