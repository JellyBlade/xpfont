package com.jellyblade.xpfont;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;

public class XpFontCommand_Deposit implements CommandExecutor {

    XpFontData xp = XpFont.getXpFontData();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (player.hasPermission("xpfont.remote.deposit")) {
            if (!xp.isPlayerInXPData(player)) {
                xp.initXPDataForPlayer(player);
            }
            if (args.length == 0) {
                return XpFont.depositXp(player, ChatMessageType.CHAT);
            }
            try {
                int xpLevels = Integer.parseInt(args[0]);
                if (xpLevels > (player.getLevel())) {
                    return XpFont.depositXp(player, ChatMessageType.CHAT);
                }
                else {
                    return XpFont.depositXp(player, xpLevels, ChatMessageType.CHAT);
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        XpFont.permissionsDenied(player);
        return true;
    }
}