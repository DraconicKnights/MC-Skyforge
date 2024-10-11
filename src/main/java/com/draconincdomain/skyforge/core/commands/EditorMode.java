package com.draconincdomain.skyforge.core.commands;

import com.draconincdomain.skyforge.core.Arrays.ArrayController;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.draconincdomain.skyforge.core.Annotations.Commands;

import java.util.List;

@Commands
public class EditorMode extends CommandCore {

    public EditorMode() {
        super("editormode", "skyforge.admin", 5);
    }

    @Override
    protected void execute(Player player, String[] args) {
        // Clear player's inventory
        player.getInventory().clear();

        // Provide the editor tools
        player.getInventory().setItem(0, new ItemStack(Material.BLAZE_ROD));  // Region selection tool
        player.getInventory().setItem(1, new ItemStack(Material.PLAYER_HEAD)); // Player spawn setter
        player.getInventory().setItem(2, new ItemStack(Material.CHEST));       // Chest spawn setter
        player.getInventory().setItem(3, new ItemStack(Material.NETHER_STAR)); // Save map tool

        player.setGameMode(GameMode.CREATIVE);
        ArrayController.getPlayerEditors().add(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "You are now in map editor mode. Use the tools provided to configure the map.");
    }

    @Override
    protected List<String> commandCompletion(Player player, Command command, String[] strings) {
        return null;
    }
}
