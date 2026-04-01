package com.pvpcrosshair;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CrosshairSettingsScreen extends Screen {
    private static final int GRID_SIZE = CrosshairConfig.GRID_SIZE;
    private static final int PIXEL_SIZE = 14;
    private boolean editingAim = false;
    private int gridX = 30, gridY = 50;
    
    public CrosshairSettingsScreen() { super(Text.literal("Crosshair Settings")); }
    
    @Override
    protected void init() {
        clearChildren();
        int centerX = width / 2;
        
        addDrawableChild(ButtonWidget.builder(
            Text.literal(CrosshairConfig.INSTANCE.pixelMode ? "Mode: PIXEL" : "Mode: CLASSIC"), b -> {
            CrosshairConfig.INSTANCE.pixelMode = !CrosshairConfig.INSTANCE.pixelMode;
            init();
        }).dimensions(centerX - 50, 5, 100, 20).build());
        
        if (CrosshairConfig.INSTANCE.pixelMode) initPixelMode();
        else initClassicMode();
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> { CrosshairConfig.save(); close(); })
            .dimensions(centerX - 50, height - 25, 100, 20).build());
    }
    
    private void initClassicMode() {
        int cx = width / 2, sy = 35, sp = 24;
        addDrawableChild(new CSlider(cx-100, sy, 200, 20, "Size", CrosshairConfig.INSTANCE.size, 2, 15, v -> CrosshairConfig.INSTANCE.size = v));
        addDrawableChild(new CSlider(cx-100, sy+sp, 200, 20, "Thick", CrosshairConfig.INSTANCE.thickness, 1, 5, v -> CrosshairConfig.INSTANCE.thickness = v));
        addDrawableChild(new CSlider(cx-100, sy+sp*2, 200, 20, "Gap", CrosshairConfig.INSTANCE.gap, 0, 10, v -> CrosshairConfig.INSTANCE.gap = v));
        addDrawableChild(new RGBSlider(cx-100, sy+sp*3+10, 200, 18, "NR", CrosshairConfig.INSTANCE.normalRed, v -> CrosshairConfig.INSTANCE.normalRed = v));
        addDrawableChild(new RGBSlider(cx-100, sy+sp*4+10, 200, 18, "NG", CrosshairConfig.INSTANCE.normalGreen, v -> CrosshairConfig.INSTANCE.normalGreen = v));
        addDrawableChild(new RGBSlider(cx-100, sy+sp*5+10, 200, 18, "NB", CrosshairConfig.INSTANCE.normalBlue, v -> CrosshairConfig.INSTANCE.normalBlue = v));
        addDrawableChild(new RGBSlider(cx-100, sy+sp*6+20, 200, 18, "AR", CrosshairConfig.INSTANCE.aimRed, v -> CrosshairConfig.INSTANCE.aimRed = v));
        addDrawableChild(new RGBSlider(cx-100, sy+sp*7+20, 200, 18, "AG", CrosshairConfig.INSTANCE.aimGreen, v -> CrosshairConfig.INSTANCE.aimGreen = v));
        addDrawableChild(new RGBSlider(cx-100, sy+sp*8+20, 200, 18, "AB", CrosshairConfig.INSTANCE.aimBlue, v -> CrosshairConfig.INSTANCE.aimBlue = v));
    }
    
    private void initPixelMode() {
        int rp = gridX + GRID_SIZE * PIXEL_SIZE + 30;
        addDrawableChild(ButtonWidget.builder(Text.literal(editingAim ? "Edit: AIM" : "Edit: NORMAL"), b -> {
            editingAim = !editingAim; b.setMessage(Text.literal(editingAim ? "Edit: AIM" : "Edit: NORMAL"));
        }).dimensions(rp, 50, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(CrosshairConfig.INSTANCE.useAimDesign ? "AimMode:ON" : "AimMode:OFF"), b -> {
            CrosshairConfig.INSTANCE.useAimDesign = !CrosshairConfig.INSTANCE.useAimDesign;
            b.setMessage(Text.literal(CrosshairConfig.INSTANCE.useAimDesign ? "AimMode:ON" : "AimMode:OFF"));
        }).dimensions(rp, 75, 100, 20).build());
        int sy = 105;
        addDrawableChild(new RGBSlider(rp, sy, 100, 16, "NR", CrosshairConfig.INSTANCE.normalRed, v -> CrosshairConfig.INSTANCE.normalRed = v));
        addDrawableChild(new RGBSlider(rp, sy+18, 100, 16, "NG", CrosshairConfig.INSTANCE.normalGreen, v -> CrosshairConfig.INSTANCE.normalGreen = v));
        addDrawableChild(new RGBSlider(rp, sy+36, 100, 16, "NB", CrosshairConfig.INSTANCE.normalBlue, v -> CrosshairConfig.INSTANCE.normalBlue = v));
        addDrawableChild(new RGBSlider(rp, sy+58, 100, 16, "AR", CrosshairConfig.INSTANCE.aimRed, v -> CrosshairConfig.INSTANCE.aimRed = v));
        addDrawableChild(new RGBSlider(rp, sy+76, 100, 16, "AG", CrosshairConfig.INSTANCE.aimGreen, v -> CrosshairConfig.INSTANCE.aimGreen = v));
        addDrawableChild(new RGBSlider(rp, sy+94, 100, 16, "AB", CrosshairConfig.INSTANCE.aimBlue, v -> CrosshairConfig.INSTANCE.aimBlue = v));
        addDrawableChild(new CSlider(rp, sy+116, 100, 16, "Scale", CrosshairConfig.INSTANCE.pixelScale, 1, 10, v -> CrosshairConfig.INSTANCE.pixelScale = v));
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> {
            boolean[][] p = editingAim ? CrosshairConfig.INSTANCE.aimPixels : CrosshairConfig.INSTANCE.normalPixels;
            for (int y = 0; y < GRID_SIZE; y++) for (int x = 0; x < GRID_SIZE; x++) p[y][x] = false;
        }).dimensions(rp, sy+140, 48, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            boolean[][] p = editingAim ? CrosshairConfig.INSTANCE.aimPixels : CrosshairConfig.INSTANCE.normalPixels;
            for (int y = 0; y < GRID_SIZE; y++) for (int x = 0; x < GRID_SIZE; x++) p[y][x] = false;
            int c = GRID_SIZE/2;
            for (int x = c-4; x <= c+4; x++) if (x!=c) p[c][x] = true;
            for (int y = c-4; y <= c+4; y++) if (y!=c) p[y][c] = true;
        }).dimensions(rp+52, sy+140, 48, 20).build());
    }
    
    @Override
    public void render(DrawContext ctx, int mx, int my, float d) {
        ctx.fill(0, 0, width, height, 0xCC000000);
        
        if (CrosshairConfig.INSTANCE.pixelMode) {
            // Проверяем мышь через GLFW
            MinecraftClient mc = MinecraftClient.getInstance();
            long window = mc.getWindow().getHandle();
            boolean lmb = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean rmb = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            
            if (lmb || rmb) {
                int px = (mx - gridX) / PIXEL_SIZE;
                int py = (my - gridY) / PIXEL_SIZE;
                if (px >= 0 && px < GRID_SIZE && py >= 0 && py < GRID_SIZE) {
                    boolean[][] pixels = editingAim ? CrosshairConfig.INSTANCE.aimPixels : CrosshairConfig.INSTANCE.normalPixels;
                    pixels[py][px] = lmb;
                }
            }
            
            renderPixelMode(ctx, mx, my);
        } else {
            renderClassicMode(ctx);
        }
        
        super.render(ctx, mx, my, d);
    }
    
    private void renderClassicMode(DrawContext ctx) {
        int cx = width/2 + 130, cy = 100;
        drawClassicPreview(ctx, cx, cy, CrosshairConfig.INSTANCE.getNormalColor());
        drawClassicPreview(ctx, cx, cy + 80, CrosshairConfig.INSTANCE.getAimColor());
    }
    
    private void drawClassicPreview(DrawContext ctx, int cx, int cy, int col) {
        CrosshairConfig c = CrosshairConfig.INSTANCE;
        int sz = c.size, th = c.thickness, g = c.gap, h = th/2;
        ctx.fill(cx-sz-g, cy-h, cx-g, cy-h+th, col);
        ctx.fill(cx+g, cy-h, cx+sz+g, cy-h+th, col);
        ctx.fill(cx-h, cy-sz-g, cx-h+th, cy-g, col);
        ctx.fill(cx-h, cy+g, cx-h+th, cy+sz+g, col);
    }
    
    private void renderPixelMode(DrawContext ctx, int mx, int my) {
        boolean[][] pix = editingAim ? CrosshairConfig.INSTANCE.aimPixels : CrosshairConfig.INSTANCE.normalPixels;
        int col = editingAim ? CrosshairConfig.INSTANCE.getAimColor() : CrosshairConfig.INSTANCE.getNormalColor();
        
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                int px = gridX + x * PIXEL_SIZE, py = gridY + y * PIXEL_SIZE;
                ctx.fill(px, py, px + PIXEL_SIZE, py + PIXEL_SIZE, ((x + y) % 2 == 0) ? 0xFF3A3A3A : 0xFF2A2A2A);
                if (pix[y][x]) ctx.fill(px + 1, py + 1, px + PIXEL_SIZE - 1, py + PIXEL_SIZE - 1, col);
                if (x == GRID_SIZE/2 || y == GRID_SIZE/2) {
                    ctx.fill(px, py, px + PIXEL_SIZE, py + 1, 0x44FFFFFF);
                    ctx.fill(px, py, px + 1, py + PIXEL_SIZE, 0x44FFFFFF);
                }
            }
        }
        
        int hx = (mx - gridX) / PIXEL_SIZE, hy = (my - gridY) / PIXEL_SIZE;
        if (hx >= 0 && hx < GRID_SIZE && hy >= 0 && hy < GRID_SIZE) {
            int px = gridX + hx * PIXEL_SIZE, py = gridY + hy * PIXEL_SIZE;
            ctx.fill(px, py, px + PIXEL_SIZE, py + PIXEL_SIZE, 0x55FFFFFF);
        }
        
        int ex = gridX + GRID_SIZE * PIXEL_SIZE, ey = gridY + GRID_SIZE * PIXEL_SIZE;
        ctx.fill(gridX-2, gridY-2, ex+2, gridY, 0xFFFFFFFF);
        ctx.fill(gridX-2, ey, ex+2, ey+2, 0xFFFFFFFF);
        ctx.fill(gridX-2, gridY, gridX, ey, 0xFFFFFFFF);
        ctx.fill(ex, gridY, ex+2, ey, 0xFFFFFFFF);
        
        drawPixelPreview(ctx, gridX + GRID_SIZE * PIXEL_SIZE / 2, ey + 35, false);
    }
    
    private void drawPixelPreview(DrawContext ctx, int cx, int cy, boolean aim) {
        CrosshairConfig c = CrosshairConfig.INSTANCE;
        boolean[][] p = c.getCurrentPixels(aim);
        int col = aim ? c.getAimColor() : c.getNormalColor(), sc = c.pixelScale, off = (GRID_SIZE * sc) / 2;
        for (int y = 0; y < GRID_SIZE; y++) for (int x = 0; x < GRID_SIZE; x++) 
            if (p[y][x]) ctx.fill(cx - off + x * sc, cy - off + y * sc, cx - off + x * sc + sc, cy - off + y * sc + sc, col);
    }
    
    @Override public boolean shouldPause() { return false; }
    
    static class RGBSlider extends SliderWidget {
        String l; java.util.function.IntConsumer s;
        RGBSlider(int x, int y, int w, int h, String l, int v, java.util.function.IntConsumer s) {
            super(x, y, w, h, Text.literal(l + ":" + v), v / 255.0); this.l = l; this.s = s;
        }
        @Override protected void updateMessage() { setMessage(Text.literal(l + ":" + (int)(value * 255))); }
        @Override protected void applyValue() { s.accept((int)(value * 255)); }
    }
    
    static class CSlider extends SliderWidget {
        String l; int min, max; java.util.function.IntConsumer s;
        CSlider(int x, int y, int w, int h, String l, int v, int min, int max, java.util.function.IntConsumer s) {
            super(x, y, w, h, Text.literal(l + ":" + v), (v - min) / (double)(max - min));
            this.l = l; this.min = min; this.max = max; this.s = s;
        }
        @Override protected void updateMessage() { setMessage(Text.literal(l + ":" + (min + (int)(value * (max - min))))); }
        @Override protected void applyValue() { s.accept(min + (int)(value * (max - min))); }
    }
}
