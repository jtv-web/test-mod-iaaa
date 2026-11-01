package com.example.bananacreate.block;

import com.example.bananacreate.block.entity.BloodExtractorBE;
import com.example.bananacreate.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Extrator de Sangue: maquina cinetica do Create. Quando recebe rotacao, fere
 * (de leve) os mobs em cima dele e enche um tanque de sangue, que pode ser
 * puxado por canos do Create.
 */
public class BloodExtractorBlock extends RotatedPillarKineticBlock implements IBE<BloodExtractorBE> {
    public BloodExtractorBlock(Properties properties) {
        super(properties);
    }

    /** Em pe por padrao (eixo Y). Agachado, segue a face clicada (deitado). */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean sneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        Direction.Axis axis = sneaking ? context.getClickedFace().getAxis() : Direction.Axis.Y;
        return defaultBlockState().setValue(AXIS, axis);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Class<BloodExtractorBE> getBlockEntityClass() {
        return BloodExtractorBE.class;
    }

    @Override
    public BlockEntityType<? extends BloodExtractorBE> getBlockEntityType() {
        return ModBlockEntities.BLOOD_EXTRACTOR.get();
    }
}
