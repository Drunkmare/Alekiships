package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class CannonEntity extends Entity {
    public static final byte EVENT_LIGHT = 10;
    public static final String FUSE_KEY = "Fuse";
    public static final String CANNONBALL_KEY = "Cannonball";
    public static final String DAMAGE_KEY = "Damage";
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(CannonEntity.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ItemStack> DATA_ID_CANNONBALL_ITEM = SynchedEntityData.defineId(
            CannonEntity.class, EntityDataSerializers.ITEM_STACK);
    /**
     * Vanilla only syncs rotation continuously for arrows.
     * As the cannons rotation must be accurate we have to override
     * {@link #setXRot(float)} and {@link #getXRot()} to use this synced data instead
     */
    private static final EntityDataAccessor<Float> DATA_ID_X_ROT = SynchedEntityData.defineId(CannonEntity.class,
            EntityDataSerializers.FLOAT);
    private static final ItemStack CANNONBALL = new ItemStack(AlekiShipsItems.CANNONBALL.get());
    private static final float DAMAGE_TO_BREAK = 8;
    private static final float DAMAGE_RECOVERY = 0.5F;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    private int recentlyFired;
    private int fuse = -1;
    @Nullable
    private LivingEntity igniter;

    public CannonEntity(final EntityType<? extends CannonEntity> entityType, final Level level) {
        super(entityType, level);
        fuse = -1;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_DAMAGE, 0F);
        this.entityData.define(DATA_ID_CANNONBALL_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_ID_X_ROT, 0F);
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {
        this.fuse = compoundTag.getInt(FUSE_KEY);
        this.setCannonball(ItemStack.of(compoundTag.getCompound(CANNONBALL_KEY)));
        this.setDamage(compoundTag.getFloat(DAMAGE_KEY));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putInt(FUSE_KEY, this.fuse);
        compoundTag.put(CANNONBALL_KEY, this.getCannonball().save(new CompoundTag()));
        compoundTag.putFloat(DAMAGE_KEY, this.getDamage());
    }

    @Override
    public void tick() {
        if(recentlyFired > 0){
            recentlyFired--;
        }
        if (tickCount <= 1){
            fuse = -1;
        }
        if (!this.isPassenger()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            if (this.isInWater()) {
                this.setDeltaMovement(0.0D, -0.01D, 0.0D);
                if (!this.onGround()) {
                    this.setYRot(this.getYRot() + 0.4f);
                }
            }
            if (!this.onGround() || this.getDeltaMovement()
                    .horizontalDistanceSqr() > (double) 1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
                this.checkInsideBlocks();
                this.move(MoverType.SELF, this.getDeltaMovement());
                float f1 = 0.8F;

                this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.98D, f1));
                if (this.onGround()) {
                    Vec3 vec31 = this.getDeltaMovement();
                    if (vec31.y < 0.0D) {
                        this.setDeltaMovement(vec31.multiply(1.0D, -0.5D, 1.0D));
                    }
                }
            }
            this.updateInWaterStateAndDoFluidPushing();
        }
        tickLerp();
        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - DAMAGE_RECOVERY);
        }


        if (this.fuse > 0) {
            --this.fuse;
            final Vec3 fuse = new Vec3((Mth.sin((float) (-this.getYRot() * (Math.PI / 180))) * 0.5), 0.8,
                    Mth.cos((float) (this.getYRot() * (Math.PI / 180))) * 0.5).multiply(-1, 1, -1)
                    .add(this.getPosition(0));
            final Vec3 deltaMovement = this.getRootVehicle().getDeltaMovement();
            this.level().addAlwaysVisibleParticle(ParticleTypes.FLAME, fuse.x, fuse.y, fuse.z, deltaMovement.x, 0.01,
                    deltaMovement.z);
            this.level().addAlwaysVisibleParticle(ParticleTypes.FLAME, fuse.x, fuse.y, fuse.z, deltaMovement.x, 0.01,
                    deltaMovement.z);
        } else if (this.fuse == 0) {
            this.fire();
            recentlyFired = 60;
        }
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final ItemStack heldItem = player.getItemInHand(hand);

        final InteractionResult insertResult = this.insertItem(heldItem);

        if (insertResult.consumesAction()) return insertResult;

        if (this.isLoaded() && heldItem.is(Items.FLINT_AND_STEEL)) {
            // Already lit
            if (this.isLit()) return InteractionResult.FAIL;

            this.light(player);
            player.swing(hand);
            heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            return InteractionResult.SUCCESS;
        } else if (heldItem.is(Items.FLINT_AND_STEEL)) {
            return InteractionResult.FAIL;
        }

        if (player.isSecondaryUseActive() && this.getXRot() < 20) {
            this.setXRot(this.getXRot() + 1);
            return InteractionResult.SUCCESS;
        } else if (this.getXRot() > -20) {
            this.setXRot(this.getXRot() - 1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * Called to insert an item into the cannon. We only insert cannonballs in vanilla
     *
     * @param itemStack The item stack to insert
     * @return The result of the interaction. If {@link InteractionResult#consumesAction()} is
     * true no further processing is attempted
     */
    protected InteractionResult insertItem(final ItemStack itemStack) {
        if (itemStack.is(AlekiShipsItems.CANNONBALL.get())) {
            if (this.getCannonball().isEmpty()) {
                this.setCannonball(itemStack.split(1));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
    /**
     * Lights the cannon
     */
    public void light(@Nullable final LivingEntity igniter) {
        if (this.isInWater()) {
            return;
        }

        this.igniter = igniter;

        this.fuse = 40;
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, EVENT_LIGHT);
            if (!this.isSilent()) {
                this.playSound(SoundEvents.TNT_PRIMED, 1.5f, this.level().getRandom().nextFloat() * 0.05F + 0.91F);
            }
        }
    }

    /**
     * Fires the cannon once the fuse is out. This should also clear whatever contents are necessary
     */
    public void fire() {

        this.fuse = -1;
        this.setCannonball(ItemStack.EMPTY);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5f, this.level().getRandom().nextFloat() * 0.05F + 0.01F);

        final CannonballEntity cannonball = new CannonballEntity(this);

        cannonball.setPos(this.position());
        this.level().addFreshEntity(cannonball);
        final Vec3 movement = new Vec3((Mth.sin(-this.getYRot() * ((float) Math.PI / 180F)) * 0.04), this.getDeltaMovement().y,
                Mth.cos(this.getYRot() * ((float) Math.PI / 180F)) * 0.04).multiply(-1, 1, -1);
        this.setDeltaMovement(this.getDeltaMovement().add(movement));
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if (this.isInvulnerableTo(damageSource)) return false;

        if (this.level().isClientSide || this.isRemoved()) return true;

        this.setDamage(this.getDamage() + amount * 10);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        final boolean instantKill = damageSource.getEntity() instanceof Player player && player.getAbilities().instabuild;

        if (instantKill) {
            this.kill();
        } else if (this.getDamage() > DAMAGE_TO_BREAK) {
            this.destroy(damageSource);
            this.remove(RemovalReason.KILLED);
        }


        return true;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    protected void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            //this.setRot(this.getYRot(), this.getXRot());
        }
    }

    @Override
    public void lerpTo(final double posX, final double posY, final double posZ, final float yaw, final float pitch,
                       final int pPosRotationIncrements, final boolean teleport) {
        this.lerpX = posX;
        this.lerpY = posY;
        this.lerpZ = posZ;
        this.lerpYRot = yaw;
        this.lerpXRot = pitch;
        this.lerpSteps = 10;
    }

    protected void destroy(@SuppressWarnings("unused") final DamageSource damageSource) {
        this.spawnAtLocation(this.getCannonball(), 1);
        this.spawnAtLocation(new ItemStack(this.getDropItem()), 1);
    }

    @Override
    public void handleEntityEvent(final byte eventID) {
        if (eventID == EVENT_LIGHT) {
            this.light(null);
        } else {
            super.handleEntityEvent(eventID);
        }
    }

    public ItemStack getCannonball() {
        return this.entityData.get(DATA_ID_CANNONBALL_ITEM);
    }

    protected void setCannonball(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_CANNONBALL_ITEM, itemStack.copy());
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(final float damageTaken) {
        this.entityData.set(DATA_ID_DAMAGE, damageTaken);
    }

    @SuppressWarnings("unused")
    public int getFuseTime() {
        return this.fuse;
    }

    public boolean recentlyFired(){
        return recentlyFired > 0;
    }

    public Item getDropItem() {
        return AlekiShipsItems.CANNON.get();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isPassenger();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    /**
     * @return The next required ItemStack to load the cannon.
     * @apiNote The returned stack must not be modified and is expected to be used only in rendering
     */
    public ItemStack nextRequiredItem() {
        return CANNONBALL;
    }

    /**
     * Vanilla only syncs rotation continuously for arrows.
     * As the cannons rotation must be accurate we have to override this
     */
    @Override
    public float getXRot() {
        return this.entityData.get(DATA_ID_X_ROT);
    }

    /**
     * Vanilla only syncs rotation continuously for arrows.
     * As the cannons rotation must be accurate we have to override this
     */
    @Override
    public void setXRot(final float xRot) {
        this.entityData.set(DATA_ID_X_ROT, xRot);
    }

    public boolean isLoaded() {
        return !this.getCannonball().isEmpty();
    }

    public boolean isLit() {
        return this.fuse > -1;
    }
}