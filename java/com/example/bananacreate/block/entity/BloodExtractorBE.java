package com.example.bananacreate.block.entity;

import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModFluids;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class BloodExtractorBE extends KineticBlockEntity {
    public final FluidTank tank = new FluidTank(4000, fs -> fs.getFluid().isSame(ModFluids.BLOOD.get()));

    public BloodExtractorBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_EXTRACTOR.get(), pos, state);
    }

    /** Impacto de estresse por RPM (o Create multiplica pela velocidade). */
    @Override
    public float calculateStressApplied() {
        return 4.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        float speed = Math.abs(getSpeed());
        if (speed == 0 || tank.getSpace() <= 0) return;
        if (level.getGameTime() % 20 != 0) return;

        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(worldPosition.above()), e -> !(e instanceof Player));
        for (LivingEntity victim : victims) {
            if (victim.hurt(level.damageSources().generic(), 1.0f)) {
                int amount = Math.max(10, (int) (speed / 16f * 50f));
                tank.fill(new FluidStack(ModFluids.BLOOD.get(), amount), IFluidHandler.FluidAction.EXECUTE);
                setChanged();
                sendData();
            }
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        tank.readFromNBT(registries, compound.getCompound("Tank"));
    }
}
