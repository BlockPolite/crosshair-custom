package com.pvpcrosshair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.*;

public class CrosshairConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "pvpcrosshair.json");
    
    public static CrosshairConfig INSTANCE = new CrosshairConfig();
    public static final int GRID_SIZE = 15;
    
    // Режим: false = обычный, true = пиксельный
    public boolean pixelMode = false;
    
    // Обычный крестик
    public int size = 5;
    public int thickness = 1;
    public int gap = 2;
    
    // Пиксельный крестик
    public boolean[][] normalPixels = new boolean[GRID_SIZE][GRID_SIZE];
    public boolean[][] aimPixels = new boolean[GRID_SIZE][GRID_SIZE];
    public boolean useAimDesign = false;
    public int pixelScale = 1;
    
    // Цвета
    public int normalRed = 255, normalGreen = 255, normalBlue = 255;
    public int aimRed = 255, aimGreen = 50, aimBlue = 50;
    
    public CrosshairConfig() {
        createDefaultCrosshair(normalPixels);
        createDefaultCrosshair(aimPixels);
    }
    
    private void createDefaultCrosshair(boolean[][] p) {
        int c = GRID_SIZE / 2;
        for (int x = c - 4; x <= c + 4; x++) if (x != c) p[c][x] = true;
        for (int y = c - 4; y <= c + 4; y++) if (y != c) p[y][c] = true;
    }
    
    public int getNormalColor() { return 0xFF000000 | (normalRed << 16) | (normalGreen << 8) | normalBlue; }
    public int getAimColor() { return 0xFF000000 | (aimRed << 16) | (aimGreen << 8) | aimBlue; }
    public boolean[][] getCurrentPixels(boolean aim) { return (aim && useAimDesign) ? aimPixels : normalPixels; }
    
    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(Files.readString(CONFIG_PATH), CrosshairConfig.class);
                if (INSTANCE.normalPixels == null) { INSTANCE.normalPixels = new boolean[GRID_SIZE][GRID_SIZE]; INSTANCE.createDefaultCrosshair(INSTANCE.normalPixels); }
                if (INSTANCE.aimPixels == null) { INSTANCE.aimPixels = new boolean[GRID_SIZE][GRID_SIZE]; INSTANCE.createDefaultCrosshair(INSTANCE.aimPixels); }
            }
        } catch (Exception e) { INSTANCE = new CrosshairConfig(); }
    }
    
    public static void save() {
        try { Files.createDirectories(CONFIG_PATH.getParent()); Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE)); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
