package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.block.BananaLeavesBlock;
import com.example.bananacreate.block.BananaTreeGrower;
import com.example.bananacreate.block.BloodCentrifugeBlock;
import com.example.bananacreate.block.BloodExtractorBlock;
import com.example.bananacreate.block.TitanTreadmillBlock;
import com.example.bananacreate.block.ServoArmBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BananaCreate.MOD_ID);

    // --- Bananeira ---
    public static final DeferredBlock<RotatedPillarBlock> BANANA_LOG = BLOCKS.register("banana_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG)));

    public static final DeferredBlock<Block> BANANA_PLANKS = BLOCKS.register("banana_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS)));

    public static final DeferredBlock<BananaLeavesBlock> BANANA_LEAVES = BLOCKS.register("banana_leaves",
            () -> new BananaLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LEAVES)));

    public static final DeferredBlock<SaplingBlock> BANANA_SAPLING = BLOCKS.register("banana_sapling",
            () -> new SaplingBlock(BananaTreeGrower.BANANA, BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_SAPLING)));

    // --- Sangue ---
    public static final DeferredBlock<LiquidBlock> BLOOD_BLOCK = BLOCKS.register("blood",
            () -> new LiquidBlock(ModFluids.BLOOD.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    // Maquinas cineticas do Create
    public static final DeferredBlock<BloodExtractorBlock> BLOOD_EXTRACTOR = BLOCKS.register("blood_extractor",
            () -> new BloodExtractorBlock(BlockBehaviour.Properties.of()
                    .strength(3.5f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));

    public static final DeferredBlock<BloodCentrifugeBlock> BLOOD_CENTRIFUGE = BLOCKS.register("blood_centrifuge",
            () -> new BloodCentrifugeBlock(BlockBehaviour.Properties.of()
                    .strength(3.5f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));

    // Blocos roboticos cyberpunk (decorativos)
    public static final DeferredBlock<Block> CHROME_CASING = BLOCKS.register("chrome_casing",
            () -> new Block(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final DeferredBlock<ServoArmBlock> SERVO_ARM = BLOCKS.register("servo_arm",
            () -> new ServoArmBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops()
                    .noOcclusion().sound(SoundType.METAL)));

    public static final DeferredBlock<RotatedPillarBlock> CHROME_COGWHEEL = BLOCKS.register("chrome_cogwheel",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops()
                    .noOcclusion().sound(SoundType.METAL)));

    // Gerador cinetico movido por titas
    public static final DeferredBlock<TitanTreadmillBlock> TITAN_TREADMILL = BLOCKS.register("titan_treadmill",
            () -> new TitanTreadmillBlock(BlockBehaviour.Properties.of()
                    .strength(4.0f).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.METAL)));
}
