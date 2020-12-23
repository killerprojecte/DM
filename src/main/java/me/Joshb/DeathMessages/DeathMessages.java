package me.Joshb.DeathMessages;

import me.Joshb.DeathMessages.Config.*;
import me.Joshb.DeathMessages.Listener.*;
import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathMessages extends JavaPlugin {

    public static DeathMessages plugin;

    public void onEnable(){
        plugin = this;
        initializeConfigs();
        initializeListeners();
        initializeOnlinePlayers();

        for(Player p : Bukkit.getOnlinePlayers()){
            new PlayerManager(p.getUniqueId(), p.getName(), p.getDisplayName());
        }
    }

    public void onDisable(){


        plugin = null;
    }

    private void initializeConfigs(){
       // EntityDeathMessages.getInstance().initialize();
        Messages.getInstance().initialize();
        PlayerDeathMessages.getInstance().initialize();
        Settings.getInstance().initialize();
    }

    private void initializeListeners(){
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
    }

    public void initializeOnlinePlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            new PlayerManager(p.getUniqueId(), p.getName(), p.getDisplayName());
        }
    }
}
