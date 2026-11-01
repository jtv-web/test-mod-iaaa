package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.MiniGoblin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MiniGoblinRenderer extends MobRenderer<MiniGoblin, VillagerModel<MiniGoblin>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "textures/entity/mini_goblin.png");

    public MiniGoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.3f);
    }

    @Override
    protected void scale(MiniGoblin goblin, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.55f, 0.55f, 0.55f);
    }

    @Override
    public ResourceLocation getTextureLocation(MiniGoblin goblin) {
        return TEXTURE;
    }
}
