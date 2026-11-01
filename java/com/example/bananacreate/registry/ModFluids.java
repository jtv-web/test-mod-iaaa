package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, BananaCreate.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, BananaCreate.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> BLOOD_TYPE = FLUID_TYPES.register("blood",
            () -> new FluidType(FluidType.Properties.create()
                    .density(1100)
                    .viscosity(1500)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BLOOD = FLUIDS.register("blood",
            () -> new BaseFlowingFluid.Source(ModFluids.BLOOD_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> BLOOD_FLOWING = FLUIDS.register("blood_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.BLOOD_PROPERTIES));

    public static final BaseFlowingFluid.Properties BLOOD_PROPERTIES =
            new BaseFlowingFluid.Properties(BLOOD_TYPE, BLOOD, BLOOD_FLOWING)
                    .block(ModBlocks.BLOOD_BLOCK)
                    .bucket(ModItems.BLOOD_BUCKET)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2);
}
