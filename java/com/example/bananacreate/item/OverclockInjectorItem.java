package com.example.bananacreate.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Injetor de overclock: reflexos acelerados por 1 minuto, em troca de sangue. */
public class OverclockInjectorItem extends Item {
    public OverclockInjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getHealth() <= 7.0f && !player.getAbilities().instabuild) {
            return InteractionResultHolder.fail(stack); // vida baixa demais
        }

        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0));
            if (!player.getAbilities().instabuild) {
                player.hurt(level.damageSources().magic(), 6.0f);
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
