package com.draconincdomain.skyforge.core.Arrays;

import com.draconincdomain.skyforge.core.SkyForgeData.SkyForgeMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArrayController {
    private static Map<String, SkyForgeMap> _skyMaps;
    private static List<UUID> _playerEditors;

    public static Map<String, SkyForgeMap> getSkyForgeMaps() {
        return _skyMaps;
    }
    public static List<UUID> getPlayerEditors() {
        return _playerEditors;
    }
}
