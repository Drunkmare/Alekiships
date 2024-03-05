package com.alekiponi.alekiships.common.entity;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class CannonEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ID_CANNONBALL_ITEM = SynchedEntityData.defineId(CannonEntity.class,
            EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<ItemStack> DATA_ID_PAPER_ITEM = SynchedEntityData.defineId(CannonEntity.class,
            EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<ItemStack> DATA_ID_GUNPOWDER_ITEM = SynchedEntityData.defineId(CannonEntity.class,
            EntityDataSerializers.ITEM_STACK);

    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(
            CannonEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(
            CannonEntity.class, EntityDataSerializers.FLOAT);

    public static final byte EVENT_LIGHT = 10;

    public final Item cannonBallItem = AlekiShipsItems.CANNONBALL.get();

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    private int fuse = -1;

    public CannonEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick(){
        if (!this.isPassenger()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            if (this.isInWater()) {
                this.setDeltaMovement(0.0D, -0.01D, 0.0D);
                this.setYRot(this.getYRot() + 0.4f);
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

        if (this.fuse > 0) {
            --this.fuse;
            final Vec3 fuse = new Vec3((Mth.sin((float) (this.getYRot() * (Math.PI / 180))) * 0.5), 0.8,
                    Mth.cos((float) (-this.getYRot() * (Math.PI / 180))) * 0.5).multiply(-1, 1, -1)
                    .add(this.getPosition(0));
            final Vec3 deltaMovement = this.getRootVehicle().getDeltaMovement();
            this.level().addAlwaysVisibleParticle(ParticleTypes.FLAME, fuse.x, fuse.y, fuse.z, deltaMovement.x, 0.01,
                    deltaMovement.z);
            this.level().addAlwaysVisibleParticle(ParticleTypes.FLAME, fuse.x, fuse.y, fuse.z, deltaMovement.x, 0.01,
                    deltaMovement.z);
        } else if (this.fuse == 0) {
            this.fire();
        }
    }

    public boolean needsPaperItem(){
        return getPaperItem() != null;
    }

    public boolean needsGunpowderItem(){
        return getGunpowderItem() != null;
    }

    @Nullable
    public Item getPaperItem(){
        return null;
    }


    @Nullable
    public Item getGunpowderItem(){
        return null;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        final ItemStack item = player.getItemInHand(hand);
        if (item.is(this.cannonBallItem)) {
            if(this.getCannonball().isEmpty()){
                this.setCannonball(item.split(1));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        if(needsPaperItem()){
            if (item.is(this.getPaperItem())) {
                if(this.getPaper().isEmpty()){
                    this.setPaper(item.split(1));
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        if(needsGunpowderItem()){
            if (item.is(this.getGunpowderItem())) {
                if(this.getGunpowder().isEmpty()){
                    this.setGunpowder(item.split(1));
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }

        if (item.is(Items.FLINT_AND_STEEL)) {
            this.light();
            return InteractionResult.CONSUME;
        }
        if(player.isSecondaryUseActive() && this.getXRot() < 20){
            this.setXRot(this.getXRot()+1);
            return InteractionResult.SUCCESS;
        } else if (this.getXRot() > -20){
            this.setXRot(this.getXRot()-1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

    /**
     * Lights the cannon
     */
    public void light(){
        if(!this.getCannonball().is(AlekiShipsItems.CANNONBALL.get())){
            return;
        }
        if(needsPaperItem() && !this.getPaper().is(TFCItems.UNREFINED_PAPER.get())){
            return;
        }
        if(needsGunpowderItem() && !this.getGunpowder().is(Items.GUNPOWDER)){
            return;
        }
        if(this.isInWater()){
            return;
        }

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
    public void fire(){
        if(!this.getCannonball().is(AlekiShipsItems.CANNONBALL.get())){
            return;
        }
        if(this.needsPaperItem() && !this.getPaper().is(TFCItems.UNREFINED_PAPER.get())){
            return;
        }
        if(this.needsGunpowderItem() && !this.getGunpowder().is(Items.GUNPOWDER)){
            return;
        }
        this.fuse = -1;
        this.setPaper(ItemStack.EMPTY);
        this.setGunpowder(ItemStack.EMPTY);
        this.setCannonball(ItemStack.EMPTY);

        final CannonballEntity cannonball = AlekiShipsEntities.CANNONBALL_ENTITY.get()
                .create(this.level());
        assert cannonball != null;
        cannonball.setPos(this.getX(), this.getY(), this.getZ());

        cannonball.setDeltaMovement(
                Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * 3.0,
                Mth.sin(-this.getXRot() * ((float) Math.PI / 180F)) * 3.0,
                Mth.cos(-this.getYRot() * ((float) Math.PI / 180F)) * 3.0);

        if(this.isPassenger() && this.getVehicle() instanceof EmptyCompartmentEntity compartment){
            cannonball.setDeltaMovement(cannonball.getDeltaMovement().add(compartment.getRootVehicle().getDeltaMovement()));
        }

        //cannonball.setDeltaMovement(cannonball.getDeltaMovement().add(0,0.3,0));

        //TODO config for terrain damage
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5f, this.level().getRandom().nextFloat() * 0.05F + 0.01F);
        Random r = new Random();
        Vec3 particleMovement = cannonball.getDeltaMovement().multiply(0.3,0.3,0.3);
        for(int i = 0; i < 4; i ++){
            this.level().addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, this.getX() + particleMovement.x+(r.nextDouble()-0.5)*0.5, this.getY() + 0.5 + particleMovement.y+(r.nextDouble()-0.5)*0.5, this.getZ() + particleMovement.z+(r.nextDouble()-0.5)*0.5,
                    particleMovement.x*0.05, 0.05D, particleMovement.z*0.05);
        }


        Vec3 rayCastStep = cannonball.getDeltaMovement().multiply(0.33,0.33,0.33);
        Vec3 startPos = this.getPosition(0);

        for(int i = 0; i < 8; i ++){
            Vec3i currentPos = new Vec3i(Mth.floor(startPos.add(rayCastStep).x), Mth.floor(startPos.add(rayCastStep).y), Mth.floor((startPos.add(rayCastStep).z)));
            BlockPos blockPos = new BlockPos(currentPos);
            cannonball.setPos(startPos.add(rayCastStep));
            if(!this.level().getBlockState(blockPos).isAir() && blockPos != this.blockPosition() && this.level().getBlockState(blockPos).canBeReplaced(Fluids.WATER)){
                cannonball.discard();
                cannonball.explode(3);
                break;
            }

            rayCastStep = rayCastStep.add(rayCastStep);
        }

        cannonball.setPos(this.getPosition(0).add(cannonball.getDeltaMovement()).add(0,0.3,0));

        this.level().addFreshEntity(cannonball);
        Vec3 movement = new Vec3((Mth.sin(this.getYRot() * ((float) Math.PI / 180F)) * 0.04), 0,
                Mth.cos(-this.getYRot() * ((float) Math.PI / 180F)) * 0.04).multiply(-1,1,-1);
        this.setDeltaMovement(this.getDeltaMovement().add(movement));
    }

    @Override
    public void handleEntityEvent(final byte eventID) {
        if (eventID == EVENT_LIGHT) {
            this.light();
        } else {
            super.handleEntityEvent(eventID);
        }
    }

    @Override
    public boolean hurt(final DamageSource damageSource, final float amount) {
        if (this.isInvulnerableTo(damageSource)) return false;

        if (this.level().isClientSide || this.isRemoved()) return true;

        this.setHurtTime(10);
        this.setDamage(this.getDamage() + amount * 10);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        final boolean instantKill = damageSource.getEntity() instanceof Player && ((Player) damageSource.getEntity()).getAbilities().instabuild;

        if (instantKill) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtLocation(this.getDropItem());
            }
            this.discard();
        }
        if (this.getDamage() > 20) {
            this.destroy(damageSource);
            this.remove(RemovalReason.KILLED);
        }

        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if(pSource.is(DamageTypeTags.IS_EXPLOSION)){
            return true;
        }
        return super.isInvulnerableTo(pSource);
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

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    public ItemStack getCannonball(){
        return this.entityData.get(DATA_ID_CANNONBALL_ITEM);
    }

    public ItemStack getPaper(){
        return this.entityData.get(DATA_ID_PAPER_ITEM);
    }

    public ItemStack getGunpowder(){
        return this.entityData.get(DATA_ID_GUNPOWDER_ITEM);
    }

    protected void setCannonball(ItemStack itemStack){
        this.entityData.set(DATA_ID_CANNONBALL_ITEM, itemStack.copy());
    }

    protected void setPaper(ItemStack itemStack){
        this.entityData.set(DATA_ID_PAPER_ITEM, itemStack.copy());
    }

    protected void setGunpowder(ItemStack itemStack){
        this.entityData.set(DATA_ID_GUNPOWDER_ITEM, itemStack.copy());
    }

    private static final float DAMAGE_TO_BREAK = 8.0f;
    private static final float DAMAGE_RECOVERY = 0.5f;

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_DAMAGE, 0F);
        this.entityData.define(DATA_ID_PAPER_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_ID_CANNONBALL_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_ID_GUNPOWDER_ITEM, ItemStack.EMPTY);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(final float damageTaken) {
        this.entityData.set(DATA_ID_DAMAGE, damageTaken);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtTime(final int hurtTime) {
        this.entityData.set(DATA_ID_HURT, hurtTime);
    }

    public int getFuseTime(){
        return this.fuse;
    }

    protected void destroy(final DamageSource damageSource) {
        this.spawnAtLocation(this.getCannonball(), 1);
        this.spawnAtLocation(this.getPaper(), 1);
        this.spawnAtLocation(this.getGunpowder(), 1);
        this.spawnAtLocation(this.getDropItem(), 1);
    }

    public Item getDropItem() {
        return AlekiShipsItems.CANNON.get();
    }

    @Override
    public ItemStack getPickResult() {
        return getDropItem().getDefaultInstance();
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean canCollideWith(final net.minecraft.world.entity.Entity other) {
        return canVehicleCollide(this, other);
    }

    public static boolean canVehicleCollide(final net.minecraft.world.entity.Entity vehicle, final net.minecraft.world.entity.Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isPassenger();
    }

    @Override
    public boolean isPushable() {
        return true;
    }
}
