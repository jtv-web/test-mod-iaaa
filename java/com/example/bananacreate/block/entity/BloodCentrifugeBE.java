package com.example.bananacreate.block.entity;

import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModFluids;
import com.example.bananacreate.registry.ModItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class BloodCentrifugeBE extends KineticBlockEntity {
    private static final int COST_PER_VIAL = 250;

    public final FluidTank tank = new FluidTank(4000, fs -> fs.getFluid().isSame(ModFluids.BLOOD.get()));
    private int progress;

    public BloodCentrifugeBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_CENTRIFUGE.get(), pos, state);
    }

    @Override
    public float calculateStressApplied() {
        return 8.0f;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        float speed = Math.abs(getSpeed());
        if (speed == 0 || tank.getFluidAmount() < COST_PER_VIAL) {
            progress = 0;
            return;
        }

        // mais rotacao = processa mais rapido
        progress += Math.max(1, (int) (speed / 8f));
        if (progress >= 200) {
            progress = 0;
            tank.drain(COST_PER_VIAL, IFluidHandler.FluidAction.EXECUTE);

            ItemEntity vial = new ItemEntity(level,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5,
                    new ItemStack(ModItems.BLOOD_VIAL.get()));
            vial.setDeltaMovement(0, 0.15, 0);
            level.addFreshEntity(vial);

            setChanged();
            sendData();
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
        compound.putInt("Progress", progress);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        tank.readFromNBT(registries, compound.getCompound("Tank"));
        progress = compound.getInt("Progress");
    }
}
