package com.draconincdomain.skyforge;

import com.draconincdomain.skyforge.core.commands.CommandCore;
import com.draconincdomain.skyforge.core.commands.EditorMode;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import com.draconincdomain.skyforge.core.Annotations.Commands;
import com.draconincdomain.skyforge.core.Annotations.Events;
import com.draconincdomain.skyforge.core.Arrays.ArrayController;
import com.draconincdomain.skyforge.core.SkyForgeData.SkyForgeMap;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public final class SkyForge extends JavaPlugin {

    private static SkyForge Instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Plugin has been enabled");
        setInstance();
        loadMaps();
        testRegisterCommand(new EditorMode());
/*        registerPluginCommands();
        registerEvents();*/
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void testRegisterCommand(CommandCore core) {
        this.getCommand(core.getClass().getSimpleName()).setExecutor(core);
        System.out.println("Command registered successfully: " + core.getClass().getSimpleName());
    }

    private void registerPluginCommands() {
        Reflections reflections = new Reflections("com.draconincdomain.skyforge.core.commands", new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> customCommandClasses = reflections.getTypesAnnotatedWith(Commands.class);

        for (Class<?> commandClass : customCommandClasses) {
            try {
                commandClass.getDeclaredConstructor().newInstance();
                System.out.println("Command registered successfully: " + commandClass.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerEvents() {
        Reflections reflections = new Reflections("com.draconincdomain.skyforge.core.events", new TypeAnnotationsScanner(), new SubTypesScanner());
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
