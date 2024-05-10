package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveBlockOnlyCompartments;
import com.alekiponi.alekiships.common.entity.vehiclecapability.IHaveCleats;
import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.AbstractCompartmentEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.network.CustomEntityDataSerializers;
import com.alekiponi.alekiships.util.BoatMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.IntFunction;

public class RowboatEntity extends AbstractAlekiBoatEntity implements IHaveBlockOnlyCompartments, IHaveCleats {
    private static final EntityDataAccessor<Byte> DATA_ID_OARS = SynchedEntityData.defineId(RowboatEntity.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<DyeColor>> DATA_ID_PAINT_COLOR = SynchedEntityData.defineId(RowboatEntity.class,
            CustomEntityDataSerializers.OPTIONAL_DYE_COLOR);
    public final int PASSENGER_NUMBER = 6;

    public final int[] CLEATS = {5};

    public final int[][] COMPARTMENT_ROTATIONS = {{0, 180}};

    public final int[] CAN_ADD_ONLY_BLOCKS = {2, 1};

    public final int[] COMPARTMENTS = {0, 1, 2, 3, 4};
    protected final float PASSENGER_SIZE_LIMIT = 1.4F;

    protected final float DAMAGE_THRESHOLD = 128.0f;
    protected final float DAMAGE_RECOVERY = 5.333f;
    private final BoatMaterial boatMaterial;


    public RowboatEntity(final EntityType<? extends RowboatEntity> entityType, final Level level, EntityDimensions extent,
                         final BoatMaterial boatMaterial) {
        super(entityType, level, extent);
        this.boatMaterial = boatMaterial;
    }

    @Override
    public float getPassengerSizeLimit() {
        return PASSENGER_SIZE_LIMIT;
    }

    @Override
    public int getMaxPassengers() {
        return this.PASSENGER_NUMBER;
    }

    @Override
    public int[] getCleatIndices() {
        return this.CLEATS;
    }

    @Override
    public int[] getColliderIndices() {
        return new int[0];
    }

    @Override
    public int[] getCompartmentIndices() {
        return this.COMPARTMENTS;
    }

    @Override
    public int[] getCanAddOnlyBlocksIndices() {
        return CAN_ADD_ONLY_BLOCKS;
    }

    public AbstractCompartmentEntity.RidingPose[] getRidingPoses() {
        AbstractCompartmentEntity.RidingPose[] poses = new AbstractCompartmentEntity.RidingPose[this.getMaxPassengers()];
        for (int i = 0; i < this.getMaxPassengers(); i++) {
            poses[i] = AbstractCompartmentEntity.RidingPose.COMPACT;
        }
        poses[0] = AbstractCompartmentEntity.RidingPose.STANDARD;
        return poses;
    }

    protected Vec3 positionRiderByIndex(int index) {
        float localX = 0.0F;
        float localZ = 0.0F;
        float localY = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()));
        switch (index) {
            case 0 -> {
                // front / pilot seat
                localX = 1.1f;
                localZ = 0.0f;
                localY += 0.2f;
            }
            case 1 -> {
                // back right seat
                localX = -0.95f;
                localZ = 0.375f;
                localY += 0.1f;
            }
            case 2 -> {
                // back left seat
                localX = -0.95f;
                localZ = -0.375f;
                localY += 0.1f;
            }
            case 3 -> {
                // middle right seat
                localX = -0.1f;
                localZ = 0.375f;
                localY += 0.1f;
            }
            case 4 -> {
                // middle left seat
                localX = -0.1f;
                localZ = -0.375f;
                localY += 0.1f;
            }
            case 5 -> {
                // cleat
                localX = 1.720f;
                localZ = 0f;
                localY += 0.937f;
            }
        }
        return new Vec3(localX, localY, localZ);
    }

    @Override
    public float getDamageThreshold() {
        return DAMAGE_THRESHOLD;
    }

    @Override
    public float getDamageRecovery() {
        return DAMAGE_RECOVERY;
    }

    @Override
    public void remove(final RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason.shouldDestroy()) {
            this.playSound(SoundEvents.WOOD_BREAK, 1.0F, this.level().getRandom().nextFloat() * 0.1F + 0.9F);
        }

        super.remove(removalReason);
    }

    @Override
    protected float getPaddleMultiplier() {
        float paddleMultiplier = 1.0f;
        if (this.getOars() != Oars.ZERO) {
            paddleMultiplier = 1.6f;
        }
        return paddleMultiplier;
    }

    @Override
    protected float getTurnSpeed() {
        return 0.7f;
    }

    @Override
    protected float getMomentumSubtractor() {
        return 0.005f;
    }

    @Override
    public int getCompartmentRotation(int i) {
        return COMPARTMENT_ROTATIONS[i][0];
    }

    @Override
    public int[][] getCompartmentRotationsArray() {
        return COMPARTMENT_ROTATIONS;
    }

    @Override
    public Item getDropItem() {
        return this.boatMaterial.getDeckItem();
    }

    @Override
    protected void dropCustomDestructionLoot(final DamageSource damageSource) {
        super.dropCustomDestructionLoot(damageSource);
        switch (this.getOars()) {
            case ZERO:
                break;
            case TWO:
                this.spawnAtLocation(AlekiShipsItems.OAR.get());
            case ONE:
                this.spawnAtLocation(AlekiShipsItems.OAR.get());
                break;
        }
    }

    @Nullable
    @Override
    public Entity getPilotVehiclePartAsEntity() {
        if (this.isVehicle() && this.getPassengers().size() == this.getMaxPassengers()) {
            return this.getPassengers().get(0);
        }
        return null;
    }

    public Oars getOars() {
        return Oars.byId(this.entityData.get(DATA_ID_OARS));
    }

    public void setOars(final Oars oars) {
        this.entityData.set(DATA_ID_OARS, (byte) oars.ordinal());
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.is(Tags.Items.DYES)) {
            final DyeColor dyeColor = DyeColor.getColor(heldItem);
            if (dyeColor != null) {
                final Optional<DyeColor> paintColor = this.getPaintColor();
                if (paintColor.isEmpty() || paintColor.get() != dyeColor) {
                    this.setPaintColor(dyeColor);
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (heldItem.is(Items.WATER_BUCKET)) {
            this.clearPaint();
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }

        if (heldItem.is(AlekiShipsItems.OAR.get()) && this.getOars() != Oars.TWO) {
            this.addOar();
            heldItem.split(1);
            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    public void addOar() {
        switch (this.getOars()) {
            case ZERO -> this.setOars(Oars.ONE);
            case ONE -> this.setOars(Oars.TWO);
            case TWO -> {
                // Intentionally empty
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_OARS, (byte) Oars.ZERO.getId());
        this.entityData.define(DATA_ID_PAINT_COLOR, Optional.empty());
    }

    /**
     * @return The paint color of the boat. {@code null} for no color
     */
    public Optional<DyeColor> getPaintColor() {
        return this.entityData.get(DATA_ID_PAINT_COLOR);
    }

    /**
     * @param paintColor A {@link DyeColor}
     */
    public void setPaintColor(final DyeColor paintColor) {
        this.entityData.set(DATA_ID_PAINT_COLOR, Optional.of(paintColor));
    }

    public void clearPaint() {
        this.entityData.set(DATA_ID_PAINT_COLOR, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setOars(Oars.byId(compoundTag.getByte("oars")));

        if (compoundTag.contains("paint", CompoundTag.TAG_BYTE)) {
            this.setPaintColor(DyeColor.byId(compoundTag.getByte("paint")));
        }
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putByte("oars", (byte) this.getOars().getId());

        this.getPaintColor().ifPresent(dyeColor -> compoundTag.putByte("paint", (byte) dyeColor.getId()));
    }

    @Override
    public float getStepHeight() {
        return 0.0f;
    }

    public enum Oars {
        ZERO(0),
        ONE(1),
        TWO(2);

        private static final IntFunction<Oars> BY_ID = ByIdMap.continuous(Oars::getId, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
        private final int id;

        Oars(final int id) {
            this.id = id;
        }

        public static Oars byId(final int id) {
            return BY_ID.apply(id);
        }

        public int getId() {
            return id;
        }
    }
}