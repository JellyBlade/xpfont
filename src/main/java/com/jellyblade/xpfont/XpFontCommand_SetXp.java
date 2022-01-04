package com.jellyblade.xpfont;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class XpFontCommand_SetXp implements CommandExecutor {
    XpFontData xp = XpFont.getXpFontData();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (player.hasPermission("xpfont.admin.setxp")) {
            if (args.length < 2 ) {
                return false;
            }
            try {
                long xpPoints = Long.parseLong(args[1]);
                Player otherPlayer = Bukkit.getServer().getPlayer(args[0]);
                if (otherPlayer == null) {
                    player.sendMessage(ChatColor.RED + args[0] + " is not on this server.");
                    return true;
                }
                if (xp.isPlayerInXPData(otherPlayer)) {
                    xp.setXPDataForPlayer(otherPlayer, xpPoints);
                    player.sendMessage(ChatColor.GOLD + otherPlayer.getName() + " has " + ChatColor.GREEN
                     + xp.getXPDataForPlayer(otherPlayer) + ChatColor.GOLD + " xp stored in their font.");
                    return true;
                }
                else {
                    player.sendMessage(ChatColor.RED + "User: " + args[0] + " not found.");
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        XpFont.permissionsDenied(player);
        return true;
    }
}
