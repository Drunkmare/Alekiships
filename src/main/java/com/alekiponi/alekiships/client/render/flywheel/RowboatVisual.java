package com.alekiponi.alekiships.client.render.flywheel;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.BoatAtlases;
import com.alekiponi.alekiships.client.model.entity.RowboatEntityModel;
import com.alekiponi.alekiships.client.render.ShipSheets;
import com.alekiponi.alekiships.client.reosurces.BoatAtlasHolder;
import com.alekiponi.alekiships.common.entity.vehicle.RowboatEntity;
import com.alekiponi.alekiships.util.AlekiShipsHelper;
import com.alekiponi.alekiships.util.VanillaWood;
import com.ibm.icu.impl.Row;
import com.jozufozu.flywheel.api.material.Material;
import com.jozufozu.flywheel.api.model.Model;
import com.jozufozu.flywheel.api.visual.DynamicVisual;
import com.jozufozu.flywheel.api.visual.VisualFrameContext;
import com.jozufozu.flywheel.api.visual.VisualTickContext;
import com.jozufozu.flywheel.api.visualization.VisualizationContext;
import com.jozufozu.flywheel.lib.instance.InstanceTypes;
import com.jozufozu.flywheel.lib.instance.TransformedInstance;
import com.jozufozu.flywheel.lib.material.CutoutShaders;
import com.jozufozu.flywheel.lib.material.SimpleMaterial;
import com.jozufozu.flywheel.lib.model.ModelCache;
import com.jozufozu.flywheel.lib.model.ModelHolder;
import com.jozufozu.flywheel.lib.model.SimpleModel;
import com.jozufozu.flywheel.lib.model.SingleMeshModel;
import com.jozufozu.flywheel.lib.model.part.ModelPartConverter;
import com.jozufozu.flywheel.lib.visual.SimpleDynamicVisual;
import com.jozufozu.flywheel.lib.visual.SimpleEntityVisual;
import com.jozufozu.flywheel.lib.visual.SimpleTickableVisual;
import com.jozufozu.flywheel.lib.visual.components.FireComponent;
import com.jozufozu.flywheel.lib.visual.components.HitboxComponent;
import com.jozufozu.flywheel.lib.visual.components.ShadowComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

import java.util.EnumMap;
import java.util.function.Supplier;

public class RowboatVisual<T extends RowboatEntity> extends SimpleEntityVisual<T> implements SimpleTickableVisual, SimpleDynamicVisual {

    private final PoseStack poseStack = new PoseStack();
    public static final ModelHolder ROWBOAT_MODEL = createBodyModelHolder(RowboatEntityModel.LAYER_LOCATION);

    private static final ModelCache<Material> ROWBOAT_MODELS = new ModelCache<>(texture -> {
        return (createBodyModelHolder(RowboatEntityModel.LAYER_LOCATION).get());
    });

    protected final ResourceLocation rowboatTexture;
    protected final EnumMap<DyeColor, ResourceLocation> paintTextures;

    private static final BoatAtlasHolder ROWBOAT_ATLAS = BoatAtlases.getRowboatAtlas();
    public RowboatVisual(VisualizationContext context, T entity, final VanillaWood vanillaWood) {

        this(context, entity, new ResourceLocation(AlekiShips.MOD_ID,
                        "textures/entity/watercraft/rowboat/" + vanillaWood.getSerializedName()),
                AlekiShipsHelper.mapOfKeys(DyeColor.class, dyeColor -> new ResourceLocation(AlekiShips.MOD_ID,
                        "textures/entity/watercraft/rowboat/" + vanillaWood.getSerializedName() + "/" + dyeColor.getSerializedName())));
    }

    private TransformedInstance body;

    public RowboatVisual(VisualizationContext context, T entity, ResourceLocation rowboatTexture, final EnumMap<DyeColor, ResourceLocation> paintTextures) {
        super(context, entity);
        this.rowboatTexture = rowboatTexture;
        this.paintTextures = paintTextures;
    }

    private static ModelHolder createBodyModelHolder(ModelLayerLocation layer) {
        return new ModelHolder(() -> {
            return new SingleMeshModel(ModelPartConverter.convert(layer), SimpleMaterial.builder()
                    .cutout(CutoutShaders.ONE_TENTH)
                    .texture(ShipSheets.ROWBOAT_SHEET)
                    .mipmap(false)
                    .backfaceCulling(false)
                    .build()
            );
        });
    }

    @Override
    public void init(float partialTick){
        addComponent(new ShadowComponent(visualizationContext, entity).radius(1));
        addComponent(new FireComponent(visualizationContext, entity));
        addComponent(new HitboxComponent(visualizationContext,entity));

        Material texture;

        texture = (Material) ROWBOAT_ATLAS.getSprite(entity.getPaintColor().map(this.paintTextures::get).orElse(this.rowboatTexture));

        body = createBodyInstance();

        updateInstances(partialTick);
        updateLight();

        super.init(partialTick);
    }

    private TransformedInstance createBodyInstance() {
        return instancerProvider.instancer(InstanceTypes.TRANSFORMED, ROWBOAT_MODEL.get())
                .createInstance();
    }

    @Override
    public void beginFrame(VisualFrameContext context) {
        super.beginFrame(context);

        if (!isVisible(context.frustum())) {
            return;
        }

        updateInstances(context.partialTick());
    }

    private void updateInstances(float partialTick){
        poseStack.setIdentity();

        double posX = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double posY = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double posZ = Mth.lerp(partialTick, entity.zOld, entity.getZ());

        poseStack.translate(posX - renderOrigin.getX(), posY - renderOrigin.getY(), posZ - renderOrigin.getZ());

        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

        poseStack.translate(0, 0.4375D, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));

        poseStack.translate(0, 1.0625f, 0);
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));

        body.setTransform(poseStack)
                .setChanged();
    }

    public void updateLight() {
        relight(entity.blockPosition(), body);
    }

    @Override
    public void tick(VisualTickContext ctx) {

    }

    @Override
    protected void _delete() {
        body.delete();
    }
}
