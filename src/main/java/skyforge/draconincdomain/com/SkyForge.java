package skyforge.draconincdomain.com;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import skyforge.draconincdomain.com.Core.Annotations.Commands;
import skyforge.draconincdomain.com.Core.Annotations.Events;
import skyforge.draconincdomain.com.Core.Arrays.ArrayController;
import skyforge.draconincdomain.com.Core.SkyForge.SkyForgeMap;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public final class SkyForge extends JavaPlugin {

    private static SkyForge Instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setInstance();
        loadMaps();
        registerPluginCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerPluginCommands() {
        Reflections reflections = new Reflections("skyforge.draconincdomain.com.Core.Commands", new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> customCommandClasses = reflections.getTypesAnnotatedWith(Commands.class);

        for (Class<?> commandClass : customCommandClasses) {
            try {
                commandClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerEvents() {
        Reflections reflections = new Reflections("skyforge.draconincdomain.com.Core.Events", new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> customEventClasses = reflections.getTypesAnnotatedWith(Events.class);

        for (Class<?> eventClass : customEventClasses) {
            try {
                Listener listener = (Listener) eventClass.getDeclaredConstructor().newInstance();
                Bukkit.getServer().getPluginManager().registerEvents( listener, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void loadMaps() {
        File mapsFolder = new File(getDataFolder(), "maps");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }

        File[] files = mapsFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try {
                SkyForgeMap map = SkyForgeMap.loadFromFile(file);
                ArrayController.getSkyForgeMaps().put(map.getMapName(), map);
                getLogger().info("Loaded map: " + map.getMapName());
            } catch (IOException e) {
                getLogger().warning("Failed to load map from file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private void setInstance() {
        Instance = this;
    }

    public static SkyForge getInstance() {
        return Instance;
    }
}
