package com.pvpcrosshair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

public class PvPCrosshairClient implements ClientModInitializer {
    private boolean wasPressed = false;
    
    @Override
    public void onInitializeClient() {
        CrosshairConfig.load();
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() == null) return;
            
            long window = client.getWindow().getHandle();
            boolean isPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_G) == GLFW.GLFW_PRESS;
            
            if (isPressed && !wasPressed && client.currentScreen == null) {
                client.setScreen(new CrosshairSettingsScreen());
            }
            wasPressed = isPressed;
        });
        
        System.out.println("PvP Crosshair Mod loaded! Press G for pixel editor.");
    }
}
