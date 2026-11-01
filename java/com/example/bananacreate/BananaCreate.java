package com.example.bananacreate;

import com.example.bananacreate.entity.KasaneTeto;
import com.example.bananacreate.entity.MiniGoblin;
import com.example.bananacreate.registry.ModBlockEntities;
import com.example.bananacreate.registry.ModBlocks;
import com.example.bananacreate.registry.ModCreativeTabs;
import com.example.bananacreate.registry.ModEntities;
import com.example.bananacreate.registry.ModFluids;
import com.example.bananacreate.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@Mod(BananaCreate.MOD_ID)
public class BananaCreate {
    public static final String MOD_ID = "bananacreate";

    public BananaCreate(IEventBus modBus) {
        ModFluids.FLUID_TYPES.register(modBus);
        ModFluids.FLUIDS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        modBus.addListener(BananaCreate::registerCapabilities);
        modBus.addListener(BananaCreate::registerAttributes);
        modBus.addListener(BananaCreate::registerSpawnPlacements);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MINI_GOBLIN.get(), MiniGoblin.createAttributes().build());
        event.put(ModEntities.KASANE_TETO.get(), KasaneTeto.createAttributes().build());
    }

    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.MINI_GOBLIN.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ModEntities.KASANE_TETO.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR);
    }

    /** Expoe os tanques das maquinas para canos/bombas do Create. */
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.BLOOD_EXTRACTOR.get(), (be, side) -> be.tank);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.BLOOD_CENTRIFUGE.get(), (be, side) -> be.tank);
    }
}
