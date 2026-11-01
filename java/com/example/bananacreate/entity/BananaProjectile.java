package com.example.bananacreate.entity;

import com.example.bananacreate.registry.ModEntities;
import com.example.bananacreate.registry.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BananaProjectile extends ThrowableItemProjectile {
    public BananaProjectile(EntityType<? extends BananaProjectile> type, Level level) {
        super(type, level);
    }

    public BananaProjectile(Level level, LivingEntity owner) {
        super(ModEntities.BANANA_PROJECTILE.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BANANA.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide) {
            result.getEntity().hurt(damageSources().thrown(this, getOwner()), 6.0f);
            if (result.getEntity() instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100));
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            // deixa uma casca de banana no chao
            spawnAtLocation(new ItemStack(ModItems.BANANA_PEEL.get()));
            discard();
        }
    }
}
