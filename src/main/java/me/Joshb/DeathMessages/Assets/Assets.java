package me.Joshb.DeathMessages.Assets;

import me.Joshb.DeathMessages.Config.PlayerDeathMessages;
import me.Joshb.DeathMessages.Manager.PlayerManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Assets {

    /**
     * public static String deathMessage(PlayerManager pm){
     * LivingEntity mob = (LivingEntity) pm.getLastEntityDamager();
     * boolean hasWeapon = mob.getEquipment() != null;
     * if(hasWeapon){
     * List<String> msgs = colorize(getPlayerDeathMessages().getStringList("Mobs." +
     * mob.getName().toLowerCase() + ".Solo.Weapon"));
     * Random random = new Random();
     * int msgRandom = random.nextInt(msgs.size());
     * ItemStack weapon = mob.getEquipment().getItemInMainHand();
     * ComponentBuilder weaponText = new ComponentBuilder(weapon.getItemMeta().getDisplayName());
     * weaponText.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("weapon shit")));
     * <p>
     * String msg = msgs.get(msgRandom)
     * .replaceAll("%player%", pm.getName())
     * .replaceAll("%player_display%", pm.getDisplayName())
     * .replaceAll("%killer%", mob.getName())
     * .replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name())
     * .replaceAll("%world%", pm.getLastLocation().getWorld().getName());
     * return msg;
     * }
     * }
     **/

    public static TextComponent deathMessage(PlayerManager pm, boolean gang) {
        LivingEntity mob = (LivingEntity) pm.getLastEntityDamager();
        boolean hasWeapon = mob.getEquipment() != null;
        if (hasWeapon) {
            if (gang) {
                if (getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang.Weapon").isEmpty())
                    return null;
            } else {
                if (getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo.Weapon").isEmpty())
                    return null;
            }
            //Not using a projectile item. (Swords)
            if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                if (gang) {
                    return null;
                } else {
                    Random random = new Random();
                    List<String> msgs = getPlayerDeathMessages().getStringList("Mobs." +
                            mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo.Weapon");
                    String msg = msgs.get(random.nextInt(msgs.size()));
                    TextComponent tc = new TextComponent();
                    String[] sec = msg.split("::");
                    String firstSection;
                    if (msg.contains("::")) {
                        firstSection = sec[0];
                    } else {
                        firstSection = msg;
                    }
                    for (String splitMessage : firstSection.split(" ")) {
                        if (splitMessage.equalsIgnoreCase("%weapon%")) {
                            if (mob.getEquipment().getItemInMainHand().getType() == Material.AIR) {
                                tc.addExtra("Weapon not found");
                            } else {
                                ItemStack i = mob.getEquipment().getItemInMainHand();
                                TextComponent weaponComp = new TextComponent(mob.getEquipment().getItemInMainHand().getItemMeta().getDisplayName());
                                StringBuilder sb = new StringBuilder();
                                sb.append(mob.getEquipment().getItemInMainHand().getItemMeta().getDisplayName());
                                sb.append("\n");
                                Map<Enchantment, Integer> enchants = i.getEnchantments();
                                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                                    sb.append(ChatColor.GRAY + convertEnchant(entry.getKey()) + " " + RomanNumber.toRoman(entry.getValue()) + " ");
                                    sb.append("\n");
                                }
                                if (i.hasItemMeta() && i.getItemMeta().hasLore()) {
                                    sb.append("\n" + ChatColor.RESET);
                                    for (String lore : i.getItemMeta().getLore()) {
                                        sb.append(ChatColor.RESET + lore);
                                        sb.append("\n");
                                    }
                                }
                                weaponComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(placeholders(sb.toString(), pm, mob))));
                                tc.addExtra(weaponComp);
                            }
                        } else {
                            tc.addExtra(Assets.colorize(placeholders(splitMessage, pm, mob)) + " ");
                        }
                    }
                    if (sec.length == 2) {
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(placeholders(sec[1], pm, mob))));
                    }
                    if (sec.length == 3) {
                        if (sec[2].startsWith("COMMAND:")) {
                            String cmd = sec[2].split(":")[1];
                            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + placeholders(cmd, pm, mob)));
                        } else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
                            String cmd = sec[2].split(":")[1];
                            tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + placeholders(cmd, pm, mob)));
                        }
                    }
                    return tc;
                }
            } else {
                System.out.println("5");
                return null;
            }
        } else {
            System.out.println("6");
            return null;
        }
    }

    public static List<String> colorize(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return newList;
    }

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String placeholders(String msg, PlayerManager pm, LivingEntity mob) {
        msg = msg
                .replaceAll("%player%", pm.getName())
                .replaceAll("%player_display%", pm.getDisplayName())
                .replaceAll("%killer%", mob.getName())
                .replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name())
                .replaceAll("%world%", pm.getLastLocation().getWorld().getName());
        return msg;
    }

    public static String convertEnchant(Enchantment enchantment) {
        String enchant = enchantment.getKey().getKey();
        enchant = enchant.replaceAll("_", " ");
        String[] spl = enchant.split(" ");
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

    public static FileConfiguration getPlayerDeathMessages() {
        return PlayerDeathMessages.getInstance().getConfig();
    }
}
