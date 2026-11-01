package com.example.bananacreate.client;

import com.example.bananacreate.BananaCreate;
import com.example.bananacreate.entity.KasaneTeto;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Modelo humanoide da Teto: drills de 4 segmentos que balancam, cabelo de tras,
 * saia, e poses proprias para cantar e dancar.
 *
 * Mapa de UV (skin 64x64): linhas 0-31 sao o corpo padrao; abaixo ficam as pecas extras:
 *  drills (0,32) (0,40) (0,47) (0,53) | saia (20,32) | cabelo de tras (20,44)
 */
public class KasaneTetoModel extends HumanoidModel<KasaneTeto> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(BananaCreate.MOD_ID, "kasane_teto"), "main");

    private static final int SEGMENTS = 4;
    private final ModelPart[] drillRight = new ModelPart[SEGMENTS];
    private final ModelPart[] drillLeft = new ModelPart[SEGMENTS];

    public KasaneTetoModel(ModelPart root) {
        super(root);
        ModelPart r = root.getChild("head");
        ModelPart l = root.getChild("head");
        for (int i = 0; i < SEGMENTS; i++) {
            r = r.getChild("drill_right_" + (i + 1));
            l = l.getChild("drill_left_" + (i + 1));
            drillRight[i] = r;
            drillLeft[i] = l;
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        PartDefinition body = root.getChild("body");

        // cabelo de tras (franja que cobre a nuca)
        head.addOrReplaceChild("hair_back",
                CubeListBuilder.create().texOffs(20, 44).addBox(-4.5f, 0.0f, 2.5f, 9.0f, 5.0f, 1.0f),
                PartPose.ZERO);

        // saia
        body.addOrReplaceChild("skirt",
                CubeListBuilder.create().texOffs(20, 32).addBox(-4.5f, 0.0f, -2.5f, 9.0f, 4.0f, 5.0f),
                PartPose.offset(0.0f, 9.0f, 0.0f));

        addDrill(head, "right", -7.0f, 0.3f);
        addDrill(head, "left", 7.0f, -0.3f);
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** 4 blocos encadeados que vao afinando; cada um herda a rotacao do anterior. */
    private static void addDrill(PartDefinition head, String side, float x, float tilt) {
        PartDefinition s1 = head.addOrReplaceChild("drill_" + side + "_1",
                CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 3.0f, 4.0f),
                PartPose.offsetAndRotation(x, -7.0f, 0.0f, 0.0f, 0.0f, tilt));
        PartDefinition s2 = s1.addOrReplaceChild("drill_" + side + "_2",
                CubeListBuilder.create().texOffs(0, 40).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 3.0f, 3.0f),
                PartPose.offset(0.0f, 3.0f, 0.0f));
        PartDefinition s3 = s2.addOrReplaceChild("drill_" + side + "_3",
                CubeListBuilder.create().texOffs(0, 47).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offset(0.0f, 3.0f, 0.0f));
        s3.addOrReplaceChild("drill_" + side + "_4",
                CubeListBuilder.create().texOffs(0, 53).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f),
                PartPose.offset(0.0f, 3.0f, 0.0f));
    }

    @Override
    public void setupAnim(KasaneTeto teto, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(teto, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        boolean dancing = teto.isDancing();
        boolean singing = teto.isSinging();

        if (dancing) {
            float t = ageInTicks * 0.7f;
            rightArm.xRot = -2.6f + Mth.sin(t) * 0.4f;
            rightArm.zRot = 0.3f;
            leftArm.xRot = -2.6f + Mth.sin(t + Mth.PI) * 0.4f;
            leftArm.zRot = -0.3f;
            body.zRot = Mth.sin(t) * 0.08f;
            head.zRot = -body.zRot * 1.5f;
        } else if (singing) {
            // maos junto ao peito, como quem segura um microfone
            rightArm.xRot = -1.3f + Mth.sin(ageInTicks * 0.2f) * 0.05f;
            rightArm.yRot = -0.5f;
            leftArm.xRot = -1.0f + Mth.cos(ageInTicks * 0.2f) * 0.05f;
            leftArm.yRot = 0.5f;
            head.zRot = Mth.sin(ageInTicks * 0.15f) * 0.09f;
            head.xRot -= 0.12f;
        }
        hat.copyFrom(head);

        // drills: balanco parado + chicote ao andar + extra ao dancar/cantar
        float idle = Mth.sin(ageInTicks * 0.09f) * 0.05f;
        float bounce = Mth.cos(limbSwing * 1.3f) * 0.35f * limbSwingAmount;
        float party = dancing ? Mth.sin(ageInTicks * 0.7f) * 0.35f : (singing ? Mth.sin(ageInTicks * 0.2f) * 0.12f : 0.0f);
        for (int i = 0; i < SEGMENTS; i++) {
            float k = (i + 1) * 0.5f;
            drillRight[i].zRot = (i == 0 ? 0.3f : 0.0f) + (idle + party) * k;
            drillLeft[i].zRot = (i == 0 ? -0.3f : 0.0f) + (idle - party) * k;
            drillRight[i].xRot = bounce * k;
            drillLeft[i].xRot = bounce * k;
        }
    }
}
