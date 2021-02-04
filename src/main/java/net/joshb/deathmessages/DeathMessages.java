package net.joshb.deathmessages;

import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.command.CommandManager;
import net.joshb.deathmessages.config.*;
import net.joshb.deathmessages.hook.DiscordBotAPIExtension;
import net.joshb.deathmessages.hook.DiscordSRVExtension;
import net.joshb.deathmessages.hook.PlaceholderAPIExtension;
import net.joshb.deathmessages.hook.WorldGuardExtension;
import net.joshb.deathmessages.listener.*;
import net.joshb.deathmessages.listener.customlisteners.BroadcastPlayerDeathListener;
import net.joshb.deathmessages.listener.customlisteners.BroadcastTameableDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

public class DeathMessages extends JavaPlugin {

    public static DeathMessages plugin;

    public boolean placeholderAPIEnabled = false;

    public static WorldGuardExtension worldGuardExtension;
    public static int worldGuardVersion;

    public static DiscordBotAPIExtension discordBotAPIExtension;
    public static DiscordSRVExtension discordSRVExtension;

    public static EventPriority eventPriority = EventPriority.HIGH;


    public void onEnable(){
        plugin = this;
        partner();
        initializeConfigs();
        initializeListeners();
        initializeCommands();
        initializeHooks();
    }

    public void onLoad(){
        initializeOnlinePlayers();
        initializeHooksOnLoad();
    }

    public void onDisable(){

        plugin = null;
    }

    public static String serverVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public static int majorVersion(){
        return Integer.parseInt(serverVersion().replace("1_", "").replaceAll("_R\\d", "").replaceAll("v", ""));
    }

    public static int worldGuardVersion(){
        return worldGuardVersion;
    }

    private void initializeConfigs(){
        EntityDeathMessages.getInstance().initialize();
        Gangs.getInstance().initialize();
        Messages.getInstance().initialize();
        PlayerDeathMessages.getInstance().initialize();
        Settings.getInstance().initialize();
        UserData.getInstance().initialize();

        String eventPri = Settings.getInstance().getConfig().getString("Death-Listener-Priority");
        if(eventPri.equalsIgnoreCase("LOWEST")){
            eventPriority = EventPriority.LOWEST;
        } else if(eventPri.equalsIgnoreCase("LOW")){
            eventPriority = EventPriority.LOW;
        } else if(eventPri.equalsIgnoreCase("NORMAL")){
            eventPriority = EventPriority.NORMAL;
        } else if(eventPri.equalsIgnoreCase("HIGH")){
            eventPriority = EventPriority.HIGH;
        } else if(eventPri.equalsIgnoreCase("HIGHEST")){
            eventPriority = EventPriority.HIGHEST;
        } else if(eventPri.equalsIgnoreCase("MONITOR")){
            eventPriority = EventPriority.HIGH;
        } else {
            eventPriority = EventPriority.HIGH;
        }
    }

    private void initializeListeners(){
        //Custom Events
        Bukkit.getPluginManager().registerEvents(new BroadcastPlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new BroadcastTameableDeathListener(), this);
        //Bukkits events
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
        Bukkit.getPluginManager().registerEvents(new OnMove(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
    }

    private void initializeCommands(){
        CommandManager cm = new CommandManager();
        cm.initializeSubCommands();
        getCommand("deathmessages").setExecutor(cm);
    }

    private void initializeHooks(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExtension(this).register();
            placeholderAPIEnabled = true;
            getLogger().log(Level.INFO, "PlaceholderAPI found. Enabling Hook.");
        }

        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null &&
                Settings.getInstance().getConfig().getBoolean("Hooks.WorldGuard.Enabled")){
            worldGuardExtension = null;
        } else {
            if(worldGuardExtension != null){
                getLogger().log(Level.INFO, "WorldGuard found. Enabling Hook.");
                worldGuardExtension.enable();
            }
        }

        if(Bukkit.getPluginManager().getPlugin("DiscordBotAPI") != null
                && Settings.getInstance().getConfig().getBoolean("Hooks.Discord.Enabled")){
            discordBotAPIExtension = new DiscordBotAPIExtension();
            getLogger().log(Level.INFO, "DiscordBotAPI found. Enabling Hook.");
        }

        if(Bukkit.getPluginManager().getPlugin("DiscordSRV") != null
                && Settings.getInstance().getConfig().getBoolean("Hooks.Discord.Enabled")){
            discordSRVExtension = new DiscordSRVExtension();
            getLogger().log(Level.INFO, "DiscordSRV found. Enabling Hook.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlugMan") && worldGuardExtension != null) {
            Plugin plugMan = Bukkit.getPluginManager().getPlugin("PlugMan");
            getLogger().log(Level.INFO, "PlugMan found. Adding this plugin to its ignored plugins list.");
            try {
                List<String> ignoredPlugins = (List<String>) plugMan.getClass().getMethod("getIgnoredPlugins").invoke(plugMan);
                if (!ignoredPlugins.contains("DeathMessages")) {
                    ignoredPlugins.add("DeathMessages");
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                getLogger().log(Level.SEVERE, "Error adding plugin to ignored plugins list: " + exception.getMessage());
            }
        }
    }

    private void initializeHooksOnLoad(){
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                (worldGuardExtension = new WorldGuardExtension()).register();
                worldGuardVersion = Integer.parseInt(Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().split("\\.")[0]);
            } catch (Exception e){
                worldGuardExtension = null;
                getLogger().log(Level.SEVERE, "Error loading WorldGuardHook. Error: " + e.getMessage());
            }
        }
    }

    private void partner(){
        getLogger().log(Level.INFO, "Partnered with Sparked Host");
        getLogger().log(Level.INFO, "Grab a server today with the code `Josh` for 15% off @ https://sparkedhost.com");
    }

    private void initializeOnlinePlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            new PlayerManager(p);
        }
    }

    public static EventPriority getEventPriority(){
        return eventPriority;
    }
}
