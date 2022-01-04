package com.jellyblade.xpfont;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class XpFont extends JavaPlugin
{
    static XpFontData xp;

    // Config values
    public static ChatMessageType msgType;
    public static XpFontData.Storagetype storagetype;
    public static boolean messagesEnabled;
    public static boolean commandsEnabled;
    public static boolean xpFontEnabled;
    public static Material xpFontBlock;
    public static Material xpFontItem;
    public static FileConfiguration config;


    @Override
    public void onEnable() {

        // Config
        this.saveDefaultConfig();
        config = this.getConfig();
        try {
            storagetype = XpFontData.Storagetype.valueOf(config.getString("data-storage-type"));
        } catch (NullPointerException | IllegalArgumentException e) {
            this.getLogger().warning("Invalid data-storage-type in config. Please fix this and restart the server!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (storagetype.equals(XpFontData.Storagetype.SERIALIZED)) {
            // Serialized Data Storage
            File storedXpFonts = new File(this.getDataFolder() + File.separator + "fontData.xpfont");
            if (storedXpFonts.exists()) {
                xp = new XpFontData(XpFontData.loadDataSerialized(storedXpFonts));
            }
            else {
                xp = new XpFontData();
            }
        }
        else {
            // YAML
            xp = new XpFontData();
            File storedXpFonts = new File(this.getDataFolder() + File.separator + "fontblocks.yml");
            if (storedXpFonts.exists()) {
                xp.loadFontBlocks(storedXpFonts);
            }
            File playerXpFonts = new File(this.getDataFolder() + File.separator + "playerfonts.yml");
            if (playerXpFonts.exists()) {
                xp.loadPlayerFonts(playerXpFonts);
            }
        }

        messagesEnabled = config.getBoolean("messages-enabled");
        if (messagesEnabled) {
            try {
                msgType = ChatMessageType.valueOf(config.getString("message-type"));
            } catch (NullPointerException | IllegalArgumentException e) {
                this.getLogger().warning("Invalid message-type in config, defaulting to 'ACTION_BAR'");
                msgType = ChatMessageType.ACTION_BAR;
            }
        }
        commandsEnabled = config.getBoolean("commands-enabled");
        if (commandsEnabled) {
            this.getCommand("xpcheck").setExecutor(new XpFontCommand_Check());
            this.getCommand("xpdeposit").setExecutor(new XpFontCommand_Deposit());
            this.getCommand("xpwithdraw").setExecutor(new XpFontCommand_Withdraw());
        }
        this.getCommand("xpadd").setExecutor(new XpFontCommand_AddXp());
        this.getCommand("xpset").setExecutor(new XpFontCommand_SetXp());
        this.getCommand("xpmigrate").setExecutor(new XpFontCommand_Migrate());
        xpFontEnabled = config.getBoolean("xp-font-enabled");
        if (xpFontEnabled) {
            try {
                xpFontBlock = Material.valueOf(config.getString("xp-font-block"));
                xpFontItem = Material.valueOf(config.getString("xp-font-activation-item"));
            } catch (NullPointerException | IllegalArgumentException e) {
                this.getLogger().warning("Invalid xp-font-block or xp-font-activation-item in config, using EMERALD_BLOCK and BOOK");
                xpFontBlock = Material.EMERALD_BLOCK;
                xpFontItem = Material.BOOK;
            }
            getServer().getPluginManager().registerEvents(new XpFontListener(), this);
        }
    }

    public void onDisable() {
        if (storagetype == null) {
            return;
        }
        if (storagetype.equals(XpFontData.Storagetype.SERIALIZED)) {
            File save = new File(this.getDataFolder() + File.separator + "fontData.xpfont");
            xp.saveDataSerialized(save);
        }
        else {
            File playerFonts = new File(this.getDataFolder() + File.separator + "playerfonts.yml");
            File fontBlocks = new File(this.getDataFolder() + File.separator + "fontblocks.yml");
            xp.saveDataYAML(playerFonts, fontBlocks);
        }
    }

    public static XpFontData getXpFontData() {
        return xp;
    }

    public static long getXpToNextLevel(int currentLevel) {
        if (currentLevel <= 15) {
            return (2 * currentLevel) + 7;
        }
        else if (currentLevel <= 30) {
            return (5 * currentLevel) - 38;
        }
        else {
            return (9 * currentLevel) - 158;
        }
    }

    public static long getXpAtLevel(int currentLevel) {
        if (currentLevel <= 16) {
            return (long) Math.pow(currentLevel, 2) + (6 * currentLevel);
        }
        else if (currentLevel <= 31) {
            return (long) (2.5 * Math.pow(currentLevel, 2) - (40.5 * currentLevel) + 360);
        }
        else {
            return (long) (4.5 * Math.pow(currentLevel, 2) - (162.5 * currentLevel) + 2220);
        }
    }

    public static long convertRawXpToLevel(long xp) {
        if (xp > 1395) {
            return (long) Math.floor((Math.sqrt(72l * xp - 54215l) + 325l) / 18l);
        }
        if (xp > 315) {
            return (long) Math.floor(Math.sqrt(40 * xp - 7839) / 10 + 8.1);
        }
        if (xp > 0) {
            return (long) Math.floor(Math.sqrt(xp + 9) - 3);
        }
        return 0;
    }

    public static void permissionsDenied(Player player) {
        player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
    }

    public static boolean depositXp(Player player, ChatMessageType chatMessageType) {
        int xpChange;
        try {
            xpChange = Math.toIntExact(XpFont.getTotalXp(player.getLevel(), player.getExp()));
        } catch (ArithmeticException e) {
            Bukkit.getLogger().warning("User " + player.getName() + " is depositing suspicious levels of exp!");
            xpChange = Integer.MAX_VALUE;
        }
        xp.changeXPDataForPlayer(player, Long.valueOf(xpChange));
        player.setExp(0);
        player.setLevel(0);
        player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(ChatColor.GOLD + "Stored " + ChatColor.GREEN +  xpChange + ChatColor.GOLD 
        + " xp, total stored: " + ChatColor.GREEN + xp.getXPDataForPlayer(player) 
        + ChatColor.GOLD + " (" + ChatColor.GREEN + XpFont.convertRawXpToLevel(xp.getXPDataForPlayer(player)) + ChatColor.GOLD + " levels)."));
        return true;
    }

    public static boolean depositXp(Player player, int xpLevels, ChatMessageType chatMessageType) {
        int xpChange;
        int levelChange = player.getLevel() - xpLevels;
        try { 
            xpChange = Math.toIntExact(XpFont.getXpDifferenceBetweenLevels(player.getLevel(), levelChange));
            xpChange = Math.addExact(xpChange, Math.round(XpFont.getXpToNextLevel(player.getLevel()) * player.getExp()));
        } catch (ArithmeticException e) {
            Bukkit.getLogger().warning("User " + player.getName() + " is depositing suspicious levels of exp!");
            xpChange = Integer.MAX_VALUE;
        }
        xp.changeXPDataForPlayer(player, Long.valueOf(xpChange));
        player.setExp(0);
        player.setLevel(levelChange);
        player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(ChatColor.GOLD + "Stored " + ChatColor.GREEN +  xpChange + ChatColor.GOLD 
        + " xp, total stored: " + ChatColor.GREEN + xp.getXPDataForPlayer(player) 
        + ChatColor.GOLD + " (" + ChatColor.GREEN + XpFont.convertRawXpToLevel(xp.getXPDataForPlayer(player)) + ChatColor.GOLD + " levels)."));
        return true;
    }

    public static boolean withdrawXp(Player player, ChatMessageType chatMessageType) {
        int xpChange;
        try {
            xpChange = Math.toIntExact(xp.getXPDataForPlayer(player));
        } catch (ArithmeticException e) {
            Bukkit.getLogger().warning("User " + player.getName() + " is withdrawing suspicious levels of exp!");
            xpChange = Integer.MAX_VALUE;
        }
        player.giveExp(xpChange);
        xp.changeXPDataForPlayer(player, Long.valueOf(xpChange*-1));
        player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(ChatColor.GOLD + "Withdrew " + ChatColor.RED +  xpChange + ChatColor.GOLD 
        + " xp, total remaining: " + ChatColor.RED + xp.getXPDataForPlayer(player) 
        + ChatColor.GOLD + " (" + ChatColor.RED + XpFont.convertRawXpToLevel(xp.getXPDataForPlayer(player)) + ChatColor.GOLD + " levels)."));
        return true;
    }

    public static boolean withdrawXp(Player player, int xpLevels, ChatMessageType chatMessageType) {
        int xpChange;
        int levelChange = player.getLevel() + xpLevels;
        try {
            xpChange = Math.toIntExact(XpFont.getXpDifferenceBetweenLevels(player.getLevel(), levelChange) * -1);
        } catch (ArithmeticException e) {
            Bukkit.getLogger().warning("User " + player.getName() + " is withdrawing suspicious levels of exp!");
            xpChange = Integer.MAX_VALUE;
        }
        if (xp.getXPDataForPlayer(player) > xpChange) {
            player.giveExp(xpChange);
            xp.changeXPDataForPlayer(player, Long.valueOf(xpChange*-1));
        }
        else {
            try {
                xpChange = Math.toIntExact(xp.getXPDataForPlayer(player));
            } catch (ArithmeticException e) {
                xpChange = Integer.MAX_VALUE;
            }
            player.giveExp(xpChange);
            xp.changeXPDataForPlayer(player, Long.valueOf(xpChange*-1));
        }
        player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(ChatColor.GOLD + "Withdrew " + ChatColor.RED +  xpChange + ChatColor.GOLD 
        + " xp, total remaining: " + ChatColor.RED + xp.getXPDataForPlayer(player) 
        + ChatColor.GOLD + " (" + ChatColor.RED + XpFont.convertRawXpToLevel(xp.getXPDataForPlayer(player)) + ChatColor.GOLD + " levels)."));
        return true;
    }

    public static boolean checkXp(Player player, ChatMessageType chatMessageType) {
        player.spigot().sendMessage(chatMessageType, TextComponent.fromLegacyText(ChatColor.GOLD + "Your XP font contains " + ChatColor.GREEN + xp.getXPDataForPlayer(player) + ChatColor.GOLD 
        + " xp (" + ChatColor.GREEN + XpFont.convertRawXpToLevel(xp.getXPDataForPlayer(player)) + ChatColor.GOLD + " levels)."));
        return true;
    }

    public static boolean checkXp(Player player, Player otherPlayer) {
        player.spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(ChatColor.GOLD + otherPlayer.getName() + " has " + ChatColor.GREEN 
        + xp.getXPDataForPlayer(otherPlayer) + ChatColor.GOLD + " xp stored in their font."));
        return true;
    }

    public static long getXpDifferenceBetweenLevels(int levelFrom, int levelTo) {
        return getXpAtLevel(levelFrom) - getXpAtLevel(levelTo);
    }

    public static long getTotalXp(int currentLevel, float levelProgress) {
        return getXpAtLevel(currentLevel) + Math.round(getXpToNextLevel(currentLevel) * levelProgress);
    }
}