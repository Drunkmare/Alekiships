package com.alekiponi.alekiships.common;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.wind.Wind;
import com.alekiponi.alekiships.wind.WindModel;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class AlekiShipsAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, AlekiShips.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WindModel>> WIND_MODEL = ATTACHMENT_TYPES.register(
            "wind_model", () -> AttachmentType.builder(() -> (WindModel) (x, y, z) -> Wind.ZERO).build());
}