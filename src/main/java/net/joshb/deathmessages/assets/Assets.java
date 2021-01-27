package net.joshb.deathmessages.assets;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.config.EntityDeathMessages;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.PlayerDeathMessages;
import net.joshb.deathmessages.config.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assets {

    static boolean addPrefix = Settings.getInstance().getConfig().getBoolean("Add-Prefix-To-All-Messages");

    public static String formatMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                Messages.getInstance().getConfig().getString(path)
                        .replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix")));
    }

    public static List<String> formatMessage(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(ChatColor.translateAlternateColorCodes('&', s
                    .replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix"))));
        }
        return newList;
    }

    public static boolean isClimable(Material material) {
        return material.name().contains("LADDER")
                || material.name().contains("VINE")
                || material.equals(Material.SCAFFOLDING)
                || material.name().contains("TRAPDOOR");
    }

    public static boolean isWeapon(ItemStack itemStack) {
        return itemStack.getType().toString().contains("SHOVEL")
                || itemStack.getType().toString().contains("PICKAXE")
                || itemStack.getType().toString().contains("AXE")
                || itemStack.getType().toString().contains("HOE")
                || itemStack.getType().toString().contains("SWORD")
                || itemStack.getType().toString().contains("BOW");
    }

    static String lastColor = null;

    public static TextComponent deathMessage(PlayerManager pm, boolean gang) {
        lastColor = null;
        LivingEntity mob = (LivingEntity) pm.getLastEntityDamager();
        boolean hasWeapon;
        if (mob.getEquipment() == null) {
            hasWeapon = false;
        } else if (!isWeapon(mob.getEquipment().getItemInMainHand())) {
            hasWeapon = false;
        } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.THORNS)) {
            hasWeapon = false;
        } else {
            hasWeapon = true;
        }
        if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            if (pm.getLastExplosiveEntity() instanceof EnderCrystal) {
                return get(gang, pm, mob, "End-Crystal");
            } else if (pm.getLastExplosiveEntity() instanceof TNTPrimed) {
                return get(gang, pm, mob, "TNT");
            } else if (pm.getLastExplosiveEntity() instanceof Firework) {
                return get(gang, pm, mob, "Firework");
            }
        }
        if (hasWeapon) {
            if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                return getWeapon(gang, pm, mob);
            } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)
                    && pm.getLastProjectileEntity() instanceof Arrow) {
                return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
            } else {
                return null;
            }
        } else {
            for (EntityDamageEvent.DamageCause dc : EntityDamageEvent.DamageCause.values()) {
                if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                    return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
                }
                if (pm.getLastDamage().equals(dc)) {
                    return get(gang, pm, mob, getSimpleCause(dc));
                }
            }
            return null;
        }
    }


    public static TextComponent getNaturalDeath(PlayerManager pm, String damageCause) {
        Random random = new Random();
        List<String> msgs = filterSortList(getPlayerDeathMessages().getStringList("Natural-Cause." + damageCause), pm);
        if (msgs.isEmpty()) return null;
                String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent();
        if(addPrefix){
            String prefix = Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"));
            tc.addExtra(prefix + " ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            firstSection = sec[0];
        } else {
            firstSection = msg;
        }
        for (String splitMessage : firstSection.split(" ")) {
            if (splitMessage.contains("%block%") && pm.getLastEntityDamager() instanceof FallingBlock) {
                FallingBlock fb = (FallingBlock) pm.getLastEntityDamager();
                String mssa = Assets.colorize(splitMessage.replaceAll("%block%", convertString(fb.getBlockData().getMaterial().name())));
                tc.addExtra(mssa);
                lastColor = getColorOfString(lastColor + mssa);
            } else if(splitMessage.contains("%climbable%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
                String mssa = Assets.colorize(splitMessage.replaceAll("%climbable%", convertString(pm.getLastClimbing().toString())));
                tc.addExtra(mssa);
                lastColor = getColorOfString(lastColor + mssa);
            } else {
                if (lastColor != null) {
                    TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + splitMessage, pm, null)) + " "));
                    tc.addExtra(tx);
                    for(BaseComponent bs : tx.getExtra()){
                        lastColor = bs.getColor().toString();
                    }
                } else {
                    TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(splitMessage, pm, null)) + " "));
                    tc.addExtra(tx);
                    for(BaseComponent bs : tx.getExtra()){
                        lastColor = bs.getColor().toString();
                    }
                }
            }
        }
        if (sec.length >= 2) {
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(playerDeathPlaceholders(sec[1], pm, null))));
        }
        if (sec.length == 3) {
            if (sec[2].startsWith("COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, null)));
            } else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, null)));
            }
        }
        return tc;
    }

    public static TextComponent getWeapon(boolean gang, PlayerManager pm, LivingEntity mob) {
        Random random = new Random();
        List<String> msgs;
        if (gang) {
            msgs = filterSortList(getPlayerDeathMessages().getStringList("Mobs." +
                    mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang.Weapon"), pm);
        } else {
            msgs = filterSortList(getPlayerDeathMessages().getStringList("Mobs." +
                    mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo.Weapon"), pm);
        }
        if (msgs.isEmpty()) return null;
        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent();
        if(addPrefix){
            String prefix = Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"));
            tc.addExtra(prefix + " ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            firstSection = sec[0];
        } else {
            firstSection = msg;
        }
        for (String splitMessage : firstSection.split(" ")) {
            if (splitMessage.contains("%weapon%")) {
                ItemStack i = mob.getEquipment().getItemInMainHand();
                String displayName;
                if (!i.hasItemMeta() && !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().equals("")) {
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Enabled")) {
                        return get(gang, pm, mob, Settings.getInstance().getConfig()
                                .getString("Disable-Weapon-Kill-With-No-Custom-Name.Source.Weapon.Default-To"));
                    }
                    displayName = Assets.convertString(i.getType().name());
                } else {
                    displayName = i.getItemMeta().getDisplayName();
                }
                String[] spl = splitMessage.split("%weapon%");
                if (spl.length != 0 && spl[0] != null && !spl[0].equals("")) {
                    displayName = spl[0] + displayName;
                }
                if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].equals("")) {
                    displayName = displayName + spl[1];
                }
                TextComponent weaponComp = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(displayName)));
                BaseComponent[] hoverEventComponents = new BaseComponent[]{
                        new TextComponent(NBTItem.convertItemtoNBT(i).getCompound().toString())
                };
                weaponComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
                tc.addExtra(weaponComp);
            } else {
                if (lastColor != null) {
                    TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + splitMessage, pm, mob)) + " "));
                    tc.addExtra(tx);
                    for(BaseComponent bs : tx.getExtra()){
                        lastColor = bs.getColor().toString();
                    }
                } else {
                    TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(splitMessage, pm, mob)) + " "));
                    tc.addExtra(tx);
                    for(BaseComponent bs : tx.getExtra()){
                        lastColor = bs.getColor().toString();
                    }
                }
            }
        }
        lastColor = null;
        if (sec.length >= 2) {
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(playerDeathPlaceholders(sec[1], pm, mob))));
        }
        if (sec.length == 3) {
            if (sec[2].startsWith("COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, mob)));
            } else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, mob)));
            }
        }
        return tc;
    }

    public static TextComponent get(boolean gang, PlayerManager pm, LivingEntity mob, String damageCause) {
        Random random = new Random();
        List<String> msgs;
        if (gang) {
            msgs = filterSortList(getPlayerDeathMessages().getStringList("Mobs." +
                    mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang." + damageCause), pm);
        } else {
            msgs = filterSortList(getPlayerDeathMessages().getStringList("Mobs." +
                    mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo." + damageCause), pm);
        }

        if (msgs.isEmpty()) {
            if (Settings.getInstance().getConfig().getBoolean("Default-Natural-Death-Not-Defined")) {
                return getNaturalDeath(pm, damageCause);
            }
            if (Settings.getInstance().getConfig().getBoolean("Default-Melee-Last-Damage-Not-Defined")) {
                return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
            }
            return null;
        }

        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent();
        if(addPrefix){
            String prefix = Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"));
            tc.addExtra(prefix + " ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            firstSection = sec[0];
        } else {
            firstSection = msg;
        }
        for (String splitMessage : firstSection.split(" ")) {
            if (lastColor != null) {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + splitMessage, pm, mob)) + " "));
                tc.addExtra(tx);
                for(BaseComponent bs : tx.getExtra()){
                    lastColor = bs.getColor().toString();
                }
            } else {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(splitMessage, pm, mob)) + " "));
                tc.addExtra(tx);
                for(BaseComponent bs : tx.getExtra()){
                    lastColor = bs.getColor().toString();
                }
            }
        }
        if (sec.length >= 2) {
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(playerDeathPlaceholders(sec[1], pm, mob))));
        }
        if (sec.length == 3) {
            if (sec[2].startsWith("COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, mob)));
            } else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, mob)));
            }
        }
        return tc;
    }

    public static TextComponent getProjectile(boolean gang, PlayerManager pm, LivingEntity mob, String projectileDamage) {
        Random random = new Random();
        List<String> msgs;
        if (gang) {
            msgs = filterSortList(getPlayerDeathMessages().getStringList("Mobs." +
                    mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang." + projectileDamage), pm);
        } else {
            msgs = filterSortList(getPlayerDeathMessages().getStringList("Mobs." +
                    mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo." + projectileDamage), pm);
        }
        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent();
        if(addPrefix){
            String prefix = Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"));
            tc.addExtra(prefix + " ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            firstSection = sec[0];
        } else {
            firstSection = msg;
        }
        for (String splitMessage : firstSection.split(" ")) {
            if (splitMessage.equalsIgnoreCase("%weapon%") && pm.getLastProjectileEntity() instanceof Arrow) {
                if(mob.getEquipment().getItemInMainHand() == null){
                    continue;
                }
                ItemStack i = mob.getEquipment().getItemInMainHand();
                String displayName;
                if (!i.hasItemMeta() && !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().equals("")) {
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Enabled")) {
                        return get(gang, pm, mob, Settings.getInstance().getConfig()
                                .getString("Disable-Weapon-Kill-With-No-Custom-Name.Source.Projectile.Default-To"));
                    }
                    displayName = Assets.convertString(i.getType().name());
                } else {
                    displayName = i.getItemMeta().getDisplayName();
                }
                String[] spl = splitMessage.split("%weapon%");
                if (spl.length != 0 && spl[0] != null && !spl[0].equals("")) {
                    displayName = spl[0] + displayName;
                }
                if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].equals("")) {
                    displayName = displayName + spl[1];
                }
                displayName = Assets.colorize(displayName);
                TextComponent weaponComp = new TextComponent(displayName);
                BaseComponent[] hoverEventComponents = new BaseComponent[]{
                        new TextComponent(NBTItem.convertItemtoNBT(i).getCompound().toString())
                };
                weaponComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
                tc.addExtra(weaponComp);
            } else {
                if (lastColor != null) {
                    TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + splitMessage, pm, mob)) + " "));
                    tc.addExtra(tx);
                    for(BaseComponent bs : tx.getExtra()){
                        lastColor = bs.getColor().toString();
                    }
                } else {
                    TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(splitMessage, pm, mob)) + " "));
                    tc.addExtra(tx);
                    for(BaseComponent bs : tx.getExtra()){
                        lastColor = bs.getColor().toString();
                    }
                }
            }
        }
        if (sec.length >= 2) {
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(playerDeathPlaceholders(sec[1], pm, mob))));
        }
        if (sec.length == 3) {
            if (sec[2].startsWith("COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, mob)));
            } else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + playerDeathPlaceholders(cmd, pm, mob)));
            }
        }
        return tc;
    }

    public static TextComponent getTamable(PlayerManager pm, Tameable tameable) {
        Random random = new Random();
        List<String> msgs = filterSortList(getEntityDeathMessages().getStringList("Tamable"), pm);
        if (msgs.isEmpty()) return null;

        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent();
        if(addPrefix){
            String prefix = Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"));
            tc.addExtra(prefix + " ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            firstSection = sec[0];
        } else {
            firstSection = msg;
        }
        for (String splitMessage : firstSection.split(" ")) {
            if (lastColor != null) {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + splitMessage, pm, tameable)) + " "));
                tc.addExtra(tx);
                for(BaseComponent bs : tx.getExtra()){
                    lastColor = bs.getColor().toString();
                }
            } else {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(splitMessage, pm, tameable)) + " "));
                tc.addExtra(tx);
                for(BaseComponent bs : tx.getExtra()){
                    lastColor = bs.getColor().toString();
                }
            }
        }
        if (sec.length >= 2) {
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(entityDeathPlaceholders(sec[1], pm, tameable))));
        }
        if (sec.length == 3) {
            if (sec[2].startsWith("COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + entityDeathPlaceholders(cmd, pm, tameable)));
            } else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
                String cmd = sec[2].split(":")[1];
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + entityDeathPlaceholders(cmd, pm, tameable)));
            }
        }
        return tc;
    }

    public static List<String> filterSortList(List<String> list, PlayerManager pm){
        List<String> newList = list;
        ListMultimap<String, String> permMessages = ArrayListMultimap.create();
        List<String> permissions = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        for(String s : list){
            if(s.startsWith("PERMISSION[")){
                Matcher m = Pattern.compile("PERMISSION\\[([^)]+)\\]").matcher(s);
                while (m.find()){
                    String perm = m.group(1);
                    if(!permissions.contains(perm)){
                        permissions.add(perm);
                    }
                    permMessages.put(perm, s.replace("PERMISSION[" + perm + "]", ""));
                }
            }
        }
        for(String perm : permissions){
            if(pm.getPlayer().hasPermission(perm)){
                tempList.addAll(permMessages.get(perm));
            }
        }
        if(!tempList.isEmpty()){
            newList = tempList;
        } else {
            newList.removeIf(s -> s.startsWith("PERMISSION["));
        }
        return newList;
    }

    public static String colorize(String message) {
        if (DeathMessages.majorVersion() >= 16) {

            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
            message = message.replace('&', ChatColor.COLOR_CHAR);
            return message;
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    public static String entityDeathPlaceholders(String msg, PlayerManager killer, Tameable tameable) {
        msg = colorize(msg
                .replaceAll("%killer%", killer.getName())
                .replaceAll("%biome%", tameable.getLocation().getBlock().getBiome().name())
                .replaceAll("%world%", tameable.getLocation().getWorld().getName())
                .replaceAll("%tamable%", tameable.getType().getEntityClass().getSimpleName())
                .replaceAll("%tamable_displayname%", tameable.getName())
                .replaceAll("%owner%", tameable.getOwner().getName())
                .replaceAll("%x%", String.valueOf(tameable.getLocation().getBlock().getX()))
                .replaceAll("%y%", String.valueOf(tameable.getLocation().getBlock().getY()))
                .replaceAll("%z%", String.valueOf(tameable.getLocation().getBlock().getZ())));
        if (DeathMessages.plugin.placeholderAPIEnabled) {
            msg = PlaceholderAPI.setPlaceholders(killer.getPlayer(), msg);
        }
        return msg;
    }

    public static String playerDeathPlaceholders(String msg, PlayerManager pm, LivingEntity mob) {
        if (mob == null) {
            msg = colorize(msg
                    .replaceAll("%player%", pm.getName())
                    .replaceAll("%player_display%", pm.getDisplayName())
                    .replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name())
                    .replaceAll("%world%", pm.getLastLocation().getWorld().getName())
                    .replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
                    .replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
                    .replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ())));
            if (DeathMessages.plugin.placeholderAPIEnabled) {
                msg = PlaceholderAPI.setPlaceholders(pm.getPlayer(), msg);
            }
            return msg;
        } else {
            String mobName = mob.getName();
            if(Settings.getInstance().getConfig().getBoolean("Rename-Mobs.Enabled")){
                String[] chars = Settings.getInstance().getConfig().getString("Rename-Mobs.If-Contains").split("(?!^)");
                for (String ch : chars){
                    if(mobName.contains(ch)){
                        mobName = convertString(mob.getType().toString().toLowerCase());
                        break;
                    }
                }
            }
            if(!(mob instanceof Player) && Settings.getInstance().getConfig().getBoolean("Disable-Named-Mobs")){
                mobName = convertString(mob.getType().toString().toLowerCase());
            }
            msg = colorize(msg
                    .replaceAll("%player%", pm.getName())
                    .replaceAll("%player_display%", pm.getDisplayName())
                    .replaceAll("%killer%", mobName)
                    .replaceAll("%killer_type%", convertString(mob.getType().toString().toLowerCase()))
                    .replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name())
                    .replaceAll("%world%", pm.getLastLocation().getWorld().getName())
                    .replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
                    .replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
                    .replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ())));
            if(mob instanceof Player){
                Player p = (Player) mob;
                msg = msg.replaceAll("%killer_display%", p.getDisplayName());
            }
            if (DeathMessages.plugin.placeholderAPIEnabled) {
                msg = PlaceholderAPI.setPlaceholders(pm.getPlayer(), msg);
            }
            return msg;
        }
    }

    public static String convertString(String string) {
        string = string.replaceAll("_", " ").toLowerCase();
        String[] spl = string.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spl.length; i++) {
            if (i == spl.length - 1) {
                sb.append(StringUtils.capitalize(spl[i]));
            } else {
                sb.append(StringUtils.capitalize(spl[i]) + " ");
            }
        }
        return sb.toString();
    }

    public static String getSimpleProjectile(Projectile projectile) {
        if (projectile instanceof Arrow) {
            return "Projectile-Arrow";
        } else if (projectile instanceof DragonFireball) {
            return "Projectile-Dragon-Fireball";
        } else if (projectile instanceof Egg) {
            return "Projectile-Egg";
        } else if (projectile instanceof EnderPearl) {
            return "Projectile-EnderPearl";
        } else if (projectile instanceof Fireball) {
            return "Projectile-Fireball";
        } else if (projectile instanceof FishHook) {
            return "Projectile-FishHook";
        } else if (projectile instanceof LlamaSpit) {
            return "Projectile-LlamaSpit";
        } else if (projectile instanceof Snowball) {
            return "Projectile-Snowball";
        } else if (projectile instanceof Trident) {
            return "Projectile-Trident";
        } else if (projectile instanceof WitherSkull) {
            return "Projectile-WitherSkull";
        } else if (projectile instanceof ShulkerBullet) {
            return "Projectile-ShulkerBullet";
        } else {
            return "Projectile-Arrow";
        }
    }

    public static String getSimpleCause(EntityDamageEvent.DamageCause damageCause) {
        switch (damageCause) {
            case CONTACT:
                return "Contact";
            case ENTITY_ATTACK:
                return "Melee";
            case PROJECTILE:
                return "Projectile";
            case SUFFOCATION:
                return "Suffocation";
            case FALL:
                return "Fall";
            case FIRE:
                return "Fire";
            case FIRE_TICK:
                return "Fire-Tick";
            case MELTING:
                return "Melting";
            case LAVA:
                return "Lava";
            case DROWNING:
                return "Drowning";
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                return "Explosion";
            case VOID:
                return "Void";
            case LIGHTNING:
                return "Lightning";
            case SUICIDE:
                return "Suicide";
            case STARVATION:
                return "Starvation";
            case POISON:
                return "Poison";
            case MAGIC:
                return "Magic";
            case WITHER:
                return "Wither";
            case FALLING_BLOCK:
                return "Falling-Block";
            case THORNS:
                return "Thorns";
            case DRAGON_BREATH:
                return "Dragon-Breath";
            case CUSTOM:
                return "Custom";
            case FLY_INTO_WALL:
                return "Fly-Into-Wall";
            case HOT_FLOOR:
                return "Hot-Floor";
            case CRAMMING:
                return "Cramming";
            case DRYOUT:
                return "Dryout";
            default:
                return "Unknown";
        }
    }

    public static FileConfiguration getPlayerDeathMessages() {
        return PlayerDeathMessages.getInstance().getConfig();
    }

    public static FileConfiguration getEntityDeathMessages() {
        return EntityDeathMessages.getInstance().getConfig();
    }

    public static String getColorOfString(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();
        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);
                if (color != null) {
                    result.insert(0, color.toString());
                    // Once we find a color or reset we can stop searching
                    if (isChatColorAColor(color) || color.equals(ChatColor.RESET)) {
                        break;
                    }
                }
            }
        }
        return result.toString();
    }

 //   public static String getLastColors(String input){
 //       if(input.charAt(0) == ChatColor.COLOR_CHAR){

   //     }
  //  }

    public static boolean isChatColorAColor(ChatColor chatColor) {
        return chatColor != ChatColor.MAGIC && chatColor != ChatColor.BOLD
                && chatColor != ChatColor.STRIKETHROUGH && chatColor != ChatColor.UNDERLINE
                && chatColor != ChatColor.ITALIC;
    }

}
