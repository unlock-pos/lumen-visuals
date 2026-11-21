package com.lumen.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LumenScreen extends Screen {
    public LumenScreen() {
        super(Text.literal("LUMEN Visuals"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);

        int w = 320, h = 230;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        // Тёмное стекло macOS + фиолетовая рамка
        ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0x667C5CFF);
        ctx.fill(x, y, x + w, y + h, 0xE612121A);

        // Кнопки окна macOS (красная, жёлтая, зелёная)
        ctx.fill(x + 12, y + 12, x + 20, y + 20, 0xFFFF5F56);
        ctx.fill(x + 25, y + 12, x + 33, y + 20, 0xFFFFBD2E);
        ctx.fill(x + 38, y + 12, x + 46, y + 20, 0xFF27C93F);

        ctx.drawTextWithShadow(this.textRenderer, "LUMEN VISUALS • 1.21.4", x + 60, y + 12, 0xFFFFFFFF);

        drawCard(ctx, x + 14, y + 36, "Target HUD", LumenConfig.targetHud, mouseX, mouseY);
        drawCard(ctx, x + 14, y + 66, "Hit Particles", LumenConfig.hitParticles, mouseX, mouseY);
        drawCard(ctx, x + 14, y + 96, "Trails & Glow", LumenConfig.trailsGlow, mouseX, mouseY);
        drawCard(ctx, x + 14, y + 126, "Armor & Totem HUD", LumenConfig.armorHud, mouseX, mouseY);
        drawCard(ctx, x + 14, y + 156, "Smooth Zoom (C)", LumenConfig.smoothZoom, mouseX, mouseY);
        drawCard(ctx, x + 14, y + 186, "FullBright (Night)", LumenConfig.fullBright, mouseX, mouseY);
    }

    private void drawCard(DrawContext ctx, int x, int y, String name, boolean state, int mx, int my) {
        int w = 292, h = 24;
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        ctx.fill(x, y, x + w, y + h, hover ? 0x26FFFFFF : 0x12FFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, name, x + 10, y + 8, 0xFFF5F5F7);

        // Тумблер как на сайте
        int tx = x + w - 36, ty = y + 5;
        ctx.fill(tx, ty, tx + 28, ty + 14, state ? 0xFF7C5CFF : 0xFF2A2A35);
        int dotX = state ? tx + 16 : tx + 2;
        ctx.fill(dotX, ty + 2, dotX + 10, ty + 12, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - 320) / 2 + 14;
        int y = (this.height - 230) / 2 + 36;
        if (mouseX >= x && mouseX <= x + 292) {
            int idx = (int) ((mouseY - y) / 30);
            if (idx == 0) LumenConfig.targetHud = !LumenConfig.targetHud;
            if (idx == 1) LumenConfig.hitParticles = !LumenConfig.hitParticles;
            if (idx == 2) LumenConfig.trailsGlow = !LumenConfig.trailsGlow;
            if (idx == 3) LumenConfig.armorHud = !LumenConfig.armorHud;
            if (idx == 4) LumenConfig.smoothZoom = !LumenConfig.smoothZoom;
            if (idx == 5) LumenConfig.fullBright = !LumenConfig.fullBright;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
