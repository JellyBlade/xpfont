package com.jellyblade.xpfont;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class XpFontListener implements Listener {

    XpFontData xp = XpFont.getXpFontData();
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        // XP Font Storage/Creation
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND) && event.getClickedBlock().getType().equals(XpFont.xpFontBlock)) {
            Player player = event.getPlayer();
            // XP Font Creation
            if (player.getInventory().getItemInMainHand().getType().equals(XpFont.xpFontItem)) {
                if (player.hasPermission("xpfont.create") && !xp.checkLocation(event.getClickedBlock().getLocation())) {
                    xp.createFont(event.getClickedBlock().getLocation());
                    player.sendMessage(ChatColor.GOLD + "XP font" + ChatColor.GREEN + " created!");
                }
                else if (player.hasPermission("xpfont.destroy") && xp.checkLocation(event.getClickedBlock().getLocation()) && player.isSneaking()) {
                    xp.destroyFont(event.getClickedBlock().getLocation());
                    player.sendMessage(ChatColor.GOLD + "XP font" + ChatColor.RED + " unbound.");
                }
            }
            else if (xp.checkLocation(event.getClickedBlock().getLocation())) {
                if (!xp.isPlayerInXPData(player)) {
                    xp.initXPDataForPlayer(player);
                }
                if (player.hasPermission("xpfont.deposit") && player.getInventory().getItemInMainHand().getType().equals(null) || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    if (player.getExp() > 0 || player.getLevel() > 0) {
                        if (player.isSneaking()) {
                            XpFont.depositXp(player, XpFont.msgType);
                        }
                        else {
                            XpFont.depositXp(player, 1, XpFont.msgType);
                        }
                    }
                    else if (player.hasPermission("xpfont.check")) {
                        XpFont.checkXp(player, XpFont.msgType);
                    }
                }
                else if (player.hasPermission("xpfont.check")) {
                    XpFont.checkXp(player, XpFont.msgType);
                }
            }
        }
        
        // XP Font Withdrawal
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND) && event.getClickedBlock().getType().equals(XpFont.xpFontBlock)) {
            if (xp.checkLocation(event.getClickedBlock().getLocation())) {
                Player player = event.getPlayer();
                if (!xp.isPlayerInXPData(player)) {
                    xp.initXPDataForPlayer(player);
                }
                if (player.hasPermission("xpfont.withdraw") && player.getInventory().getItemInMainHand().getType().equals(null) || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    if (player.isSneaking()) {
                        XpFont.withdrawXp(player, XpFont.msgType);
                    }
                    else {
                        XpFont.withdrawXp(player, 1, XpFont.msgType);
                    }
                }
                else if (player.hasPermission("xpfont.check")) {
                    XpFont.checkXp(player, XpFont.msgType);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(XpFont.xpFontBlock) && xp.checkLocation(event.getBlock().getLocation())) {
            xp.destroyFont(event.getBlock().getLocation());
        }
    }
}