package net.joshb.deathmessages.assets;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.ExplosionManager;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assets {

    static boolean addPrefix = Settings.getInstance().getConfig().getBoolean("Add-Prefix-To-All-Messages");

    public static List<String> damageTypes = Arrays.asList(
            "Bed",
            "Respawn-Anchor",
            "Projectile-Arrow",
            "Projectile-Dragon-Fireball",
            "Projectile-Egg",
            "Projectile-EnderPearl",
            "Projectile-Fireball",
            "Projectile-FishHook",
            "Projectile-LlamaSpit",
            "Projectile-Snowball",
            "Projectile-Trident",
            "Projectile-WitherSkull",
            "Projectile-ShulkerBullet",
            "Contact",
            "Melee",
            "Suffocation",
            "Fall",
            "Climbable",
            "Fire",
            "Fire-Tick",
            "Melting",
            "Lava",
            "Drowning",
            "Explosion",
            "Tnt",
            "Firework",
            "End-Crystal",
            "Void",
            "Lightning",
            "Suicide",
            "Starvation",
            "Poison",
            "Magic",
            "Wither",
            "Falling-Block",
            "Dragon-Breath",
            "Custom",
            "Fly-Into-Wall",
            "Hot-Floor",
            "Cramming",
            "Dryout",
            "Unknown");

    public static boolean isNumeric(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    public static HashMap<String, String> addingMessage = new HashMap<>();

    public static String formatMessage(String path) {
        return colorize(Messages.getInstance().getConfig().getString(path)
                .replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix")));
    }

    public static String formatString(String string) {
        return colorize(string
                .replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix")));
    }

    public static List<String> formatMessage(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(colorize(s
                    .replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix"))));
        }
        return newList;
    }

    public static boolean isClimable(Block b) {
        if (DeathMessages.majorVersion() >= 14) {
            return b.getType().name().contains("LADDER")
                    || b.getType().name().contains("VINE")
                    || b.getType().equals(Material.SCAFFOLDING)
                    || b.getType().name().contains("TRAPDOOR");
        }
        return b.getType().name().contains("LADDER")
                || b.getType().name().contains("VINE")
                || b.getType().name().contains("TRAPDOOR");
    }

    public static boolean displayNameIsWeapon(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return false;
        }
        String displayName = itemStack.getItemMeta().getDisplayName();

        for (String s : Settings.getInstance().getConfig().getStringList("Custom-Item-Display-Names-Is-Weapon")) {
            Pattern pattern = Pattern.compile(Assets.colorize(s));
            Matcher matcher = pattern.matcher(displayName);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeapon(ItemStack itemStack) {
        return itemStack.getType().toString().contains("SHOVEL")
                || itemStack.getType().toString().contains("PICKAXE")
                || itemStack.getType().toString().contains("AXE")
                || itemStack.getType().toString().contains("HOE")
                || itemStack.getType().toString().contains("SWORD")
                || itemStack.getType().toString().contains("BOW")
                || displayNameIsWeapon(itemStack);
    }

    public static TextComponent deathMessage(PlayerManager pm, boolean gang) {
        LivingEntity mob = (LivingEntity) pm.getLastEntityDamager();
        boolean hasWeapon;
        if (DeathMessages.majorVersion() < 9) {
            if (mob.getEquipment() == null || mob.getEquipment().getItemInHand() == null) {
                hasWeapon = false;
            } else if (!isWeapon(mob.getEquipment().getItemInHand())) {
                hasWeapon = false;
            } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.THORNS)) {
                hasWeapon = false;
            } else {
                hasWeapon = true;
            }
        } else {
            if (mob.getEquipment() == null || mob.getEquipment().getItemInMainHand() == null) {
                hasWeapon = false;
            } else if (!isWeapon(mob.getEquipment().getItemInMainHand())) {
                hasWeapon = false;
            } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.THORNS)) {
                hasWeapon = false;
            } else {
                hasWeapon = true;
            }
        }

        if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            if (pm.getLastExplosiveEntity() instanceof EnderCrystal) {
                return get(gang, pm, mob, "End-Crystal");
            } else if (pm.getLastExplosiveEntity() instanceof TNTPrimed) {
                return get(gang, pm, mob, "TNT");
            } else if (pm.getLastExplosiveEntity() instanceof Firework) {
                return get(gang, pm, mob, "Firework");
            } else {
                return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));
            }
        }
        if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            //Bed kill

            ExplosionManager explosionManager = ExplosionManager.getManagerIfEffected(pm.getPlayer());
            if (explosionManager.getMaterial().name().contains("BED")) {
                PlayerManager pyro = PlayerManager.getPlayer(explosionManager.getPyro());
                return get(gang, pm, pyro.getPlayer(), "Bed");
            }
            //Respawn Anchor kill
            if (DeathMessages.majorVersion() >= 16 && explosionManager.getMaterial().equals(Material.RESPAWN_ANCHOR)) {
                PlayerManager pyro = PlayerManager.getPlayer(explosionManager.getPyro());
                return get(gang, pm, pyro.getPlayer(), "Respawn-Anchor");
            }
        }
        if (hasWeapon) {
            if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                return getWeapon(gang, pm, mob);
            } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)
                    && pm.getLastProjectileEntity() instanceof Arrow) {
                return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
            } else {
                return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
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
        List<String> msgs = sortList(getPlayerDeathMessages().getStringList("Natural-Cause." + damageCause), pm);
        if (msgs.isEmpty()) return null;
        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent("");
        if (addPrefix) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"))));
            tc.addExtra(tx);
            tc.addExtra(" ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            if (sec.length == 0) {
                firstSection = msg;
            } else {
                firstSection = sec[0];
            }
        } else {
            firstSection = msg;
        }
        String lastColor = "";
        String lastFont = "";
        for (String splitMessage : firstSection.split(" ")) {

            if (splitMessage.contains("%block%") && pm.getLastEntityDamager() instanceof FallingBlock) {
                try {
                    FallingBlock fb = (FallingBlock) pm.getLastEntityDamager();
                    String material;
                    if (DeathMessages.majorVersion() < 13) {
                        material = XMaterial.matchXMaterial(fb.getMaterial()).parseMaterial().toString().toLowerCase();
                    } else {
                        material = XMaterial.matchXMaterial(fb.getBlockData().getMaterial()).parseMaterial().toString().toLowerCase();
                    }
                    String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);
                    String mssa = Assets.colorize(splitMessage.replaceAll("%block%", configValue));
                    tc.addExtra(mssa);
                    lastColor = getColorOfString(lastColor + mssa);
                } catch (NullPointerException e) {
                    DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not parse %block%. Please check your config for a wrong value." +
                            " Your materials could be spelt wrong or it does not exists in the config. If this problem persist, contact support" +
                            " on the discord https://discord.gg/K9zVDwt");
                    pm.setLastEntityDamager(null);
                    return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.SUFFOCATION));
                }

            } else if (splitMessage.contains("%climbable%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
                try {
                    String material;
                    if (DeathMessages.majorVersion() < 13) {
                        material = XMaterial.matchXMaterial(pm.getLastClimbing()).parseMaterial().toString().toLowerCase();
                    } else {
                        material = XMaterial.matchXMaterial(pm.getLastClimbing()).parseMaterial().toString().toLowerCase();
                    }
                    String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);
                    String mssa = Assets.colorize(splitMessage.replaceAll("%climbable%", configValue));
                    tc.addExtra(mssa);
                    lastColor = getColorOfString(lastColor + mssa);
                } catch (NullPointerException e) {
                    DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not parse %climbable%. Please check your config for a wrong value." +
                            " Your materials could be spelt wrong or it does not exists in the config. If this problem persist, contact support" +
                            " on the discord https://discord.gg/K9zVDwt");
                    pm.setLastClimbing(null);
                    return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.FALL));
                }
            } else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE) && splitMessage.contains("%weapon%")) {
                ItemStack i;
                if (DeathMessages.majorVersion() <= 9) {
                    i = pm.getPlayer().getEquipment().getItemInHand();
                } else {
                    i = pm.getPlayer().getEquipment().getItemInMainHand();
                }
                if(!i.getType().equals(Material.BOW)){
                    return getNaturalDeath(pm, "Projectile-Unknown");
                }
                if (DeathMessages.majorVersion() < 14) {
                    if(!i.getType().equals(Material.CROSSBOW)){
                        return getNaturalDeath(pm, "Projectile-Unknown");
                    }
                }
                String displayName;
                if (!(i.getItemMeta() == null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().equals("")) {
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Allow-Message-Color-Override")) {

                    }
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Enabled")) {
                        if (!Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Ignore-Enchantments")) {
                            if (i.getEnchantments().size() == 0) {
                                return getNaturalDeath(pm, "Projectile-Unknown");
                            }
                        } else {
                            return getNaturalDeath(pm, "Projectile-Unknown");
                        }
                    }
                    displayName = Assets.convertString(i.getType().name());
                } else {
                    displayName = i.getItemMeta().getDisplayName();
                }
                String[] spl = splitMessage.split("%weapon%");
                if (spl.length != 0 && spl[0] != null && !spl[0].equals("")) {
                    displayName = Assets.colorize(spl[0]) + displayName;
                }
                if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].equals("")) {
                    displayName = displayName + Assets.colorize(spl[1]);
                }
                TextComponent weaponComp = new TextComponent(TextComponent.fromLegacyText(displayName));
                BaseComponent[] hoverEventComponents = new BaseComponent[]{
                        new TextComponent(NBTItem.convertItemtoNBT(i).getCompound().toString())
                };
                weaponComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
                tc.addExtra(weaponComp);
            } else {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(colorize(playerDeathPlaceholders(lastColor + lastFont + splitMessage, pm, null)) + " "));
                tc.addExtra(tx);

                for (BaseComponent bs : tx.getExtra()) {
                    if (!(bs.getColor() == null)) {
                        lastColor = bs.getColor().toString();
                    }
                    lastFont = formatting(bs);
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
        boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
        if (gang) {
            if(basicMode){
                msgs = sortList(getPlayerDeathMessages().getStringList("Basic-Mode.Gang.Weapon"), pm);
            } else {
                msgs = sortList(getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang.Weapon"), pm);
            }
        } else {
            if(basicMode){
                msgs = sortList(getPlayerDeathMessages().getStringList("Basic-Mode.Solo.Weapon"), pm);
            } else {
                msgs = sortList(getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo.Weapon"), pm);
            }
        }
        if (msgs.isEmpty()) return null;
        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent("");
        if (addPrefix) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"))));
            tc.addExtra(tx);
            tc.addExtra(" ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            if (sec.length == 0) {
                firstSection = msg;
            } else {
                firstSection = sec[0];
            }
        } else {
            firstSection = msg;
        }
        String lastColor = "";
        String lastFont = "";
        for (String splitMessage : firstSection.split(" ")) {
            if (splitMessage.contains("%weapon%")) {
                ItemStack i;
                if (DeathMessages.majorVersion() <= 9) {
                    i = mob.getEquipment().getItemInHand();
                } else {
                    i = mob.getEquipment().getItemInMainHand();
                }
                String displayName;
                if (!(i.getItemMeta() == null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().equals("")) {
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Allow-Message-Color-Override")) {

                    }
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Enabled")) {
                        if (!Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Ignore-Enchantments")) {
                            if (i.getEnchantments().size() == 0) {
                                return get(gang, pm, mob, Settings.getInstance().getConfig()
                                        .getString("Disable-Weapon-Kill-With-No-Custom-Name.Source.Weapon.Default-To"));
                            }
                        } else {
                            return get(gang, pm, mob, Settings.getInstance().getConfig()
                                    .getString("Disable-Weapon-Kill-With-No-Custom-Name.Source.Weapon.Default-To"));
                        }
                    }
                    displayName = Assets.convertString(i.getType().name());
                } else {
                    displayName = i.getItemMeta().getDisplayName();
                }
                String[] spl = splitMessage.split("%weapon%");
                if (spl.length != 0 && spl[0] != null && !spl[0].equals("")) {
                    displayName = Assets.colorize(spl[0]) + displayName;
                }
                if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].equals("")) {
                    displayName = displayName + Assets.colorize(spl[1]);
                }
                TextComponent weaponComp = new TextComponent(TextComponent.fromLegacyText(displayName));
                BaseComponent[] hoverEventComponents = new BaseComponent[]{
                        new TextComponent(NBTItem.convertItemtoNBT(i).getCompound().toString())
                };
                weaponComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
                tc.addExtra(weaponComp);
            } else {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(colorize(playerDeathPlaceholders(lastColor + lastFont + splitMessage, pm, mob)) + " "));
                tc.addExtra(tx);
                for (BaseComponent bs : tx.getExtra()) {
                    if (!(bs.getColor() == null)) {
                        lastColor = bs.getColor().toString();
                    }
                    lastFont = formatting(bs);
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

    public static TextComponent get(boolean gang, PlayerManager pm, LivingEntity mob, String damageCause) {
        Random random = new Random();
        List<String> msgs;
        boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
        if (gang) {
            if(basicMode){
                msgs = sortList(getPlayerDeathMessages().getStringList("Basic-Mode.Gang." + damageCause), pm);
            } else {
                msgs = sortList(getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang." + damageCause), pm);
            }
        } else {
            if(basicMode){
                msgs = sortList(getPlayerDeathMessages().getStringList("Basic-Mode.Solo." + damageCause), pm);
            } else {
                msgs = sortList(getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo." + damageCause), pm);
            }
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
        TextComponent tc = new TextComponent("");
        if (addPrefix) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"))));
            tc.addExtra(tx);
            tc.addExtra(" ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            if (sec.length == 0) {
                firstSection = msg;
            } else {
                firstSection = sec[0];
            }
        } else {
            firstSection = msg;
        }
        String lastColor = "";
        String lastFont = "";
        for (String splitMessage : firstSection.split(" ")) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + lastFont + splitMessage, pm, mob)) + " "));
            tc.addExtra(tx);
            for (BaseComponent bs : tx.getExtra()) {
                if (!(bs.getColor() == null)) {
                    lastColor = bs.getColor().toString();
                }
                lastFont = formatting(bs);
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
        boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
        if (gang) {
            if(basicMode){
                msgs = sortList(getPlayerDeathMessages().getStringList("Basic-Mode.Gang." + projectileDamage), pm);
            } else {
                msgs = sortList(getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Gang." + projectileDamage), pm);
            }
        } else {
            if(basicMode){
                msgs = sortList(getPlayerDeathMessages().getStringList("Basic-Mode.Solo." + projectileDamage), pm);
            } else {
                msgs = sortList(getPlayerDeathMessages().getStringList("Mobs." +
                        mob.getType().getEntityClass().getSimpleName().toLowerCase() + ".Solo." + projectileDamage), pm);
            }
        }
        if (msgs.isEmpty()) {
            if (Settings.getInstance().getConfig().getBoolean("Default-Melee-Last-Damage-Not-Defined")) {
                return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
            }
            return null;
        }
        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent("");
        if (addPrefix) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"))));
            tc.addExtra(tx);
            tc.addExtra(" ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            if (sec.length == 0) {
                firstSection = msg;
            } else {
                firstSection = sec[0];
            }
        } else {
            firstSection = msg;
        }
        String lastColor = "";
        String lastFont = "";
        for (String splitMessage : firstSection.split(" ")) {
            if (splitMessage.contains("%weapon%") && pm.getLastProjectileEntity() instanceof Arrow) {
                ItemStack i;
                if (DeathMessages.majorVersion() < 9) {
                    i = mob.getEquipment().getItemInHand();
                } else {
                    i = mob.getEquipment().getItemInMainHand();
                }
                if (i == null) {
                    continue;
                }
                String displayName;
                if (!(i.getItemMeta() == null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().equals("")) {
                    if (Settings.getInstance().getConfig().getBoolean("Disable-Weapon-Kill-With-No-Custom-Name.Enabled")) {
                        if (!Settings.getInstance().getConfig()
                                .getString("Disable-Weapon-Kill-With-No-Custom-Name.Source.Projectile.Default-To").equals(projectileDamage)) {
                            return getProjectile(gang, pm, mob, Settings.getInstance().getConfig()
                                    .getString("Disable-Weapon-Kill-With-No-Custom-Name.Source.Projectile.Default-To"));
                        }
                    }
                    displayName = Assets.convertString(i.getType().name());
                } else {
                    displayName = i.getItemMeta().getDisplayName();
                }
                String[] spl = splitMessage.split("%weapon%");
                if (spl.length != 0 && spl[0] != null && !spl[0].equals("")) {
                    displayName = Assets.colorize(spl[0]) + ChatColor.RESET + displayName;
                }
                if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].equals("")) {
                    displayName = displayName + ChatColor.RESET + Assets.colorize(spl[1]);
                }
                TextComponent weaponComp = new TextComponent(TextComponent.fromLegacyText(displayName));
                BaseComponent[] hoverEventComponents = new BaseComponent[]{
                        new TextComponent(NBTItem.convertItemtoNBT(i).getCompound().toString())
                };
                weaponComp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
                tc.addExtra(weaponComp);
            } else {
                TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(playerDeathPlaceholders(lastColor + lastFont + splitMessage, pm, mob)) + " "));
                tc.addExtra(tx);
                for (BaseComponent bs : tx.getExtra()) {
                    if (!(bs.getColor() == null)) {
                        lastColor = bs.getColor().toString();
                    }
                    lastFont = formatting(bs);
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
        List<String> msgs = sortList(getEntityDeathMessages().getStringList("Tamable"), pm);
        if (msgs.isEmpty()) return null;

        String msg = msgs.get(random.nextInt(msgs.size()));
        TextComponent tc = new TextComponent("");
        if (addPrefix) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(Messages.getInstance().getConfig().getString("Prefix"))));
            tc.addExtra(tx);
            tc.addExtra(" ");
        }
        String[] sec = msg.split("::");
        String firstSection;
        if (msg.contains("::")) {
            if (sec.length == 0) {
                firstSection = msg;
            } else {
                firstSection = sec[0];
            }
        } else {
            firstSection = msg;
        }
        String lastColor = "";
        String lastFont = "";
        for (String splitMessage : firstSection.split(" ")) {
            TextComponent tx = new TextComponent(TextComponent.fromLegacyText(Assets.colorize(entityDeathPlaceholders(lastColor + lastFont + splitMessage, pm, tameable)) + " "));
            tc.addExtra(tx);
            for (BaseComponent bs : tx.getExtra()) {
                if (!(bs.getColor() == null)) {
                    lastColor = bs.getColor().toString();
                }
                lastFont = formatting(bs);
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

    public static List<String> sortList(List<String> list, PlayerManager pm) {
        List<String> newList = list;
        List<String> returnList = new ArrayList<>();
        for (String s : list) {
            //Check for permission messages
            if (s.contains("PERMISSION[")) {
                Matcher m = Pattern.compile("PERMISSION\\[([^)]+)\\]").matcher(s);
                while (m.find()) {
                    String perm = m.group(1);
                    if (pm.getPlayer().hasPermission(perm)) {
                        returnList.add(s.replace("PERMISSION[" + perm + "]", ""));
                    }
                }
            }
            //Check for region specific messages
            if (s.contains("REGION[")) {
                Matcher m = Pattern.compile("REGION\\[([^)]+)\\]").matcher(s);
                while (m.find()) {
                    String regionID = m.group(1);
                    if (DeathMessages.worldGuardExtension == null) {
                        continue;
                    }
                    if (DeathMessages.worldGuardExtension.isInRegion(pm.getPlayer(), regionID)) {
                        returnList.add(s.replace("REGION[" + regionID + "]", ""));
                    }
                }
            }
        }
        if (!returnList.isEmpty()) {
            newList = returnList;
        } else {
            newList.removeIf(s -> s.contains("PERMISSION[") || s.contains("REGION["));
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
                .replaceAll("%world%", tameable.getLocation().getWorld().getName())
                .replaceAll("%world_environment%", getEnvironment(tameable.getLocation().getWorld().getEnvironment()))
                .replaceAll("%tamable%", Messages.getInstance().getConfig().getString("Mobs."
                        + tameable.getType().getEntityClass().getSimpleName().toLowerCase()))
                .replaceAll("%tamable_displayname%", tameable.getName())
                .replaceAll("%owner%", tameable.getOwner().getName())
                .replaceAll("%x%", String.valueOf(tameable.getLocation().getBlock().getX()))
                .replaceAll("%y%", String.valueOf(tameable.getLocation().getBlock().getY()))
                .replaceAll("%z%", String.valueOf(tameable.getLocation().getBlock().getZ())));
        try {
            msg = msg.replaceAll("%biome%", tameable.getLocation().getBlock().getBiome().name());
        } catch (NullPointerException e) {
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Custom Biome detected. Using 'Unknown' for a biome name.");
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Custom Biomes are not supported yet.'");
            msg = msg.replaceAll("%biome%", "Unknown");
        }
        if (DeathMessages.plugin.placeholderAPIEnabled) {
            msg = PlaceholderAPI.setPlaceholders(killer.getPlayer(), msg);
        }
        return msg;
    }

    public static String playerDeathPlaceholders(String msg, PlayerManager pm, LivingEntity mob) {
        if (mob == null) {
            msg = colorize(msg
                    .replaceAll("%player%", pm.getName())
                    .replaceAll("%player_display%", pm.getPlayer().getDisplayName())
                    .replaceAll("%world%", pm.getLastLocation().getWorld().getName())
                    .replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
                    .replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
                    .replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
                    .replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ())));
            try {
                msg = msg.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
            } catch (NullPointerException e) {
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Custom Biome detected. Using 'Unknown' for a biome name.");
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Custom Biomes are not supported yet.'");
                msg = msg.replaceAll("%biome%", "Unknown");
            }
            if (DeathMessages.plugin.placeholderAPIEnabled) {
                msg = PlaceholderAPI.setPlaceholders(pm.getPlayer(), msg);
            }
            return msg;
        } else {
            String mobName = mob.getName();
            if (Settings.getInstance().getConfig().getBoolean("Rename-Mobs.Enabled")) {
                String[] chars = Settings.getInstance().getConfig().getString("Rename-Mobs.If-Contains").split("(?!^)");
                for (String ch : chars) {
                    if (mobName.contains(ch)) {
                        mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
                        break;
                    }
                }
            }
            if (!(mob instanceof Player) && Settings.getInstance().getConfig().getBoolean("Disable-Named-Mobs")) {
                mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
            }
            msg = msg
                    .replaceAll("%player%", pm.getName())
                    .replaceAll("%player_display%", pm.getPlayer().getDisplayName())
                    .replaceAll("%killer%", mobName)
                    .replaceAll("%killer_type%", Messages.getInstance().getConfig().getString("Mobs."
                            + mob.getType().toString().toLowerCase()))
                    .replaceAll("%world%", pm.getLastLocation().getWorld().getName())
                    .replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
                    .replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
                    .replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
                    .replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ()));
            try {
                msg = msg.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
            } catch (NullPointerException e) {
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Custom Biome detected. Using 'Unknown' for a biome name.");
                DeathMessages.plugin.getLogger().log(Level.SEVERE, "Custom Biomes are not supported yet.'");
                msg = msg.replaceAll("%biome%", "Unknown");
            }

            if (mob instanceof Player) {
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

    public static String formatting(BaseComponent tx) {
        String returning = "";
        if (tx.isBold()) returning = returning + "&l";
        if (tx.isItalic()) returning = returning + "&o";
        if (tx.isObfuscated()) returning = returning + "&k";
        if (tx.isStrikethrough()) returning = returning + "&m";
        if (tx.isUnderlined()) returning = returning + "&n";
        return returning;
    }

    public static String getEnvironment(World.Environment environment) {
        switch (environment) {
            case NORMAL:
                return Messages.getInstance().getConfig().getString("Environment.normal");
            case NETHER:
                return Messages.getInstance().getConfig().getString("Environment.nether");
            case THE_END:
                return Messages.getInstance().getConfig().getString("Environment.the_end");
            default:
                return Messages.getInstance().getConfig().getString("Environment.unknown");
        }
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

    public static boolean isChatColorAColor(ChatColor chatColor) {
        return chatColor != ChatColor.MAGIC && chatColor != ChatColor.BOLD
                && chatColor != ChatColor.STRIKETHROUGH && chatColor != ChatColor.UNDERLINE
                && chatColor != ChatColor.ITALIC;
    }

}
