package net.joshb.deathmessages;

import net.joshb.deathmessages.command.CommandManager;
import net.joshb.deathmessages.config.*;
import net.joshb.deathmessages.listener.*;
import net.joshb.deathmessages.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DeathMessages extends JavaPlugin {

    public static DeathMessages plugin;

    public boolean placeholderAPIEnabled = false;

    public void onEnable(){
        plugin = this;
        initializeConfigs();
        initializeListeners();
        initializeCommands();
        initializeOnlinePlayers();
        //initializeHooks();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPIEnabled = true;
        }
    }

    public void onDisable(){


        plugin = null;
    }

    private void initializeConfigs(){
        EntityDeathMessages.getInstance().initialize();
        Gangs.getInstance().initialize();
        Messages.getInstance().initialize();
        PlayerDeathMessages.getInstance().initialize();
        Settings.getInstance().initialize();
        UserData.getInstance().initialize();
    }

    private void initializeListeners(){
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
    }

    private void initializeCommands(){
        CommandManager cm = new CommandManager();
        cm.initializeSubCommands();
        getCommand("deathmessages").setExecutor(cm);
    }

    private void initializeHooks(){
        if (Bukkit.getPluginManager().isPluginEnabled("PlugMan")) {
            Plugin plugMan = Bukkit.getPluginManager().getPlugin("PlugMan");
            try {
                List<String> ignoredPlugins = (List<String>) plugMan.getClass().getMethod("getIgnoredPlugins").invoke(plugMan);
                if (!ignoredPlugins.contains("DeathMessages")) {
                    ignoredPlugins.add("DeathMessages");
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {}
        }
    }

    public void initializeOnlinePlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            new PlayerManager(p);
        }
    }
}
