package net.joshb.deathmessages;

import net.joshb.deathmessages.assets.PlaceholderAPIExtension;
import net.joshb.deathmessages.command.CommandManager;
import net.joshb.deathmessages.config.*;
import net.joshb.deathmessages.listener.*;
import net.joshb.deathmessages.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class DeathMessages extends JavaPlugin {

    public static DeathMessages plugin;

    public boolean placeholderAPIEnabled = false;
    public static boolean worldGuardEnabled = false;

    public static EventPriority eventPriority = EventPriority.HIGH;


    public void onEnable(){
        plugin = this;
        partner();
        initializeConfigs();
        initializeListeners();
        initializeCommands();
        initializeOnlinePlayers();
        initializeHooks();
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
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new Explosion(), this);
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
        /*
        if (Bukkit.getPluginManager().isPluginEnabled("PlugMan")) {
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
         */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExtension(this).register();
            placeholderAPIEnabled = true;
            getLogger().log(Level.INFO, "PlaceholderAPI found. Enabling Hook.");
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardEnabled = true;
            getLogger().log(Level.INFO, "WorldGuard found. Enabling Hook.");
        }
    }

    public void partner(){
        getLogger().log(Level.INFO, "Partnered with Sparked Host");
        getLogger().log(Level.INFO, "Grab a server today with the code `Josh` for 15% off @ https://sparkedhost.com");
    }

    public void initializeOnlinePlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            new PlayerManager(p);
        }
    }
}
