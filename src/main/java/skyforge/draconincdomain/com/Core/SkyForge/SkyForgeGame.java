package skyforge.draconincdomain.com.Core.SkyForge;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import skyforge.draconincdomain.com.SkyForge;

import java.util.List;
import java.util.Map;

public class SkyForgeGame {
    private SkyForgeMap map;
    private Map<SkyForgeMap, List<Player>> teams;

    public SkyForgeGame(SkyForgeMap map, Map<SkyForgeMap, List<Player>> teams) {
        this.map = map;
        this.teams = teams;
    }

    public void startGame() {
        // Create domes for all islands
        for (SkyForgeMap island : teams.keySet()) {
            island.createGlassDome();
        }

        // Assign players to their spawn points
        for (Map.Entry<SkyForgeMap, List<Player>> entry : teams.entrySet()) {
            SkyForgeMap island = entry.getKey();
            List<Player> players = entry.getValue();
            List<Location> spawnLocations = island.getPlayerSpawnLocations();

            for (int i = 0; i < players.size(); i++) {
                players.get(i).teleport(spawnLocations.get(i));
            }
        }

        // Start the game countdown, remove domes after 10 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                for (SkyForgeMap island : teams.keySet()) {
                    island.removeGlassDome();
                }
            }
        }.runTaskLater(SkyForge.getInstance(), 20 * 60 * 10); // Run after 10 minutes
    }
}
