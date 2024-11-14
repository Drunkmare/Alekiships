package com.alekiponi.alekiships.util.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class GenericTrigger extends SimpleCriterionTrigger<GenericTrigger.TriggerInstance> {

    public static final Codec<GenericTrigger.TriggerInstance> CODEC = EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf(
            "player").xmap(TriggerInstance::new, TriggerInstance::player).codec();

    @Override
    public Codec<TriggerInstance> codec() {
        return CODEC;
    }

    public void trigger(final ServerPlayer player) {
        trigger(player, instance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
    }
}