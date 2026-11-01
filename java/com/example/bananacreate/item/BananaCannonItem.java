package com.example.bananacreate.item;

import com.example.bananacreate.entity.BananaProjectile;
import com.example.bananacreate.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BananaCannonItem extends Item {
    public BananaCannonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack cannon = player.getItemInHand(hand);
        boolean creative = player.getAbilities().instabuild;
        ItemStack ammo = findAmmo(player);

        if (ammo.isEmpty() && !creative) {
            return InteractionResultHolder.fail(cannon);
        }

        if (!level.isClientSide) {
            BananaProjectile projectile = new BananaProjectile(level, player);
            projectile.setItem(new ItemStack(ModItems.BANANA.get()));
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 2.2f, 1.0f);
            level.addFreshEntity(projectile);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0f, 0.8f);

            if (!creative) {
                ammo.shrink(1);
                cannon.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
        }

        player.getCooldowns().addCooldown(this, 12);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(cannon, level.isClientSide());
    }

    private static ItemStack findAmmo(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.BANANA.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
