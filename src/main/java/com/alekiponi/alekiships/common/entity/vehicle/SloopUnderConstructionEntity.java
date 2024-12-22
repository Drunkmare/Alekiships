package com.alekiponi.alekiships.common.entity.vehicle;

import java.util.Arrays;
import java.util.Comparator;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.item.AlekiShipsItems;
import com.alekiponi.alekiships.util.BoatMaterial;
import com.alekiponi.alekiships.util.advancements.AlekiShipsAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

public class SloopUnderConstructionEntity extends AbstractUnderConstructionEntity {

    public final BoatMaterial boatMaterial;

    private static final String KEEL_KEY = "keel";
    private static final String DECK_KEY = "deck";
    private static final String BOWSPRIT_KEY = "bowsprit";
    private static final String MAST_KEY = "mast";
    private static final String BOOM_KEY = "boom";
    private static final String MAINSAIL_KEY = "mailsail";
    private static final String JIBSAIL_KEY = "jibsail";
    private static final String RAILING_BOW_KEY = "railingBow";
    private static final String RAILING_STERN_KEY = "railingStern";
    private static final String ANCHOR_KEY = "anchor";
    private static final String RIGGING_KEY = "rigging";
    private static final String STAGE_KEY = "stage";

    private static final int KEEL_ITEM_NUMBER = 8;

    private static final int DECK_ITEM_NUMBER = 20;

    private static final int BOWSPRIT_ITEM_NUMBER = 6;

    private static final int MAST_ITEM_NUMBER = 12;

    private static final int BOOM_ITEM_NUMER = 8;

    private static final int MAINSAIL_ITEM_NUMBER = 16;

    private static final int JIBSAIL_ITEM_NUMBER = 8;

    private static final int STERN_RAILING_ITEM_NUMBER = 8;

    private static final int BOW_RAILING_ITEM_NUMBER = 8;

    private static final int ANCHOR_ITEM_NUMBER = 1;

    private static final int RIGGING_ITEM_NUMBER = 8;

    public SloopUnderConstructionEntity(final EntityType<? extends SloopUnderConstructionEntity> entityType,
                                        final Level level, final BoatMaterial boatMaterial) {
        super(entityType, level);
        this.boatMaterial = boatMaterial;
    }

