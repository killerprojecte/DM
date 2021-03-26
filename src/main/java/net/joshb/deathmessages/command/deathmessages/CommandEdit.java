package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.PlayerDeathMessages;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.List;

public class CommandEdit extends DeathMessagesCommand {

    @Override
    public String command() {
        return "edit";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // /dm edit <player> <mobName> <solo, gang> <damage type> <add, remove, list> (add=message, remove=placeholder, list=noArgs)
        if(!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_EDIT.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if(args.length <= 3){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Usage"));
        } else {
            if(args[0].equalsIgnoreCase("player")){
                String mobName = args[1];
                String damageType = args[3];
                boolean exists = false;
                for(EntityType entityType : EntityType.values()){
                    //Check isAlive as getSimpleName could be null if the entity is not living
                    if(entityType.isAlive() && entityType.getEntityClass().getSimpleName().toLowerCase().equalsIgnoreCase(mobName.toLowerCase())){
                        exists = true;
                    }
                }
                if(!exists){
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Mob-Type"));
                    return;
                }
                if(!Assets.damageTypes.contains(damageType)){
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Damage-Type"));
                    return;
                }
                if(args[4].equalsIgnoreCase("add")){
                    if(args[5] == null){
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                        return;
                    }
                    if(args[2].equalsIgnoreCase("solo")){

                    }
                } else if(args[3].equalsIgnoreCase("remove")){
                    if(args[5] == null){
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                        return;
                    }


                } else if(args[4].equalsIgnoreCase("list")){
                    int placeholder = 1;
                    if(args[2].equalsIgnoreCase("solo")){
                        System.out.println("Looking for Mobs." + mobName + ".Solo." + damageType);
                        List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Solo." + damageType);
                        for(String messages : list){
                            sender.sendMessage("[" + placeholder + "] " + Assets.formatString(messages));
                            placeholder++;
                        }
                    } else if(args[2].equalsIgnoreCase("gang")){
                        System.out.println("Looking for Mobs." + mobName + ".Gang." + damageType);
                        List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Gang." + damageType);
                        for(String messages : list){
                            sender.sendMessage("[" + placeholder + "] " + Assets.formatString(messages));
                            placeholder++;
                        }
                    } else {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                    }
                } else {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                }
            }
        }
    }
}
