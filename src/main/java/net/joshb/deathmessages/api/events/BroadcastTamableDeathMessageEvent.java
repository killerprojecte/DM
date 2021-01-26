package net.joshb.deathmessages.api.events;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class BroadcastTamableDeathMessageEvent extends Event implements Cancellable {

    //The killer
    private final Player player;
    //The owner of the tameable
    private final String owner;
    //The entity that was killed
    private final Tameable tameable;
    private final TextComponent textComponent;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public BroadcastTamableDeathMessageEvent(Player player, String owner, Tameable tameable, TextComponent textComponent) {
        this.player = player;
        this.owner = owner;
        this.tameable = tameable;
        this.textComponent = textComponent;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getOwner(){
        return this.owner;
    }

    public Tameable getLivingEntity(){
        return this.tameable;
    }

    public TextComponent getTextComponent() {
        return this.textComponent;
    }

}
