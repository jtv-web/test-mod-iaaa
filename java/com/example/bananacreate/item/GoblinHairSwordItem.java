package com.example.bananacreate.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/** Espada leve de fio de goblin reforcado: ataque rapido e deixa o alvo tonto. */
public class GoblinHairSwordItem extends SwordItem {
    public GoblinHairSwordItem(Item.Properties properties) {
        super(Tiers.IRON, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide) {
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60));
        }
        return result;
    }
}
