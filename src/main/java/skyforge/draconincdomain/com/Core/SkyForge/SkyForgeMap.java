package skyforge.draconincdomain.com.Core.SkyForge;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public class SkyForgeMap {
    private String mapName;
    private World world;
    private Location lobbySpawnLocation;
    private List<Location> playerSpawnLocations;
    private List<Location> chestSpawnLocations;
    private Location domeCentre;
    private int domeRadius;

    public SkyForgeMap(String mapName, World world, Location lobbySpawnLocation, List<Location> playerSpawnLocations, List<Location> chestSpawnLocations, Location domeCentre, int domeRadius) {
        this.mapName = mapName;
        this.world = world;
        this.lobbySpawnLocation = lobbySpawnLocation;
        this.playerSpawnLocations = playerSpawnLocations;
        this.chestSpawnLocations = chestSpawnLocations;
        this.domeCentre = domeCentre;
        this.domeRadius = domeRadius;
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
        return world;
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
