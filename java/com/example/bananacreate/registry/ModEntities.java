package com.example.bananacreate.registry;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.BananaProjectile;
import com.example.bananacreate.entity.KasaneTeto;
import com.example.bananacreate.entity.MiniGoblin;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, BananaCreate.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<BananaProjectile>> BANANA_PROJECTILE =
            ENTITIES.register("banana_projectile", () -> EntityType.Builder
                    .<BananaProjectile>of(BananaProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("banana_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<MiniGoblin>> MINI_GOBLIN =
            ENTITIES.register("mini_goblin", () -> EntityType.Builder
                    .of(MiniGoblin::new, MobCategory.CREATURE)
                    .sized(0.45f, 1.05f)
                    .clientTrackingRange(10)
                    .build("mini_goblin"));

    public static final DeferredHolder<EntityType<?>, EntityType<KasaneTeto>> KASANE_TETO =
            ENTITIES.register("kasane_teto", () -> EntityType.Builder
                    .of(KasaneTeto::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build("kasane_teto"));
}
