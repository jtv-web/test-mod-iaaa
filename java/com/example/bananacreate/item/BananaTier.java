package com.example.bananacreate.item;

import com.example.bananacreate.registry.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum BananaTier implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 450;
    }

    @Override
    public float getSpeed() {
        return 7.0f;
    }

    @Override
    public float getAttackDamageBonus() {
        return 2.0f;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_IRON_TOOL;
    }

    @Override
    public int getEnchantmentValue() {
        return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.BANANA.get());
    }
}
