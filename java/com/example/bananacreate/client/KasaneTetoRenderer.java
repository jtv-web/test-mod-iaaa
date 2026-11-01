package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.KasaneTeto;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class KasaneTetoRenderer extends HumanoidMobRenderer<KasaneTeto, KasaneTetoModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "textures/entity/kasane_teto.png");
    /** Olhos fechados e boca aberta: usada quando ela canta ou danca. */
    private static final ResourceLocation TEXTURE_SING =
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "textures/entity/kasane_teto_sing.png");

    public KasaneTetoRenderer(EntityRendererProvider.Context context) {
        super(context, new KasaneTetoModel(context.bakeLayer(KasaneTetoModel.LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(KasaneTeto teto) {
        return (teto.isSinging() || teto.isDancing()) ? TEXTURE_SING : TEXTURE;
    }
}
