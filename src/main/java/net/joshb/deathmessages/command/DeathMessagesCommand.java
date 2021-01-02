package net.joshb.deathmessages.command;

import org.bukkit.entity.Player;

public abstract class DeathMessagesCommand {

    public abstract String command();

    public abstract void onCommand(Player p, String[] args);
}
