package com.example.bananacreate.block;

import com.example.bananacreate.BananaCreate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class BananaTreeGrower {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BANANA_TREE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "banana_tree"));

    public static final TreeGrower BANANA = new TreeGrower(
            BananaCreate.MOD_ID + ":banana",
            Optional.empty(),
            Optional.of(BANANA_TREE_KEY),
            Optional.empty());
}
