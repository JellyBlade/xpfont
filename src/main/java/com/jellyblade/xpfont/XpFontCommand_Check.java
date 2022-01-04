package com.jellyblade.xpfont;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;

public class XpFontCommand_Check implements CommandExecutor {

    XpFontData xp = XpFont.getXpFontData();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!xp.isPlayerInXPData(player)) {
            xp.initXPDataForPlayer(player);
        }
        if (player.hasPermission("xpfont.remote.check")) {
            if (player.hasPermission("xpfont.remote.check.others") && args.length > 0 && !args[0].equals(player.getPlayerListName())) {
                Player otherPlayer = Bukkit.getServer().getPlayer(args[0]);
                if (otherPlayer == null) {
                    player.spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(ChatColor.RED + args[0] + " is not on this server."));
                    return true;
                }
                if (!xp.isPlayerInXPData(otherPlayer)) {
                    player.spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(ChatColor.RED + "User '" + args[0] + "' not found."));
                    return true;
                }
                XpFont.checkXp(player, otherPlayer);
                return true;
            }
            XpFont.checkXp(player, ChatMessageType.CHAT);
            return true;
        }
        XpFont.permissionsDenied(player);
        return true;
    }
}