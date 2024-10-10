package skyforge.draconincdomain.com.Core.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import skyforge.draconincdomain.com.Core.Annotations.Events;
import skyforge.draconincdomain.com.Core.Arrays.ArrayController;
import skyforge.draconincdomain.com.Core.SkyForge.SkyForgeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Events
public class EditorModeListener implements Listener {
    private Map<Player, List<Location>> playerSpawns = new HashMap<>();
    private Map<Player, List<Location>> chestSpawns = new HashMap<>();
    private Map<Player, Location[]> selections = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material item = player.getInventory().getItemInMainHand().getType();

        // Check if the player is in editor mode
        if (!ArrayController.getPlayerEditors().contains(player.getUniqueId())) {
            return; // Not in edit mode, do nothing
        }

        // Handle Player Spawn Tool (Player Head)
        if (item == Material.PLAYER_HEAD) {
            handlePlayerSpawnSetting(player, event.getClickedBlock().getLocation());
        }

        // Handle Chest Spawn Tool (Chest)
        if (item == Material.CHEST) {
            handleChestSpawnSetting(player, event.getClickedBlock().getLocation());
        }

        // Handle Region Selection Tool (Blaze Rod)
        if (item == Material.BLAZE_ROD) {
            handleRegionSelection(player, event.getClickedBlock().getLocation(), event.getAction());
        }

        // Handle Saving Map Tool (Nether Star)
        if (item == Material.NETHER_STAR) {
            saveMapData(player);
        }
    }

    private void handlePlayerSpawnSetting(Player player, Location location) {
        if (location == null) return;

        // Store player spawn location
        playerSpawns.computeIfAbsent(player, k -> new ArrayList<>()).add(location);
        player.sendMessage("Player spawn set at " + location.toVector().toString());
    }

    private void handleChestSpawnSetting(Player player, Location location) {
        if (location == null) return;

        // Store chest spawn location
        chestSpawns.computeIfAbsent(player, k -> new ArrayList<>()).add(location);
        player.sendMessage("Chest spawn set at " + location.toVector().toString());
    }

    private void handleRegionSelection(Player player, Location location, Action action) {
        Location[] selection = selections.getOrDefault(player, new Location[2]);

        if (action == Action.LEFT_CLICK_BLOCK) {
            selection[0] = location;
            player.sendMessage("First corner set at: " + location.toVector().toString());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            selection[1] = location;
            player.sendMessage("Second corner set at: " + location.toVector().toString());
        }

        selections.put(player, selection);

        if (selection[0] != null && selection[1] != null) {
            player.sendMessage("Region defined between " + selection[0].toVector().toString() + " and " + selection[1].toVector().toString());
        }
    }

    private void saveMapData(Player player) {
        SkyForgeMap map = new SkyForgeMap(
                "MapName",
                Bukkit.getWorlds().get(0).getName(),
                player.getLocation(),
                getPlayerSpawns(player),
                getChestSpawns(player),
                player.getLocation(),
                10
        );

        File mapFile = new File("plugins/YourPlugin/maps/", map.getMapName() + ".json");

        try {
            map.saveToFile(mapFile);
            player.sendMessage(ChatColor.GREEN + "Map saved successfully!");
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Failed to save the map!");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (ArrayController.getPlayerEditors().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot drop items in edit mode.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (ArrayController.getPlayerEditors().contains(player.getUniqueId()) && event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot move items in your hotbar in edit mode.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ArrayController.getPlayerEditors().remove(player.getUniqueId());
        playerSpawns.remove(player);
        chestSpawns.remove(player);
        selections.remove(player);
    }

    public List<Location> getPlayerSpawns(Player player) {
        return playerSpawns.getOrDefault(player, new ArrayList<>());
    }

    public List<Location> getChestSpawns(Player player) {
        return chestSpawns.getOrDefault(player, new ArrayList<>());
    }

    public Location[] getSelection(Player player) {
        return selections.get(player);
    }
}
