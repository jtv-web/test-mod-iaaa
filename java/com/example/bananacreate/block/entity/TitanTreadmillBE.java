package com.example.bananacreate.block.entity;

import com.example.bananacreate.entity.KasaneTeto;
import com.example.bananacreate.item.TitanSyringeItem;
import com.example.bananacreate.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Esteira de Tita: gerador de energia cinetica do Create.
 * Titas (mobs transformados com a Seringa de Tita) por perto sao acalmados e
 * ficam andando em circulo em volta da maquina, empurrando-a. Cada tita soma
 * 16 RPM (maximo 64) e a maquina fornece estresse por RPM.
 * Kasane Teto cantando por perto (show) tambem soma 16 RPM cada.
 */
public class TitanTreadmillBE extends GeneratingKineticBlockEntity {
    private static final double RADIUS_SCAN = 10.0;
    private static final double WALK_RADIUS = 5.0;
    private static final float CAPACITY_PER_RPM = 32.0f;

    private int titanCount;

    public TitanTreadmillBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TITAN_TREADMILL.get(), pos, state);
    }

    @Override
    public float getGeneratedSpeed() {
        return Math.min(64, 16 * titanCount);
    }

    @Override
    public float calculateAddedStressCapacity() {
        this.lastCapacityProvided = CAPACITY_PER_RPM;
        return CAPACITY_PER_RPM;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        long time = level.getGameTime();
        if (time % 5 != 0) return;

        List<Mob> titans = level.getEntitiesOfClass(Mob.class,
                new AABB(worldPosition).inflate(RADIUS_SCAN, RADIUS_SCAN, RADIUS_SCAN),
                m -> m.getTags().contains(TitanSyringeItem.TITAN_TAG));

        int index = 0;
        for (Mob titan : titans) {
            titan.setTarget(null); // titas na esteira ficam calmos
            double angle = (time / 40.0) + index * (2 * Math.PI / Math.max(1, titans.size()));
            double tx = worldPosition.getX() + 0.5 + Math.cos(angle) * WALK_RADIUS;
            double tz = worldPosition.getZ() + 0.5 + Math.sin(angle) * WALK_RADIUS;
            titan.getNavigation().moveTo(tx, worldPosition.getY() + 1, tz, 0.9);
            index++;
        }

        // Cada Kasane Teto cantando por perto ("show") conta como mais um "corredor": +16 RPM.
        int idols = level.getEntitiesOfClass(KasaneTeto.class,
                new AABB(worldPosition).inflate(8.0, 4.0, 8.0), KasaneTeto::isSinging).size();

        int total = titans.size() + idols;
        if (total != titanCount) {
            titanCount = total;
            updateGeneratedRotation();
        }
    }
}
