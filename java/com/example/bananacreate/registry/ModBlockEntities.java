package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.block.entity.BloodCentrifugeBE;
import com.example.bananacreate.block.entity.BloodExtractorBE;
import com.example.bananacreate.block.entity.TitanTreadmillBE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BananaCreate.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodExtractorBE>> BLOOD_EXTRACTOR =
            BLOCK_ENTITIES.register("blood_extractor", () ->
                    BlockEntityType.Builder.of(BloodExtractorBE::new, ModBlocks.BLOOD_EXTRACTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodCentrifugeBE>> BLOOD_CENTRIFUGE =
            BLOCK_ENTITIES.register("blood_centrifuge", () ->
                    BlockEntityType.Builder.of(BloodCentrifugeBE::new, ModBlocks.BLOOD_CENTRIFUGE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TitanTreadmillBE>> TITAN_TREADMILL =
            BLOCK_ENTITIES.register("titan_treadmill", () ->
                    BlockEntityType.Builder.of(TitanTreadmillBE::new, ModBlocks.TITAN_TREADMILL.get()).build(null));
}
