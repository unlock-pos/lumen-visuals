package com.lumen.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class LumenClient implements ClientModInitializer {
    private static KeyBinding menuKey;
    private static LivingEntity lastTarget;
    private static long targetTime;
    private static float animHp = 20f;

    @Override
    public void onInitializeClient() {
        // Кнопка Right Shift открывает меню LUMEN
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lumen.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "LUMEN Visuals"
        ));

        // Hit Particles + запоминание цели для Target HUD
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && entity instanceof LivingEntity living) {
                lastTarget = living;
                targetTime = System.currentTimeMillis();
                if (LumenConfig.hitParticles) {
                    for (int i = 0; i < 14; i++) {
                        world.addParticle(ParticleTypes.END_ROD,
                                living.getX(), living.getBodyY(0.5), living.getZ(),
                                (world.random.nextDouble() - 0.5) * 0.45,
                                world.random.nextDouble() * 0.35,
                                (world.random.nextDouble() - 0.5) * 0.45);
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Тики: открытие меню, Trails за стрелами/перлами, FullBright
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (menuKey.wasPressed()) {
                client.setScreen(new LumenScreen());
            }

            if (LumenConfig.fullBright) {
                client.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false));
            }

            if (LumenConfig.trailsGlow) {
                client.world.getEntities().forEach(e -> {
                    if (e instanceof ProjectileEntity && !e.isOnGround()) {
                        client.world.addParticle(ParticleTypes.DRAGON_BREATH, e.getX(), e.getY(), e.getZ(), 0, 0, 0);
                    }
                });
            }
        });

        // Отрисовка Target HUD и Armor/Totem HUD на экране
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> renderHud(ctx));
    }

    private void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();

        // 1. Target HUD
        if (LumenConfig.targetHud && lastTarget != null && lastTarget.isAlive() && System.currentTimeMillis() - targetTime < 4500) {
            int x = sw / 2 + 16, y = sh / 2 + 12, w = 132, h = 40;
            ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0x887C5CFF);
            ctx.fill(x, y, x + w, y + h, 0xCC12121A);

            String name = lastTarget.getName().getString();
            float hp = lastTarget.getHealth();
            float maxHp = Math.max(1f, lastTarget.getMaxHealth());
            animHp += (hp - animHp) * 0.15f;

            ctx.drawTextWithShadow(mc.textRenderer, name, x + 8, y + 6, 0xFFFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, String.format("%.1f ❤  %.1fm", hp, mc.player.distanceTo(lastTarget)), x + 8, y + 17, 0xFFC4B5FD);

            int barW = (int) ((w - 16) * Math.min(1f, animHp / maxHp));
            ctx.fill(x + 8, y + 29, x + w - 8, y + 34, 0xFF2A2A35);
            ctx.fill(x + 8, y + 29, x + 8 + barW, y + 34, 0xFF7C5CFF);
        }

        // 2. Armor & Totem HUD над хотбаром
        if (LumenConfig.armorHud) {
            int ax = sw / 2 + 12;
            int ay = sh - 56;
            for (ItemStack armor : mc.player.getArmorItems()) {
                if (!armor.isEmpty()) {
                    ctx.drawItem(armor, ax, ay);
                    ctx.drawItemInSlot(mc.textRenderer, armor, ax, ay);
                    ax += 18;
                }
            }
            int totems = mc.player.getInventory().count(Items.TOTEM_OF_UNDYING);
            if (totems > 0) {
                ItemStack totemStack = new ItemStack(Items.TOTEM_OF_UNDYING, totems);
                ctx.drawItem(totemStack, sw / 2 - 9, sh - 56);
                ctx.drawItemInSlot(mc.textRenderer, totemStack, sw / 2 - 9, sh - 56);
            }
        }
    }
          }
