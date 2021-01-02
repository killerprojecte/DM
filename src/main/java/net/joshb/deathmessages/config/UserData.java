package net.joshb.deathmessages.config;

import net.joshb.deathmessages.DeathMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class UserData {

    private String fileName = "UserData";

    FileConfiguration config;

    File file;

    public UserData(){ }
    private static UserData instance = new UserData();
    public static UserData getInstance(){
        return instance;
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void save(){
        try {
            config.save(file);
        } catch (IOException e){
            System.out.println("COULD NOT SAVE FILE: " +fileName);
            e.printStackTrace();
        }
    }

    public void reload(){
        try {
            config.load(file);
        } catch (Exception e){
            System.out.println("COULD NOT RELOAD FILE: " + fileName);
            e.printStackTrace();
        }
    }

    public void initialize(){
        if (!DeathMessages.plugin.getDataFolder().exists()) {
            DeathMessages.plugin.getDataFolder().mkdir();
        }

        file = new File(DeathMessages.plugin.getDataFolder(), fileName + ".yml");

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        save();
        reload();
    }
}
