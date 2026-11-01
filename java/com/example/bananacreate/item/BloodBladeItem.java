package com.example.bananacreate.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.entity.LivingEntity;

/** Espada que rouba um pouco de vida do alvo a cada golpe. */
public class BloodBladeItem extends SwordItem {
    public BloodBladeItem(Item.Properties properties) {
        super(Tiers.DIAMOND, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide) {
            attacker.heal(2.0f);
        }
        return result;
    }
}