    public Pair<Item, Integer> getBowRailingsItem() {
        return new Pair<>(this.boatMaterial.getRailing(), BOW_RAILING_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getSternRailingsItem() {
        return new Pair<>(this.boatMaterial.getRailing(), STERN_RAILING_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getMainsailItem() {
        return new Pair<>(Items.WHITE_WOOL, MAINSAIL_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getJibsailItem() {
        return new Pair<>(Items.WHITE_WOOL, JIBSAIL_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getRiggingItem() {
        return new Pair<>(Items.LEAD, RIGGING_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getAnchorItem() {
        return new Pair<>(AlekiShipsItems.ANCHOR.get(), ANCHOR_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getDeckItem() {
        return new Pair<>(this.boatMaterial.getDeckItem(), DECK_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getMastItem() {
        return new Pair<>(this.boatMaterial.getStrippedLog(), MAST_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getKeelItem() {
        return new Pair<>(this.boatMaterial.getStrippedLog(), KEEL_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getBowspritItem() {
        return new Pair<>(this.boatMaterial.getStrippedLog(), BOWSPRIT_ITEM_NUMBER);
    }

    public Pair<Item, Integer> getBoomItem() {
        return new Pair<>(this.boatMaterial.getStrippedLog(), BOOM_ITEM_NUMER);
    }

    private static final EntityDataAccessor<ItemStack> DATA_ID_KEEL = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_DECK = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_BOWSPRIT = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_MAST = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_BOOM = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_MAINSAIL = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_JIBSAIL = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_RAILINGS_STERN = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_RAILINGS_BOW = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_ANCHOR = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_ID_RIGGING = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Integer> DATA_ID_CONSTRUCTION_STAGE = SynchedEntityData.defineId(SloopUnderConstructionEntity.class,
            EntityDataSerializers.INT);

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
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_KEEL, ItemStack.EMPTY);
        builder.define(DATA_ID_DECK, ItemStack.EMPTY);
        builder.define(DATA_ID_BOWSPRIT, ItemStack.EMPTY);
        builder.define(DATA_ID_MAST, ItemStack.EMPTY);
        builder.define(DATA_ID_BOOM, ItemStack.EMPTY);
        builder.define(DATA_ID_MAINSAIL, ItemStack.EMPTY);
        builder.define(DATA_ID_JIBSAIL, ItemStack.EMPTY);
        builder.define(DATA_ID_RAILINGS_STERN, ItemStack.EMPTY);
        builder.define(DATA_ID_RAILINGS_BOW, ItemStack.EMPTY);
        builder.define(DATA_ID_ANCHOR, ItemStack.EMPTY);
        builder.define(DATA_ID_RIGGING, ItemStack.EMPTY);
        builder.define(DATA_ID_CONSTRUCTION_STAGE, 0);
    }

    public ItemStack getKeel() {
        return this.entityData.get(DATA_ID_KEEL);
    }

    public void setKeel(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_KEEL, itemStack.copy());
    }

    public ItemStack getDeck() {
        return this.entityData.get(DATA_ID_DECK);
    }

    public void setDeck(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_DECK, itemStack.copy());
    }

    public ItemStack getBowsprit() {
        return this.entityData.get(DATA_ID_BOWSPRIT);
    }

    public void setBowsprit(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_BOWSPRIT, itemStack.copy());
    }

    public ItemStack getMast() {
        return this.entityData.get(DATA_ID_MAST);
    }

    public void setMast(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_MAST, itemStack.copy());
    }

    public ItemStack getBoom() {
        return this.entityData.get(DATA_ID_BOOM);
    }

    public void setBoom(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_BOOM, itemStack.copy());
    }

    public ItemStack getMainsail() {
        return this.entityData.get(DATA_ID_MAINSAIL);
    }

    public void setMainsail(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_MAINSAIL, itemStack.copy());
    }

    public ItemStack getJibsail() {
        return this.entityData.get(DATA_ID_JIBSAIL);
    }

    public void setJibsail(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_JIBSAIL, itemStack.copy());
    }

    public ItemStack getRailingsStern() {
        return this.entityData.get(DATA_ID_RAILINGS_STERN);
    }

    public void setRailingsStern(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_RAILINGS_STERN, itemStack.copy());
    }

    public ItemStack getRailingsBow() {
        return this.entityData.get(DATA_ID_RAILINGS_BOW);
    }

    public void setRailingsBow(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_RAILINGS_BOW, itemStack.copy());
    }

    public ItemStack getAnchor() {
        return this.entityData.get(DATA_ID_ANCHOR);
    }

    public void setAnchor(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_ANCHOR, itemStack.copy());
    }

    public ItemStack getRigging() {
        return this.entityData.get(DATA_ID_RIGGING);
    }

    public void setRigging(final ItemStack itemStack) {
        this.entityData.set(DATA_ID_RIGGING, itemStack.copy());
    }

    public ConstructionState getConstructionStage() {
        return ConstructionState.getByOrdinal(this.entityData.get(DATA_ID_CONSTRUCTION_STAGE));
    }

    public void setConstructionStage(ConstructionState stage) {
        this.entityData.set(DATA_ID_CONSTRUCTION_STAGE, stage.ordinal());
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {

        RegistryAccess access = this.registryAccess();

        this.setKeel(ItemStack.parseOptional(access, compoundTag.getCompound(KEEL_KEY)));
        this.setBowsprit(ItemStack.parseOptional(access, compoundTag.getCompound(BOWSPRIT_KEY)));
        this.setMast(ItemStack.parseOptional(access, compoundTag.getCompound(MAST_KEY)));
        this.setBoom(ItemStack.parseOptional(access, compoundTag.getCompound(BOOM_KEY)));
        this.setMainsail(ItemStack.parseOptional(access, compoundTag.getCompound(MAINSAIL_KEY)));
        this.setJibsail(ItemStack.parseOptional(access, compoundTag.getCompound(JIBSAIL_KEY)));
        this.setRailingsBow(ItemStack.parseOptional(access, compoundTag.getCompound(RAILING_BOW_KEY)));
        this.setRailingsStern(ItemStack.parseOptional(access, compoundTag.getCompound(RAILING_STERN_KEY)));
        this.setAnchor(ItemStack.parseOptional(access, compoundTag.getCompound(ANCHOR_KEY)));
        this.setRigging(ItemStack.parseOptional(access, compoundTag.getCompound(RIGGING_KEY)));
        this.setConstructionStage(ConstructionState.getByOrdinal(compoundTag.getInt(STAGE_KEY)));
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {

        RegistryAccess access = this.registryAccess();

        compoundTag.put(KEEL_KEY, this.getKeel().save(access));
        compoundTag.put(BOWSPRIT_KEY, this.getBowsprit().save(access));
        compoundTag.put(MAST_KEY, this.getMast().save(access));
        compoundTag.put(BOOM_KEY, this.getBoom().save(access));
        compoundTag.put(MAINSAIL_KEY, this.getMainsail().save(access));
        compoundTag.put(JIBSAIL_KEY, this.getJibsail().save(access));
        compoundTag.put(RAILING_BOW_KEY, this.getRailingsBow().save(access));
        compoundTag.put(RAILING_STERN_KEY, this.getRailingsStern().save(access));
        compoundTag.put(ANCHOR_KEY, this.getAnchor().save(access));
        compoundTag.put(RIGGING_KEY, this.getRigging().save(access));
        compoundTag.putInt(STAGE_KEY, this.getConstructionStage().ordinal());
    }

    @Override
    public int[] getCompartmentIndices() {
        return new int[0];
    }

    @Override
    public boolean isFunctional(){
        return true;
    }

    public static enum ConstructionState {
        KEEL,
        DECK,
        BOWSPRIT,
        MAST,
        BOOM,
        MAINSAIL,
        JIBSAIl,
        RAILINGS_STERN,
        RAILINGS_BOW,
        ANCHOR,
        RIGGING,
        COMPLETE;

        private static final ConstructionState[] BY_ORDINAL = Arrays.stream(values())
                .sorted(Comparator.comparingInt(ConstructionState::ordinal)).toArray(ConstructionState[]::new);

        public static ConstructionState getByOrdinal(int ordinal) {
            return BY_ORDINAL[ordinal % BY_ORDINAL.length];
        }
    }

    protected Vec3 positionRiderByIndex(int index) {
        float localX = 0.0F;
        float localZ = 0.0F;
        float localY = (float) ((this.isRemoved() ? (double) 0.01F : this.getPassengersRidingOffset()));
        ConstructionState stage = this.getConstructionStage();
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
        return getDamageThreshold()*1.25f;
    }

    @Override
    public float getDamageRecovery() {
        return 10;
    }

    public Item getCurrentRequiredItem() {
        ConstructionState stage = this.getConstructionStage();
        switch (stage) {
            case KEEL, MAST, BOOM, BOWSPRIT -> {
                return this.getBoomItem().getA();
            }
            case DECK -> {
                return this.getDeckItem().getA();
            }
            case MAINSAIL -> {
                return this.getMainsailItem().getA();
            }
            case JIBSAIl -> {
                return this.getJibsailItem().getA();
            }
            case RAILINGS_STERN -> {
                return this.getSternRailingsItem().getA();
            }
            case RAILINGS_BOW -> {
                return this.getBowRailingsItem().getA();
            }
            case ANCHOR -> {
                return this.getAnchorItem().getA();
            }
            case RIGGING -> {
                return this.getRiggingItem().getA();
            }
        }
        return null;
    }

    public int getNumberItemsLeft() {
        ConstructionState stage = this.getConstructionStage();
        switch (stage) {
            case KEEL -> {
                return getKeelItem().getB() - getKeel().getCount();
            }
            case DECK -> {
                return getDeckItem().getB() - getDeck().getCount();
            }
            case BOWSPRIT -> {
                return getBowspritItem().getB() - getBowsprit().getCount();
            }
            case MAST -> {
                return getMastItem().getB() - getMast().getCount();
            }
            case BOOM -> {
                return getBoomItem().getB() - getBoom().getCount();
            }
            case MAINSAIL -> {
                return getMainsailItem().getB() - getMainsail().getCount();
            }
            case JIBSAIl -> {
                return getJibsailItem().getB() - getJibsail().getCount();
            }
            case RAILINGS_STERN -> {
                return getSternRailingsItem().getB() - getRailingsStern().getCount();
            }
            case RAILINGS_BOW -> {
                return getBowRailingsItem().getB() - getRailingsBow().getCount();
            }
            case ANCHOR -> {
                return getAnchorItem().getB() - getAnchor().getCount();
            }
            case RIGGING -> {
                return getRiggingItem().getB() - getRigging().getCount();
            }
        }
        return -1;
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public void interactFromConstructionEntity(Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        ConstructionState stage = this.getConstructionStage();
        BlockPos thisPos = this.blockPosition();
        Direction thisDir = this.getDirection();
        thisPos = thisPos.relative(thisDir, 4);
        thisPos = thisPos.relative(thisDir.getCounterClockWise(), 2);
        switch (stage) {
            case KEEL -> {
                if (stack.is(getKeelItem().getA())) {
                    this.setKeel(new ItemStack(stack.split(1).getItem(), this.getKeel().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getKeel().getCount() >= getKeelItem().getB()) {
                        this.setConstructionStage(ConstructionState.DECK);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case DECK -> {
                if (stack.is(getDeckItem().getA())) {
                    this.setDeck(new ItemStack(stack.split(1).getItem(), this.getDeck().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getDeck().getCount() >= getDeckItem().getB()) {
                        this.setConstructionStage(ConstructionState.BOWSPRIT);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case BOWSPRIT -> {
                if (stack.is(getBowspritItem().getA())) {
                    this.setBowsprit(new ItemStack(stack.split(1).getItem(), this.getBowsprit().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getBowsprit().getCount() >= getBowspritItem().getB()) {
                        this.setConstructionStage(ConstructionState.MAST);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case MAST -> {
                if (stack.is(getMastItem().getA())) {
                    this.setMast(new ItemStack(stack.split(1).getItem(), this.getMast().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getMast().getCount() >= getMastItem().getB()) {
                        this.setConstructionStage(ConstructionState.BOOM);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case BOOM -> {
                if (stack.is(getBoomItem().getA())) {
                    this.setBoom(new ItemStack(stack.split(1).getItem(), this.getBoom().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getBoom().getCount() >= getBoomItem().getB()) {
                        this.setConstructionStage(ConstructionState.MAINSAIL);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case MAINSAIL -> {
                if (stack.is(getMainsailItem().getA())) {
                    this.setMainsail(new ItemStack(stack.split(1).getItem(), this.getMainsail().getCount() + 1));
                    this.playSound(SoundEvents.WOOL_PLACE);
                    if (this.getMainsail().getCount() >= getMainsailItem().getB()) {
                        this.setConstructionStage(ConstructionState.JIBSAIl);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOL_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), Blocks.WHITE_WOOL.defaultBlockState());
                            }
                        }
                    }
                }
            }
            case JIBSAIl -> {
                if (stack.is(getJibsailItem().getA())) {
                    this.setJibsail(new ItemStack(stack.split(1).getItem(), this.getJibsail().getCount() + 1));
                    this.playSound(SoundEvents.WOOL_PLACE);
                    if (this.getJibsail().getCount() >= getJibsailItem().getB()) {
                        this.setConstructionStage(ConstructionState.RAILINGS_STERN);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOL_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), Blocks.WHITE_WOOL.defaultBlockState());

                            }
                        }
                    }
                }
            }
            case RAILINGS_STERN -> {
                if (stack.is(getSternRailingsItem().getA())) {
                    this.setRailingsStern(new ItemStack(stack.split(1).getItem(), this.getRailingsStern().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getRailingsStern().getCount() >= getSternRailingsItem().getB()) {
                        this.setConstructionStage(ConstructionState.RAILINGS_BOW);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case RAILINGS_BOW -> {
                if (stack.is(getBowRailingsItem().getA())) {
                    this.setRailingsBow(new ItemStack(stack.split(1).getItem(), this.getRailingsBow().getCount() + 1));
                    this.playSound(SoundEvents.WOOD_PLACE);
                    if (this.getRailingsBow().getCount() >= getBowRailingsItem().getB()) {
                        this.setConstructionStage(ConstructionState.ANCHOR);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case ANCHOR -> {
                if (stack.is(getAnchorItem().getA())) {
                    this.setAnchor(new ItemStack(stack.split(1).getItem(), this.getAnchor().getCount() + 1));
                    this.playSound(SoundEvents.METAL_PLACE);
                    if (this.getAnchor().getCount() >= getAnchorItem().getB()) {
                        this.setConstructionStage(ConstructionState.RIGGING);
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                this.playSound(SoundEvents.WOOD_BREAK);
                                this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x), this.boatMaterial.getDeckBlock());
                            }
                        }
                    }
                }
            }
            case RIGGING -> {
                if (stack.is(getRiggingItem().getA())) {
                    this.setRigging(new ItemStack(stack.split(1).getItem(), this.getRigging().getCount() + 1));
                    this.playSound(SoundEvents.LEASH_KNOT_PLACE);
                    if (this.getRigging().getCount() >= getRiggingItem().getB()) {
                        this.boatMaterial.getEntityType(BoatMaterial.BoatType.SLOOP).ifPresent(entityType -> {
                            final AbstractVehicle sloop = entityType.create(this.level());
                            if (sloop != null) {
                                sloop.setYRot(this.getYRot());
                                sloop.setPos(this.getPosition(0));
                                this.level().addFreshEntity(sloop);
                                if (player instanceof ServerPlayer serverPlayer) {
                                    AlekiShipsAdvancements.SLOOP_COMPLETED.trigger(serverPlayer);
                                }

                            }
                        });

                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 7; y++) {
                                for (int z = 0; z < 11; z++) {
                                    this.playSound(SoundEvents.WOOD_BREAK);
                                    this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x).relative(Direction.UP, z), this.boatMaterial.getDeckBlock());
                                    this.level().addDestroyBlockEffect(thisPos.relative(thisDir.getOpposite(), y).relative(thisDir.getClockWise(), x).relative(Direction.UP, z), AlekiShipsBlocks.BOAT_FRAME_ANGLED.get().defaultBlockState());
                                }
                            }
                        }
                        this.kill();
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getDamage() > this.getDamageThreshold()) {
            this.kill();
            this.spawnAtLocation(this.getKeel());
            this.spawnAtLocation(this.getDeck());
            this.spawnAtLocation(this.getBowsprit());
            this.spawnAtLocation(this.getMast());
            this.spawnAtLocation(this.getBoom());
            this.spawnAtLocation(this.getMainsail());
            this.spawnAtLocation(this.getJibsail());
            this.spawnAtLocation(this.getAnchor());
            this.spawnAtLocation(this.getRigging());
        }

    }
}
