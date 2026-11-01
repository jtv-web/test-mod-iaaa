package com.example.bananacreate.item;

import com.example.bananacreate.BananaCreate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Seringa de Tita: use em um mob agressivo para transforma-lo em um tita
 * gigante (escala x6, muita vida e dano). Titas podem ser colocados na
 * Esteira de Tita para gerar energia cinetica do Create.
 */
public class TitanSyringeItem extends Item {
    public static final String TITAN_TAG = "bananacreate_titan";

    public TitanSyringeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                  InteractionHand hand) {
        if (!(target instanceof Enemy) || !(target instanceof Mob mob) || mob.getTags().contains(TITAN_TAG)) {
            return InteractionResult.PASS;
        }

        if (!player.level().isClientSide) {
            makeTitan(mob);
            player.level().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 1.0f, 0.6f);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    public static void makeTitan(Mob mob) {
        addModifier(mob, Attributes.SCALE, "scale", 5.0, AttributeModifier.Operation.ADD_VALUE);
        addModifier(mob, Attributes.MAX_HEALTH, "health", 80.0, AttributeModifier.Operation.ADD_VALUE);
        addModifier(mob, Attributes.ATTACK_DAMAGE, "damage", 10.0, AttributeModifier.Operation.ADD_VALUE);
        addModifier(mob, Attributes.KNOCKBACK_RESISTANCE, "knockback", 1.0, AttributeModifier.Operation.ADD_VALUE);
        addModifier(mob, Attributes.MOVEMENT_SPEED, "speed", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        mob.setHealth(mob.getMaxHealth());
        mob.setPersistenceRequired();
        mob.addTag(TITAN_TAG);
    }

    private static void addModifier(Mob mob, Holder<Attribute> attribute, String name, double amount,
                                    AttributeModifier.Operation op) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.addPermanentModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "titan_" + name), amount, op));
        }
    }
}
