package com.jellyblade.xpfont;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XpFontCommand_Migrate implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (player.hasPermission("xpfont.admin.migrate")) {
            if (args.length == 0) {
                return false;
            }
            /*
            switch (args[0].toLowerCase()) {
                case "yaml":
                    XpFont.storagetype = XpFontData.Storagetype.YAML;
                    break;
                case "serialized":
                    XpFont.storagetype = XpFontData.Storagetype.SERIALIZED;
                    break;
                case "sql":
                    XpFont.storagetype = XpFontData.Storagetype.SQL;
                    break;
                default:
                    return false;
            }
            XpFont.changeConfig("data-storage-type", args[0].toUpperCase());
            */
            player.sendMessage("Migration coming soon, to an update near you!");
            return true;
        }
        else {
            XpFont.permissionsDenied(player);
            return true;
        }
    }
}
