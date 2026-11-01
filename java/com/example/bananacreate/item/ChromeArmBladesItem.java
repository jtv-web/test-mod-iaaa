package com.example.bananacreate.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

/** Laminas de braco cromadas: golpes rapidos que fazem o alvo sangrar (Wither curto). */
public class ChromeArmBladesItem extends SwordItem {
    public ChromeArmBladesItem(Item.Properties properties) {
        super(Tiers.IRON, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
        }
        return result;
    }
}
