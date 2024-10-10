package skyforge.draconincdomain.com.Core.SkyForge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SkyForgeMap {
    private String mapName;
    private String worldName;
    private Location lobbySpawnLocation;
    private List<Location> playerSpawnLocations;
    private List<Location> chestSpawnLocations;
    private Location domeCentre;
    private int domeRadius;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public SkyForgeMap(String mapName, String worldName, Location lobbySpawnLocation, List<Location> playerSpawnLocations, List<Location> chestSpawnLocations, Location domeCentre, int domeRadius) {
        this.mapName = mapName;
        this.worldName = worldName;
        this.lobbySpawnLocation = lobbySpawnLocation;
        this.playerSpawnLocations = playerSpawnLocations;
        this.chestSpawnLocations = chestSpawnLocations;
        this.domeCentre = domeCentre;
        this.domeRadius = domeRadius;
    }

    public static SkyForgeMap loadFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, SkyForgeMap.class);
        }
    }

    public void saveToFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(this, writer);
        }
    }


    public void createGlassDome() {
        for (int x = -domeRadius; x <= domeRadius; x++) {
            for (int y = -domeRadius; y <= domeRadius; y++) {
                for (int z = -domeRadius; z <= domeRadius; z++) {
                    Location location = domeCentre.clone().add(x, y, z);
                    if (location.distance(domeCentre) <= domeRadius) {
                        location.getBlock().setType(Material.GLASS);
                    }
                }
            }
        }
    }

    // Method to remove the glass dome after a timer
    public void removeGlassDome() {
        for (int x = -domeRadius; x <= domeRadius; x++) {
            for (int y = -domeRadius; y <= domeRadius; y++) {
                for (int z = -domeRadius; z <= domeRadius; z++) {
                    Location location = domeCentre.clone().add(x, y, z);
                    if (location.getBlock().getType() == Material.GLASS) {
                        location.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    public String getMapName() {
        return mapName;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public Location getLobbySpawnLocation() {
        return lobbySpawnLocation;
    }

    public List<Location> getPlayerSpawnLocations() {
        return playerSpawnLocations;
    }

    public List<Location> getChestSpawnLocations() {
        return chestSpawnLocations;
    }

    public Location getDomeCentre() {
        return domeCentre;
    }

    public int getDomeRadius() {
        return domeRadius;
    }
}
