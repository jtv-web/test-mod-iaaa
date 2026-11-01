package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.item.BananaCannonItem;
import com.example.bananacreate.item.BananaTier;
import com.example.bananacreate.item.BloodBladeItem;
import com.example.bananacreate.item.ChromeArmBladesItem;
import com.example.bananacreate.item.CopyrightHammerItem;
import com.example.bananacreate.item.GoblinHairSwordItem;
import com.example.bananacreate.item.OthersideEyeItem;
import com.example.bananacreate.item.OverclockInjectorItem;
import com.example.bananacreate.item.TitanSyringeItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BananaCreate.MOD_ID);

    // --- Banana ---
    public static final DeferredItem<Item> BANANA = ITEMS.register("banana",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build())));

    public static final DeferredItem<Item> BANANA_PEEL = ITEMS.register("banana_peel",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> BANANA_SWORD = ITEMS.register("banana_sword",
            () -> new SwordItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(BananaTier.INSTANCE, 3, -2.4f))));

    public static final DeferredItem<PickaxeItem> BANANA_PICKAXE = ITEMS.register("banana_pickaxe",
            () -> new PickaxeItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(BananaTier.INSTANCE, 1.0f, -2.8f))));

    public static final DeferredItem<AxeItem> BANANA_AXE = ITEMS.register("banana_axe",
            () -> new AxeItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(AxeItem.createAttributes(BananaTier.INSTANCE, 6.0f, -3.1f))));

    public static final DeferredItem<ShovelItem> BANANA_SHOVEL = ITEMS.register("banana_shovel",
            () -> new ShovelItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(BananaTier.INSTANCE, 1.5f, -3.0f))));

    public static final DeferredItem<HoeItem> BANANA_HOE = ITEMS.register("banana_hoe",
            () -> new HoeItem(BananaTier.INSTANCE, new Item.Properties()
                    .attributes(HoeItem.createAttributes(BananaTier.INSTANCE, -2.0f, -1.0f))));

    public static final DeferredItem<BananaCannonItem> BANANA_CANNON = ITEMS.register("banana_cannon",
            () -> new BananaCannonItem(new Item.Properties().stacksTo(1).durability(500)));

    // --- Paranormal / Sangue ---
    public static final DeferredItem<Item> BLOOD_VIAL = ITEMS.register("blood_vial",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<BucketItem> BLOOD_BUCKET = ITEMS.register("blood_bucket",
            () -> new BucketItem(ModFluids.BLOOD.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<BloodBladeItem> BLOOD_BLADE = ITEMS.register("blood_blade",
            () -> new BloodBladeItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.2f))));

    public static final DeferredItem<OthersideEyeItem> OTHERSIDE_EYE = ITEMS.register("otherside_eye",
            () -> new OthersideEyeItem(new Item.Properties().rarity(Rarity.EPIC).durability(64)));

    // --- Titas / Copyright ---
    public static final DeferredItem<TitanSyringeItem> TITAN_SYRINGE = ITEMS.register("titan_syringe",
            () -> new TitanSyringeItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(16)));

    public static final DeferredItem<CopyrightHammerItem> COPYRIGHT_HAMMER = ITEMS.register("copyright_hammer",
            () -> new CopyrightHammerItem(new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 4, -2.8f))));

    // --- Cyberware ---
    public static final DeferredItem<Item> CHROME_PLATE = ITEMS.register("chrome_plate",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NEURAL_CHIP = ITEMS.register("neural_chip",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<ChromeArmBladesItem> CHROME_ARM_BLADES = ITEMS.register("chrome_arm_blades",
            () -> new ChromeArmBladesItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 2, -1.2f))));

    public static final DeferredItem<OverclockInjectorItem> OVERCLOCK_INJECTOR = ITEMS.register("overclock_injector",
            () -> new OverclockInjectorItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));

    // --- Mini Goblin ---
    public static final DeferredItem<DeferredSpawnEggItem> MINI_GOBLIN_SPAWN_EGG = ITEMS.register("mini_goblin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MINI_GOBLIN, 0x4a8f3a, 0xc98a3c, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> KASANE_TETO_SPAWN_EGG = ITEMS.register("kasane_teto_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.KASANE_TETO, 0xd02a4a, 0x3a3a44, new Item.Properties()));

    public static final DeferredItem<Item> GOBLIN_HAIR_TUFT = ITEMS.register("goblin_hair_tuft",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HAIR_PLATE = ITEMS.register("hair_plate",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<GoblinHairSwordItem> GOBLIN_HAIR_SWORD = ITEMS.register("goblin_hair_sword",
            () -> new GoblinHairSwordItem(new Item.Properties()
                    .rarity(Rarity.RARE)
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -1.8f))));

    // --- Block items ---
    public static final DeferredItem<BlockItem> CHROME_CASING = ITEMS.registerSimpleBlockItem("chrome_casing", ModBlocks.CHROME_CASING);
    public static final DeferredItem<BlockItem> SERVO_ARM = ITEMS.registerSimpleBlockItem("servo_arm", ModBlocks.SERVO_ARM);
    public static final DeferredItem<BlockItem> CHROME_COGWHEEL = ITEMS.registerSimpleBlockItem("chrome_cogwheel", ModBlocks.CHROME_COGWHEEL);
    public static final DeferredItem<BlockItem> BANANA_LOG = ITEMS.registerSimpleBlockItem("banana_log", ModBlocks.BANANA_LOG);
    public static final DeferredItem<BlockItem> BANANA_PLANKS = ITEMS.registerSimpleBlockItem("banana_planks", ModBlocks.BANANA_PLANKS);
    public static final DeferredItem<BlockItem> BANANA_LEAVES = ITEMS.registerSimpleBlockItem("banana_leaves", ModBlocks.BANANA_LEAVES);
    public static final DeferredItem<BlockItem> BANANA_SAPLING = ITEMS.registerSimpleBlockItem("banana_sapling", ModBlocks.BANANA_SAPLING);
    public static final DeferredItem<BlockItem> BLOOD_EXTRACTOR = ITEMS.registerSimpleBlockItem("blood_extractor", ModBlocks.BLOOD_EXTRACTOR);
    public static final DeferredItem<BlockItem> BLOOD_CENTRIFUGE = ITEMS.registerSimpleBlockItem("blood_centrifuge", ModBlocks.BLOOD_CENTRIFUGE);
    public static final DeferredItem<BlockItem> TITAN_TREADMILL = ITEMS.registerSimpleBlockItem("titan_treadmill", ModBlocks.TITAN_TREADMILL);
}
