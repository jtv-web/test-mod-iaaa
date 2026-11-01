package com.example.bananacreate.entity;

import com.example.bananacreate.registry.ModBlocks;
import com.example.bananacreate.registry.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Kasane Teto (homenagem de fa, versao UTAU).
 *
 * Interacoes:
 *  - Segue quem segura pao na mao.
 *  - Shift + botao direito com pao: alimenta (cura, coracoes, aumenta a amizade e as vezes da presentes).
 *  - Botao direito: abre a troca (pao, note block, cookies, disco...). Com amizade 6+ libera a Jukebox.
 *  - Canta melodias de vez em quando (notas coloridas, olhos fechados e boca aberta).
 *  - Dança quando alguem joga pao no chao, quando toca um disco numa Jukebox perto
 *    OU quando uma maquina do Create esta girando por perto.
 *  - Vai ate um Note Block proximo e toca ele (sem mudar a nota do bloco).
 *  - Acena para jogadores que chegam perto.
 *  - Foge quando machucada.
 *  - Create: vende pecas/quitutes do Create, adora doces do Create, e faz show na Esteira de Tita
 *    (cada Teto cantando por perto soma RPM na esteira).
 */
public class KasaneTeto extends AbstractVillager {
    private static final EntityDataAccessor<Boolean> SINGING =
            SynchedEntityData.defineId(KasaneTeto.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DANCING =
            SynchedEntityData.defineId(KasaneTeto.class, EntityDataSerializers.BOOLEAN);

    /** Amizade: sobe a cada pao dado com shift + botao direito. */
    private int affection;
    private int greetCooldown;

    public KasaneTeto(EntityType<? extends KasaneTeto> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BREAD));
        setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SINGING, false);
        builder.define(DANCING, false);
    }

    public boolean isSinging() {
        return entityData.get(SINGING);
    }

    public void setSinging(boolean value) {
        entityData.set(SINGING, value);
    }

    public boolean isDancing() {
        return entityData.get(DANCING);
    }

    public void setDancing(boolean value) {
        entityData.set(DANCING, value);
    }

    public int getAffection() {
        return affection;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Affection", affection);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        affection = tag.getInt("Affection");
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        goalSelector.addGoal(1, new DanceGoal(this));
        goalSelector.addGoal(2, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(3, new TemptGoal(this, 1.0, KasaneTeto::isTetoFood, false));
        goalSelector.addGoal(3, new PlayNoteBlockGoal(this));
        goalSelector.addGoal(3, new ConcertGoal(this));
        goalSelector.addGoal(3, new SingGoal(this));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.4));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.VILLAGER_SPAWN_EGG) || !isAlive() || isTrading() || isBaby()) {
            return super.mobInteract(player, hand);
        }

        // Shift + direito com pao: alimentar
        if (isTetoFood(held) && player.isSecondaryUseActive()) {
            if (!level().isClientSide) {
                feed(player, held);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
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

    private void feed(Player player, ItemStack bread) {
        if (!player.getAbilities().instabuild) {
            bread.shrink(1);
        }
        heal(4.0f);
        affection++;
        playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.3f);
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.HEART, getX(), getY() + 1.9, getZ(), 4, 0.4, 0.3, 0.4, 0.0);
        }
        if (affection % 3 == 0) {
            giveGift();
        }
        if (affection == 6) {
            // amiga o bastante: libera uma troca nova
            getOffers().add(new MerchantOffer(new ItemCost(Items.EMERALD, 8),
                    new ItemStack(Items.JUKEBOX, 1), 2, 15, 0.05f));
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.NOTE, getX(), getY() + 2.1, getZ(), 6, 0.5, 0.3, 0.5, 1.0);
            }
        }
    }


    // ------------------------------------------------------------ Create

    /** Comidas do Create que a Teto tambem adora (alem do pao). */
    private static final Set<String> CREATE_FOODS =
            Set.of("dough", "sweet_roll", "honeyed_apple", "bar_of_chocolate", "builders_tea");

    public static boolean isTetoFood(ItemStack stack) {
        if (stack.is(Items.BREAD)) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals("create") && CREATE_FOODS.contains(id.getPath());
    }

    /** Item do Create pelo id (vazio se nao existir nessa versao do Create). */
    private static ItemStack createStack(String path, int count) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("create", path))
                .map(item -> new ItemStack(item, count))
                .orElse(ItemStack.EMPTY);
    }

    private static void addCreateOffer(MerchantOffers offers, ItemCost cost, String path, int count,
                                       int maxUses, int xp) {
        ItemStack result = createStack(path, count);
        if (!result.isEmpty()) {
            offers.add(new MerchantOffer(cost, result, maxUses, xp, 0.05f));
        }
    }

    /** Toca uma nota (semitons acima da base) e solta a nota musical colorida. */
    void sing(int semitone) {
        float pitch = 0.7f * (float) Math.pow(2.0, semitone / 12.0);
        playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, pitch);
        if (level() instanceof ServerLevel server) {
            // count = 0: o "deltaX" vira a cor da nota (0..1)
            server.sendParticles(ParticleTypes.NOTE, getX(), getY() + 2.1, getZ(),
                    0, semitone / 24.0, 0.0, 0.0, 1.0);
        }
    }

    /** Presente de agradecimento: cai no chao perto dela. */
    private void giveGift() {
        ItemStack gift = switch (random.nextInt(9)) {
            case 0 -> new ItemStack(Items.BREAD, 3);
            case 1 -> new ItemStack(Items.COOKIE, 4);
            case 2 -> new ItemStack(ModItems.BANANA.get(), 3);
            case 3 -> new ItemStack(Items.EMERALD, 2);
            case 4 -> new ItemStack(Items.NOTE_BLOCK, 1);
            case 5 -> createStack("andesite_alloy", 3);
            case 6 -> createStack("cogwheel", 2);
            case 7 -> createStack("sweet_roll", 3);
            default -> new ItemStack(Items.AMETHYST_SHARD, 2);
        };
        if (gift.isEmpty()) {
            gift = new ItemStack(Items.BREAD, 3);
        }
        spawnAtLocation(gift);
        playSound(SoundEvents.VILLAGER_YES, 1.0f, 1.4f);
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = getOffers();
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 1),
                new ItemStack(Items.BREAD, 6), 16, 2, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.BREAD, 10),
                new ItemStack(Items.EMERALD, 1), 12, 4, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5),
                new ItemStack(Items.NOTE_BLOCK, 1), 8, 6, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(ModItems.BANANA.get(), 6),
                new ItemStack(Items.COOKIE, 8), 10, 4, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 12),
                new ItemStack(Items.MUSIC_DISC_CAT, 1), 2, 12, 0.05f));
        // Create: ela vende pecas e quitutes do Create
        addCreateOffer(offers, new ItemCost(Items.EMERALD, 2), "andesite_alloy", 4, 12, 3);
        addCreateOffer(offers, new ItemCost(Items.EMERALD, 3), "cogwheel", 1, 10, 4);
        addCreateOffer(offers, new ItemCost(Items.EMERALD, 1), "sweet_roll", 3, 16, 2);
        addCreateOffer(offers, new ItemCost(Items.EMERALD, 5), "goggles", 1, 4, 8);
        addCreateOffer(offers, new ItemCost(Items.EMERALD, 8), "wrench", 1, 4, 8);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int xp = 3 + random.nextInt(4);
            level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5, getZ(), xp));
        }
    }

    /** Acena para jogadores que chegam perto. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide || !isAlive()) {
            return;
        }
        if (greetCooldown > 0) {
            greetCooldown--;
        } else if (tickCount % 20 == 0 && !isTrading() && !isSinging() && !isDancing()) {
            Player nearby = level().getNearestPlayer(this, 5.0);
            if (nearby != null && !nearby.isSpectator() && hasLineOfSight(nearby)) {
                getLookControl().setLookAt(nearby);
                swing(InteractionHand.MAIN_HAND);
                playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0f, 1.6f);
                greetCooldown = 1200;
            }
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
        return isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean getYesSound) {
        return getYesSound ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    // ------------------------------------------------------------------ goals

    /**
     * Danca (e pula) quando: ha pao no chao (ate comer), uma Jukebox toca por perto,
     * ou uma maquina do Create esta girando por perto ("danca das engrenagens").
     */
    static class DanceGoal extends Goal {
        private static final int NONE = 0, JUKEBOX = 1, KINETIC = 2;

        private final KasaneTeto teto;
        private int danceTicks;
        private int mode;
        private int scanDelay;
        private int cachedScan;
        private int kineticCooldown;

        DanceGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        private List<ItemEntity> foodNearby() {
            return teto.level().getEntitiesOfClass(ItemEntity.class,
                    teto.getBoundingBox().inflate(8.0, 3.0, 8.0),
                    item -> isTetoFood(item.getItem()));
        }

        /** Escaneia a redondeza (com cache) atras de Jukebox tocando ou maquina Create girando. */
        private int scan() {
            if (--scanDelay > 0) {
                return cachedScan;
            }
            scanDelay = 20;
            if (kineticCooldown > 0) {
                kineticCooldown = Math.max(0, kineticCooldown - 20);
            }
            int found = NONE;
            BlockPos c = teto.blockPosition();
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-9, -3, -9), c.offset(9, 3, 9))) {
                var state = teto.level().getBlockState(p);
                if (state.is(Blocks.JUKEBOX)
                        && teto.level().getBlockEntity(p) instanceof JukeboxBlockEntity jukebox
                        && jukebox.isRecordPlaying()) {
                    found = JUKEBOX;
                    break;
                }
                if (found == NONE && kineticCooldown == 0 && state.hasBlockEntity()
                        && teto.level().getBlockEntity(p) instanceof KineticBlockEntity kinetic
                        && Math.abs(kinetic.getSpeed()) >= 8.0f) {
                    found = KINETIC;
                }
            }
            cachedScan = found;
            return found;
        }

        @Override
        public boolean canUse() {
            return !teto.isTrading() && (scan() != NONE || !foodNearby().isEmpty());
        }

        @Override
        public boolean canContinueToUse() {
            if (teto.isTrading()) {
                return false;
            }
            return switch (mode) {
                case JUKEBOX -> scan() == JUKEBOX;
                case KINETIC -> danceTicks < 400 && scan() != NONE;
                default -> danceTicks < 200 && !foodNearby().isEmpty();
            };
        }

        @Override
        public void start() {
            danceTicks = 0;
            int s = scan();
            mode = s != NONE ? s : 0;
            teto.getNavigation().stop();
            teto.setDancing(true);
        }

        @Override
        public void tick() {
            danceTicks++;
            teto.getNavigation().stop();

            float spin = teto.getYRot() + (mode == 0 ? 25.0f : 12.0f);
            teto.setYRot(spin);
            teto.yBodyRot = spin;
            teto.yHeadRot = spin;

            if (danceTicks % 10 == 0) {
                teto.getJumpControl().jump();
            }
            if (danceTicks % 8 == 0 && teto.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.NOTE, teto.getX(), teto.getY() + 2.1, teto.getZ(),
                        1, 0.3, 0.2, 0.3, 1.0);
                if (mode == KINETIC) {
                    server.sendParticles(ParticleTypes.ELECTRIC_SPARK, teto.getX(), teto.getY() + 1.0,
                            teto.getZ(), 2, 0.4, 0.5, 0.4, 0.1);
                }
            }
        }

        @Override
        public void stop() {
            teto.setDancing(false);
            if (mode == KINETIC) {
                kineticCooldown = 1200; // nao fica dancando pra sempre perto de uma fabrica
            }
            if (mode == 0 && danceTicks >= 200) {
                List<ItemEntity> food = foodNearby();
                if (!food.isEmpty()) {
                    food.get(0).getItem().shrink(1); // comeu
                    teto.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.2f);
                }
            }
            danceTicks = 0;
        }
    }

    /** Canta uma melodia curta (escala pentatonica) com notas coloridas subindo. */
    static class SingGoal extends Goal {
        private static final int[] SCALE = {0, 2, 4, 7, 9, 12, 14};
        private final KasaneTeto teto;
        private int[] melody;
        private int index;
        private int tick;

        SingGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !teto.isTrading() && !teto.isInWaterOrBubble() && teto.getRandom().nextInt(500) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return !teto.isTrading() && index < melody.length;
        }

        @Override
        public void start() {
            melody = new int[8 + teto.getRandom().nextInt(5)];
            int step = 2;
            for (int i = 0; i < melody.length; i++) {
                step = Math.max(0, Math.min(SCALE.length - 1, step + teto.getRandom().nextInt(3) - 1));
                melody[i] = SCALE[step];
            }
            index = 0;
            tick = 0;
            teto.getNavigation().stop();
            teto.setSinging(true);
        }

        @Override
        public void tick() {
            teto.getNavigation().stop();
            Player listener = teto.level().getNearestPlayer(teto, 8.0);
            if (listener != null) {
                teto.getLookControl().setLookAt(listener, 30.0f, 30.0f);
            }
            if (++tick % 7 != 0 || index >= melody.length) {
                return;
            }
            teto.sing(melody[index++]);
        }

        @Override
        public void stop() {
            teto.setSinging(false);
            index = 0;
        }
    }

    /**
     * Show na Esteira de Tita: ela vai ate uma Esteira de Tita proxima e canta
     * (a Esteira soma +16 RPM por Teto cantando por perto, junto com as titas).
     */
    static class ConcertGoal extends Goal {
        private static final int[] SCALE = {0, 2, 4, 7, 9, 12, 14};
        private final KasaneTeto teto;
        private BlockPos stage;
        private int ticks;

        ConcertGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private BlockPos findTreadmill() {
            BlockPos c = teto.blockPosition();
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-12, -3, -12), c.offset(12, 3, 12))) {
                if (teto.level().getBlockState(p).is(ModBlocks.TITAN_TREADMILL.get())) {
                    double d = p.distSqr(c);
                    if (d < bestDist) {
                        bestDist = d;
                        best = p.immutable();
                    }
                }
            }
            return best;
        }

        @Override
        public boolean canUse() {
            if (teto.isTrading() || teto.getRandom().nextInt(300) != 0) {
                return false;
            }
            stage = findTreadmill();
            return stage != null;
        }

        @Override
        public boolean canContinueToUse() {
            return stage != null && ticks < 400 && !teto.isTrading()
                    && teto.level().getBlockState(stage).is(ModBlocks.TITAN_TREADMILL.get());
        }

        @Override
        public void start() {
            ticks = 0;
        }

        @Override
        public void tick() {
            ticks++;
            double tx = stage.getX() + 0.5;
            double ty = stage.getY() + 0.5;
            double tz = stage.getZ() + 0.5;
            teto.getLookControl().setLookAt(tx, ty, tz);
            if (teto.distanceToSqr(tx, ty, tz) > 16.0) {
                teto.setSinging(false);
                teto.getNavigation().moveTo(tx, stage.getY(), tz, 1.0);
                return;
            }
            teto.getNavigation().stop();
            teto.setSinging(true);
            if (ticks % 8 == 0) {
                teto.sing(SCALE[teto.getRandom().nextInt(SCALE.length)]);
            }
            if (ticks % 20 == 0 && teto.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK, tx, stage.getY() + 1.2, tz,
                        6, 0.5, 0.4, 0.5, 0.2);
            }
        }

        @Override
        public void stop() {
            teto.setSinging(false);
            stage = null;
            ticks = 0;
        }
    }

    /** Anda ate um Note Block proximo e toca varias vezes (nao muda a nota do bloco). */
    static class PlayNoteBlockGoal extends Goal {
        private final KasaneTeto teto;
        private BlockPos target;
        private int plays;
        private int cooldown;
        private int timeout;

        PlayNoteBlockGoal(KasaneTeto teto) {
            this.teto = teto;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private BlockPos findNoteBlock() {
            BlockPos c = teto.blockPosition();
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-8, -2, -8), c.offset(8, 2, 8))) {
                if (teto.level().getBlockState(p).is(Blocks.NOTE_BLOCK)
                        && teto.level().getBlockState(p.above()).isAir()) {
                    double d = p.distSqr(c);
                    if (d < bestDist) {
                        bestDist = d;
                        best = p.immutable();
                    }
                }
            }
            return best;
        }

        @Override
        public boolean canUse() {
            if (teto.isTrading() || teto.getRandom().nextInt(250) != 0) {
                return false;
            }
            target = findNoteBlock();
            return target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && plays < 8 && timeout < 300 && !teto.isTrading()
                    && teto.level().getBlockState(target).is(Blocks.NOTE_BLOCK);
        }

        @Override
        public void start() {
            plays = 0;
            cooldown = 0;
            timeout = 0;
        }

        @Override
        public void tick() {
            timeout++;
            double tx = target.getX() + 0.5;
            double ty = target.getY() + 0.5;
            double tz = target.getZ() + 0.5;
            teto.getLookControl().setLookAt(tx, ty, tz);
            if (teto.distanceToSqr(tx, ty, tz) > 6.25) {
                teto.getNavigation().moveTo(tx, target.getY(), tz, 0.9);
                return;
            }
            teto.getNavigation().stop();
            if (--cooldown <= 0) {
                teto.swing(InteractionHand.MAIN_HAND);
                teto.level().blockEvent(target, Blocks.NOTE_BLOCK, 0, 0);
                plays++;
                cooldown = 12;
            }
        }

        @Override
        public void stop() {
            target = null;
            plays = 0;
        }
    }
}
