package com.alekiponi.alekiships.util.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GenericTrigger extends SimpleCriterionTrigger<GenericTrigger.TriggerInstance> {
    private final ResourceLocation id;

    // This is patterned after, read: copied from, TFC... could use a bit of restructure probably
    public GenericTrigger(ResourceLocation id) {
        this.id = id;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    @Override
    protected GenericTrigger.TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext context) {
        return new GenericTrigger.TriggerInstance(predicate);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ContextAwarePredicate predicate) {
            super(id, predicate);
        }
    }
}