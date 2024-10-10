package skyforge.draconincdomain.com;

import org.bukkit.plugin.java.JavaPlugin;

public final class SkyForge extends JavaPlugin {

    private static SkyForge Instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setInstance();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setInstance() {
        Instance = this;
    }

    public static SkyForge getInstance() {
        return Instance;
    }
}
