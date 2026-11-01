package com.example.bananacreate.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Revela tudo que esta por perto, mesmo o que se esconde do outro lado. */
public class OthersideEyeItem extends Item {
    public OthersideEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(24.0), e -> e != player)) {
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }

        player.getCooldowns().addCooldown(this, 300);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
