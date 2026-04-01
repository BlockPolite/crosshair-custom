package com.pvpcrosshair.mixin;

import com.pvpcrosshair.CrosshairConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Unique
    private boolean isAimingAtPlayer() {
        if (client.crosshairTarget == null) return false;
        if (client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hit = (EntityHitResult) client.crosshairTarget;
            if (hit.getEntity() instanceof PlayerEntity p) return p != client.player;
        }
        return false;
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client.options == null) return;
        if (client.interactionManager != null && client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR && client.targetedEntity == null) return;

        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        int cx = w / 2;
        int cy = h / 2;

        CrosshairConfig cfg = CrosshairConfig.INSTANCE;
        boolean aiming = isAimingAtPlayer();
        int color = aiming ? cfg.getAimColor() : cfg.getNormalColor();

        if (cfg.pixelMode) {
            // Пиксельный режим
            boolean[][] pixels = cfg.getCurrentPixels(aiming);
            int scale = cfg.pixelScale;
            int gridSize = CrosshairConfig.GRID_SIZE;
            int offset = (gridSize * scale) / 2;
            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    if (pixels[y][x]) {
                        int px = cx - offset + x * scale;
                        int py = cy - offset + y * scale;
                        context.fill(px, py, px + scale, py + scale, color);
                    }
                }
            }
        } else {
            // Классический режим
            int size = cfg.size;
            int thick = cfg.thickness;
            int gap = cfg.gap;
            int half = thick / 2;
            context.fill(cx - size - gap, cy - half, cx - gap, cy - half + thick, color);
            context.fill(cx + gap, cy - half, cx + size + gap, cy - half + thick, color);
            context.fill(cx - half, cy - size - gap, cx - half + thick, cy - gap, color);
            context.fill(cx - half, cy + gap, cx - half + thick, cy + size + gap, color);
        }

        // Индикатор кулдауна
        if (client.player != null) {
            float cooldown = client.player.getAttackCooldownProgress(0.0f);
            if (cooldown < 1.0f) {
                int barWidth = cfg.pixelMode ? CrosshairConfig.GRID_SIZE * cfg.pixelScale : cfg.size * 2 + cfg.gap * 2;
                int barY = cy + (cfg.pixelMode ? (CrosshairConfig.GRID_SIZE * cfg.pixelScale)/2 : cfg.size + cfg.gap) + 3;
                int barX = cx - barWidth / 2;
                context.fill(barX, barY, barX + barWidth, barY + 2, 0xFF333333);
                context.fill(barX, barY, barX + (int)(cooldown * barWidth), barY + 2, color);
            }
        }

        ci.cancel();
    }
}
