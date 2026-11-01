package com.example.bananacreate.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/**
 * Martelo do Copyright (paroquia original, sem marca registrada de ninguem):
 * cada golpe aplica uma "advertencia" no alvo. Na terceira, o alvo e
 * removido por violacao de direitos autorais.
 */
public class CopyrightHammerItem extends SwordItem {
    private static final String STRIKE_PREFIX = "bananacreate_strike_";

    public CopyrightHammerItem(Item.Properties properties) {
        super(Tiers.IRON, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!attacker.level().isClientSide && !(target instanceof Player)) {
            int strikes = 0;
            for (int i = 1; i <= 3; i++) {
                if (target.getTags().contains(STRIKE_PREFIX + i)) {
                    strikes = i;
                }
            }
            strikes++;

            if (strikes >= 3) {
                if (target.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + target.getBbHeight() / 2,
                            target.getZ(), 1, 0, 0, 0, 0);
                }
                if (attacker instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("message.bananacreate.takedown", target.getDisplayName()), true);
                }
                target.discard();
            } else {
                target.addTag(STRIKE_PREFIX + strikes);
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600));
                if (attacker instanceof Player player) {
                    player.displayClientMessage(
                            Component.translatable("message.bananacreate.strike", target.getDisplayName(), strikes), true);
                }
            }
        }
        return result;
    }
}
