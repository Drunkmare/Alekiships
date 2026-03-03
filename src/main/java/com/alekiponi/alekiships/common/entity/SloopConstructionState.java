package com.alekiponi.alekiships.common.entity;

import com.mojang.serialization.Codec;

import com.alekiponi.alekiships.common.entity.vehicle.ConstructionInput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public record SloopConstructionState(SloopConstructionStage stage,
        int remainingInputs) implements ConstructionInput.ConstructionState<SloopConstructionState.SloopConstructionStage, SloopConstructionState> {

    public static final Codec<SloopConstructionState> CODEC = ConstructionInput.ConstructionState.codec(
            SloopConstructionStage.ENUM_CODEC, SloopConstructionStage.KEEL, SloopConstructionState::new);

    public static final StreamCodec<FriendlyByteBuf, SloopConstructionState> STREAM_CODEC = ConstructionInput.ConstructionState.streamCodec(
            SloopConstructionStage.class, SloopConstructionState::new);

    public static final SloopConstructionState DEFAULT = new SloopConstructionState(SloopConstructionStage.DEFAULT, 0);

    @Override
    public SloopConstructionState withRemaining(final int remaining) {
        return new SloopConstructionState(this.stage, remaining);
    }

    @Override
    public SloopConstructionState nextStateOf(final ConstructionInput<SloopConstructionStage> constructionInput) {
        final var nextStage = this.stage.next();
        return new SloopConstructionState(nextStage,
                nextStage != nextStage.end() ? constructionInput.getIngredient(nextStage).count() : -1);
    }

    public enum SloopConstructionStage implements ConstructionInput.ConstructionStage<SloopConstructionStage> {
        KEEL("keel"),
        DECK("deck"),
        BOWSPRIT("bowsprit"),
        MAST("mast"),
        BOOM("boom"),
        MAINSAIL("mainsail"),
        JIBSAIl("jibsail"),
        RAILINGS_STERN("stern_railings"),
        RAILINGS_BOW("bow_railings"),
        ANCHOR("anchor"),
        RIGGING("rigging"),
        COMPLETE("complete");

        public static final SloopConstructionStage DEFAULT = KEEL;

        public static final int SIZE = SloopConstructionState.SloopConstructionStage.values().length - 1;

        public static final StringRepresentableCodec<SloopConstructionStage> ENUM_CODEC = StringRepresentable.fromEnum(
                SloopConstructionStage::values);

        public static final Codec<ConstructionInput<SloopConstructionStage>> INPUT_CODEC = ConstructionInput.codec(
                ENUM_CODEC, SloopConstructionStage::values);

        public static final StreamCodec<RegistryFriendlyByteBuf, ConstructionInput<SloopConstructionStage>> INPUT_STREAM_CODEC = ConstructionInput.streamCodec(
                SloopConstructionStage.class);

        final String name;

        SloopConstructionStage(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public SloopConstructionStage next() {
            return switch (this) {
                case KEEL -> DECK;
                case DECK -> BOWSPRIT;
                case BOWSPRIT -> MAST;
                case MAST -> BOOM;
                case BOOM -> MAINSAIL;
                case MAINSAIL -> JIBSAIl;
                case JIBSAIl -> RAILINGS_STERN;
                case RAILINGS_STERN -> RAILINGS_BOW;
                case RAILINGS_BOW -> ANCHOR;
                case ANCHOR -> RIGGING;
                case RIGGING -> COMPLETE;
                case COMPLETE -> throw new UnsupportedOperationException("Complete is the final construction stage");
            };
        }

        @Override
        public SloopConstructionStage end() {
            return COMPLETE;
        }
    }
}