package skyforge.draconincdomain.com.Core.SkyForge;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkyForgeTeamManager {
    private Map<SkyForgeMap, List<Player>> teams = new HashMap<>();
    private List<SkyForgeMap> availableIslands;

    public SkyForgeTeamManager(List<SkyForgeMap> availableIslands) {
        this.availableIslands = availableIslands;
    }

    public void assignPlayersToTeams(List<Player> players, boolean isSolo) {
        if (isSolo) {
            // Solo: 1 player per island spawn
            for (int i = 0; i < players.size(); i++) {
                SkyForgeMap island = availableIslands.get(i % availableIslands.size());
                teams.putIfAbsent(island, new ArrayList<>());
                teams.get(island).add(players.get(i));
            }
        } else {
            // Team: Assign teams to islands
            int playersPerTeam = players.size() / availableIslands.size();
            int index = 0;

            for (SkyForgeMap island : availableIslands) {
                List<Player> team = new ArrayList<>();
                for (int i = 0; i < playersPerTeam; i++) {
                    team.add(players.get(index));
                    index++;
                }
                teams.put(island, team);
            }
        }
    }

    public Map<SkyForgeMap, List<Player>> getTeams() {
        return teams;
    }
}
