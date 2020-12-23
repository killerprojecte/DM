package me.Joshb.DeathMessages.Listener;

import me.Joshb.DeathMessages.Config.PlayerDeathMessages;
import me.Joshb.DeathMessages.Manager.PlayerManager;
import org.bukkit.Nameable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(PlayerManager.getPlayer(p) == null) new PlayerManager(p.getUniqueId(), p.getName(), p.getDisplayName());
        List<String> list = new ArrayList<>();
        for(EntityType et : EntityType.values()){
            if(et.isAlive()){
                List<String> proj = new ArrayList<>();
                proj.add("%player% was shot by a %killer% somehow using %weapon%");
                PlayerDeathMessages.getInstance().getConfig().set(
                        "Mobs." + et.getEntityClass().getSimpleName().toLowerCase() + ".Solo.Projectile",
                        proj);

                List<String> wea = new ArrayList<>();
                wea.add("%player% was slained by a %killer% somehow using %weapon%");
                PlayerDeathMessages.getInstance().getConfig().set(
                        "Mobs." + et.getEntityClass().getSimpleName().toLowerCase() + ".Solo.Weapon",
                        wea);

                List<String> mee = new ArrayList<>();
                mee.add("%player% was slained by a %killer%");
                PlayerDeathMessages.getInstance().getConfig().set(
                        "Mobs." + et.getEntityClass().getSimpleName().toLowerCase() + ".Solo.Melee",
                        mee);

                List<String> projg = new ArrayList<>();
                projg.add("%player% was ganged up on by some %killer%! One shot them with %weapon%");
                PlayerDeathMessages.getInstance().getConfig().set(
                        "Mobs." + et.getEntityClass().getSimpleName().toLowerCase() + ".Gang.Projectile",
                        projg);

                List<String> weag = new ArrayList<>();
                weag.add("%player% was ganed up on by some %killer%'s! One wacked them with %weapon%");
                PlayerDeathMessages.getInstance().getConfig().set(
                        "Mobs." + et.getEntityClass().getSimpleName().toLowerCase().toLowerCase() + ".Gang.Weapon",
                        weag);

                List<String> meeg = new ArrayList<>();
                meeg.add("%player% was slained by a bunch of %killer%'s");
                PlayerDeathMessages.getInstance().getConfig().set(
                        "Mobs." + et.getEntityClass().getSimpleName().toLowerCase() + ".Gang.Melee",
                        meeg);

                PlayerDeathMessages.getInstance().save();
            }
        }
        System.out.println(list.toString());
    }
}
