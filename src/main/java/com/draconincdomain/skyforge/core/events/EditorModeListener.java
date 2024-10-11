package com.draconincdomain.skyforge.core.events;

import com.draconincdomain.skyforge.core.Annotations.Events;
import com.draconincdomain.skyforge.core.Arrays.ArrayController;
import com.draconincdomain.skyforge.core.SkyForgeData.SkyForgeMap;
import com.draconincdomain.skyforge.SkyForge;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Events
public class EditorModeListener implements Listener {
    private static final HashMap<Player, List<Location>> playerSpawns = new HashMap<>();
    private static final HashMap<Player, List<Location>> chestSpawns = new HashMap<>();
    private static final HashMap<Player, List<Location>> forgePoints = new HashMap<>(); // Forge Points
    private static final HashMap<Player, Location> forgeCore = new HashMap<>(); // Forge Core
    private static final HashMap<Player, Location[]> selections = new HashMap<>();
    private static final HashMap<Player, String> mapNames = new HashMap<>(); // To store map names for players

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

        // Handle SkyForge Points Tool (Diamond Sword)
        if (item == Material.DIAMOND_SWORD) {
            handleSkyForgePointSetting(player, event.getClickedBlock().getLocation());
        }

        // Handle SkyForge Core Tool (Diamond Block)
        if (item == Material.DIAMOND_BLOCK) {
            handleSkyForgeCoreSetting(player, event.getClickedBlock().getLocation());
        }

        // Handle Map Name Tool (Book)
        if (item == Material.BOOK) {
            promptForMapName(player); // Prompt player for map name
        }

        // Handle Saving Map Tool (Nether Star)
        if (item == Material.NETHER_STAR) {
            saveMapData(player);
        }
    }

    private void promptForMapName(Player player) {
        player.sendMessage("Please enter the name of the map you wish to save:");
    }

    private void handlePlayerSpawnSetting(Player player, Location location) {
        if (location == null) return;

        playerSpawns.computeIfAbsent(player, k -> new ArrayList<>()).add(location);
        player.sendMessage("Player spawn set at " + location.toVector().toString());

        spawnParticleEffect(location, Particle.PORTAL, player, 1, 1, 1, 0, 20);
    }

    private void handleChestSpawnSetting(Player player, Location location) {
        if (location == null) return;

        chestSpawns.computeIfAbsent(player, k -> new ArrayList<>()).add(location);
        player.sendMessage("Chest spawn set at " + location.toVector().toString());

        spawnParticleEffect(location, Particle.CAMPFIRE_COSY_SMOKE, player, 1, 1, 1, 0, 20);
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

            drawRegionPerimeter(selection[0], selection[1], player);
        }
    }

    private void handleSkyForgePointSetting(Player player, Location location) {
        if (location == null) return;

        forgePoints.computeIfAbsent(player, k -> new ArrayList<>()).add(location);
        player.sendMessage("SkyForge point set at " + location.toVector().toString());

        spawnParticleEffect(location, Particle.CRIT, player, 1, 1, 1, 0, 20);
    }

    private void handleSkyForgeCoreSetting(Player player, Location location) {
        if (location == null) return;

        forgeCore.put(player, location);
        player.sendMessage("SkyForge core set at " + location.toVector().toString());

        spawnParticleEffect(location, Particle.SNOWFLAKE, player, 1, 1, 1, 0, 20);
    }

    private void drawRegionPerimeter(Location corner1, Location corner2, Player player) {
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                spawnParticleEffect(new Location(corner1.getWorld(), x, minY, z), Particle.SOUL_FIRE_FLAME, player, 0, 0, 0, 0, 10);
                spawnParticleEffect(new Location(corner1.getWorld(), x, maxY, z), Particle.SOUL_FIRE_FLAME, player, 0, 0, 0, 0, 10);
            }
        }

        for (int y = minY; y <= maxY; y++) {
            spawnParticleEffect(new Location(corner1.getWorld(), minX, y, minZ), Particle.SOUL_FIRE_FLAME, player, 0, 0, 0, 0, 10);
            spawnParticleEffect(new Location(corner1.getWorld(), maxX, y, maxZ), Particle.SOUL_FIRE_FLAME, player, 0, 0, 0, 0, 10);
        }
    }

    private void spawnParticleEffect(Location location, Particle particle, Player player, double offsetX, double offsetY, double offsetZ, double speed, int count) {
        new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
            }
        }.runTaskTimer(SkyForge.getInstance(), 0L, 20L);
    }

    private void saveMapData(Player player) {
        String mapName = mapNames.getOrDefault(player, "Default");

        if (ArrayController.getSkyForgeMaps().containsKey(mapName)) {
            player.sendMessage(ChatColor.RED + "A map with that name already exists! Please choose a different name.");
            return;
        }

        List<Location> playerSpawns = getPlayerSpawns(player);
        List<Location> chestSpawns = getChestSpawns(player);
        List<Location> forgePoints = getForgePoints(player);
        Location coreLocation = getForgeCore(player);

        Location[] region = selections.getOrDefault(player, new Location[2]);
        if (region[0] == null || region[1] == null) {
            player.sendMessage(ChatColor.RED + "Region selection is incomplete!");
            return;
        }

        SkyForgeMap map = new SkyForgeMap(
                mapName,
                player.getWorld().getName(),
                region[0],
                playerSpawns,
                chestSpawns,
                forgePoints,
                coreLocation == null ? new ArrayList<>() : List.of(coreLocation),
                region[0],
                calculateDomeRadius(region[0], region[1])
        );

        ArrayController.getSkyForgeMaps().put(mapName, map);
        player.sendMessage(ChatColor.GREEN + "Map '" + mapName + "' has been saved successfully!");
    }

    private List<Location> getPlayerSpawns(Player player) {
        return playerSpawns.getOrDefault(player, new ArrayList<>());
    }

    private List<Location> getChestSpawns(Player player) {
        return chestSpawns.getOrDefault(player, new ArrayList<>());
    }

    private List<Location> getForgePoints(Player player) {
        return forgePoints.getOrDefault(player, new ArrayList<>());
    }

    private Location getForgeCore(Player player) {
        return forgeCore.get(player);
    }

    private int calculateDomeRadius(Location corner1, Location corner2) {
        int dx = Math.abs(corner1.getBlockX() - corner2.getBlockX());
        int dz = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());
        return (int) Math.sqrt(dx * dx + dz * dz) / 2;
    }
}
