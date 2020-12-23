package me.Joshb.DeathMessages.Config;

import me.Joshb.DeathMessages.DeathMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayerDeathMessages {

    private String fileName = "PlayerDeathMessages";

    FileConfiguration config;

    File file;

    public PlayerDeathMessages(){ }
    private static PlayerDeathMessages instance = new PlayerDeathMessages();
    public static PlayerDeathMessages getInstance(){
        return instance;
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void save(){
        try {
           //ConfigUpdater.update(DeathMessages.plugin, fileName + ".yml", file, Arrays.asList());
            config.save(file);
        } catch (Exception e){
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
            file.getParentFile().mkdirs();
            copy(DeathMessages.plugin.getResource(fileName + ".yml"), file);
        }
        config = YamlConfiguration.loadConfiguration(file);
        save();
        reload();
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
