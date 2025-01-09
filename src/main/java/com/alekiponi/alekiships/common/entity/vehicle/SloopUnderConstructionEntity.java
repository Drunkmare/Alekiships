package com.alekiponi.alekiships.common.entity.vehicle;

import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.SloopConstructionState;
import com.alekiponi.alekiships.network.AlekiShipsEntityDataSerializers;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SloopUnderConstructionEntity extends AbstractUnderConstructionEntity<SloopConstructionState.SloopConstructionStage, SloopConstructionState> {

    private static final EntityDataAccessor<SloopConstructionState> DATA_ID_CONSTRUCTION_STATE = SynchedEntityData.defineId(
            SloopUnderConstructionEntity.class, AlekiShipsEntityDataSerializers.SLOOP_CONSTRUCTION_STATE.get());

    public final BoatMaterial boatMaterial;

    public SloopUnderConstructionEntity(final EntityType<? extends SloopUnderConstructionEntity> entityType,
            final Level level, final BoatMaterial boatMaterial) {
        super(entityType, level, SloopConstructionState.SloopConstructionStage::getConstructionInput,
                SloopConstructionState.SloopConstructionStage.SIZE, SloopConstructionState.CODEC);
        this.boatMaterial = boatMaterial;
        this.setConstructionState(SloopConstructionState.getInitialState(this.registryAccess(),
                BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).withPath(boatMaterial.getSerializedName())));
    }

    @Override
    public int[] getColliderIndices() {
        return new int[0];
    }

    @Override
    public int[] getConstructionIndices() {
        return new int[]{0};
    }

    @Override
    public float renderSizeForCompartments() {
        return 0;
    }

    @Override
    public int getCompartmentRotation(int i) {
        return 0;
    }

    @Override
    public float getPassengerSizeLimit() {
        return 0;
    }

    @Override
    public int[][] getCompartmentRotationsArray() {
        return new int[0][];
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_CONSTRUCTION_STATE, SloopConstructionState.DEFAULT);
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_ID_CONSTRUCTION_STATE.equals(key)) {
            this.requiredItems.invalidate();
            this.constructionInput.invalidate();
        }
    }

    @Override
    public int[] getCompartmentIndices() {
        return new int[0];
    }

    @Override
    public boolean isFunctional() {
        return true;
    }

    protected Vec3 positionRiderByIndex(int index) {
        float localX = 0.0F;
        float localZ = 0.0F;
        float localY = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()));
        SloopConstructionState.SloopConstructionStage stage = this.getConstructionState().stage();
        switch (stage) {
            case KEEL -> {
                localX = 0.0f;
                localZ = 0.0f;
                localY += -0.0f;
            }
            case DECK -> {
                localX = -1.0f;
                localZ = 0.0f;
                localY += 0.5f;
            }
            case BOWSPRIT -> {
                localX = 4.0f;
                localZ = 0.0f;
                localY += 0.7f;
            }
            case MAST -> {
                localX = 2.0f;
                localZ = 0.0f;
                localY += 2.0f;
            }
            case BOOM -> {
                localX = 1.8f;
                localZ = 0.0f;
                localY += 2.25f;
            }
            case MAINSAIL -> {
                localX = 1.5f;
                localZ = 0.0f;
                localY += 2.5f;
            }
            case JIBSAIl -> {
                localX = 3.0f;
                localZ = 0.0f;
                localY += 0.5f;
            }
            case RAILINGS_STERN -> {
                localX = -3.0f;
                localZ = 0.0f;
                localY += 0.8f;
            }
            case RAILINGS_BOW -> {
                localX = 4.0f;
                localZ = 0.0f;
                localY += 0.8f;
            }
            case ANCHOR -> {
                localZ = -1.34f;
                localX = 2.52f;
                localY += 0.7f;
            }
            case RIGGING -> {
                localX = 1.5f;
                localZ = 0.0f;
                localY += 1.5f;
            }
        }
        return new Vec3(localX, localY, localZ);
    }

    @Override
    public float getDamageThreshold() {
        return 40;
    }

    @Override
    public float getDeathDamageThreshold() {
        return getDamageThreshold() * 1.25f;
    }

    @Override
    public float getDamageRecovery() {
        return 10;
    }

    @Override
    protected void switchConstructionStage(
            final ConstructionInput<SloopConstructionState.SloopConstructionStage> constructionInput,
            final SloopConstructionState.SloopConstructionStage previousStage,
            final SloopConstructionState.SloopConstructionStage currentStage) {
        final Direction thisDir = this.getDirection();
        final BlockPos thisPos = this.blockPosition().relative(thisDir, 4).relative(thisDir.getCounterClockWise(), 2);

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 7; y++) {
                this.playSound(constructionInput.getSwitchSound(previousStage));
                this.level().addDestroyBlockEffect(
                        thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x),
                        constructionInput.getSwitchBlockState(previousStage));
            }
        }
    }

    @Override
    protected void finalizeConstruction(final Player player,
            final ConstructionInput<SloopConstructionState.SloopConstructionStage> constructionInput) {
        final Direction thisDir = this.getDirection();
        final BlockPos thisPos = this.blockPosition().relative(thisDir, 4).relative(thisDir.getCounterClockWise(), 2);

        final var sloop = constructionInput.constructedEntityType.create(this.level());
        if (sloop != null) {
            sloop.setYRot(this.getYRot());
            sloop.setPos(this.position());
            this.level().addFreshEntity(sloop);
            if (player instanceof ServerPlayer serverPlayer) {
                AlekiShipsAdvancements.SLOOP_COMPLETED.trigger(serverPlayer);
            }
        }

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 7; y++) {
                for (int z = 0; z < 11; z++) {
                    this.playSound(constructionInput.assembleSound);
                    this.level().addDestroyBlockEffect(
                            thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x)
                                    .relative(Direction.UP, z), constructionInput.assembleBlockState);
                    this.level().addDestroyBlockEffect(
                            thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x)
                                    .relative(Direction.UP, z),
                            AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState());
                }
            }
        }
        this.kill();
    }

    @Override
    public SloopConstructionState getConstructionState() {
        return this.entityData.get(DATA_ID_CONSTRUCTION_STATE);
    }

    @Override
    public void setConstructionState(final SloopConstructionState constructionState) {
        this.entityData.set(DATA_ID_CONSTRUCTION_STATE, constructionState);
    }
}