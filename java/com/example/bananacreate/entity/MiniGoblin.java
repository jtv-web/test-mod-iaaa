package com.example.bananacreate.entity;

import com.example.bananacreate.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

/**
 * Mini Goblin: comerciante pequeno e original. Troca bananas, cabelo de goblin
 * e placas cromadas. Quando alguem joga pao no chao perto dele, ele danca.
 */
public class MiniGoblin extends AbstractVillager {
    public MiniGoblin(EntityType<? extends MiniGoblin> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new DanceForBreadGoal(this));
        goalSelector.addGoal(2, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.5));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.VILLAGER_SPAWN_EGG) || !isAlive() || isTrading() || isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (!getOffers().isEmpty()) {
            if (!level().isClientSide) {
                setTradingPlayer(player);
                openTradingScreen(player, getDisplayName(), 1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = getOffers();
        offers.add(new MerchantOffer(new ItemCost(ModItems.BANANA.get(), 8),
                new ItemStack(Items.EMERALD), 12, 4, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 2),
                new ItemStack(ModItems.GOBLIN_HAIR_TUFT.get(), 2), 8, 6, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.BREAD, 4),
                new ItemStack(ModItems.BANANA_SAPLING.get()), 6, 6, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4),
                new ItemStack(ModItems.CHROME_PLATE.get(), 3), 10, 8, 0.05f));
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int xp = 3 + random.nextInt(4);
            level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5, getZ(), xp));
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean getYesSound) {
        return getYesSound ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    /** Danca (gira e pula) enquanto houver pao no chao por perto; por fim, come o pao. */
    static class DanceForBreadGoal extends Goal {
        private final MiniGoblin goblin;
        private int danceTicks;

        DanceForBreadGoal(MiniGoblin goblin) {
            this.goblin = goblin;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        private List<ItemEntity> breadNearby() {
            return goblin.level().getEntitiesOfClass(ItemEntity.class,
                    goblin.getBoundingBox().inflate(8.0, 3.0, 8.0),
                    item -> item.getItem().is(Items.BREAD));
        }

        @Override
        public boolean canUse() {
            return !goblin.isTrading() && !breadNearby().isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            return !goblin.isTrading() && danceTicks < 200 && !breadNearby().isEmpty();
        }

        @Override
        public void start() {
            danceTicks = 0;
            goblin.getNavigation().stop();
        }

        @Override
        public void tick() {
            danceTicks++;
            goblin.getNavigation().stop();

            float spin = goblin.getYRot() + 25.0f;
            goblin.setYRot(spin);
            goblin.yBodyRot = spin;
            goblin.yHeadRot = spin;

            if (danceTicks % 10 == 0) {
                goblin.getJumpControl().jump();
            }
            if (danceTicks % 8 == 0 && goblin.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.NOTE, goblin.getX(), goblin.getY() + 1.3, goblin.getZ(),
                        1, 0.3, 0.2, 0.3, 1.0);
            }
        }

        @Override
        public void stop() {
            if (danceTicks >= 200) {
                List<ItemEntity> bread = breadNearby();
                if (!bread.isEmpty()) {
                    bread.get(0).getItem().shrink(1); // comeu um pao
                    goblin.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.2f);
                }
            }
            danceTicks = 0;
        }
    }
}
