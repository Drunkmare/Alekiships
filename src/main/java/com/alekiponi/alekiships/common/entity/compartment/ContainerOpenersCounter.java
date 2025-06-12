package com.alekiponi.alekiships.common.entity.compartment;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import lombok.Getter;

/**
 * Mostly a copy of vanillas {@link net.minecraft.world.level.block.entity.ContainerOpenersCounter} but for entities
 */
public abstract class ContainerOpenersCounter {
    private static final int CHECK_TICK_DELAY = 5;
    @Getter
    private int openCount;
    private double knownMaxInteractionRange;
    private int ticks;

    protected abstract void onOpen(Level level, final Vec3 pos);

    protected abstract void onClose(Level level, final Vec3 pos);

    protected abstract void openerCountChanged(Level level, int count, int openCount);

    protected abstract boolean isOwnContainer(Player player);

    public void incrementOpeners(final Player player, final Level level, final Vec3 pos) {
        final int openCount = this.openCount++;
        if (openCount == 0) {
            this.onOpen(level, pos);
            level.gameEvent(GameEvent.CONTAINER_OPEN, pos, GameEvent.Context.of(player));
        }

        this.openerCountChanged(level, openCount, this.openCount);
        this.knownMaxInteractionRange = Math.max(player.entityInteractionRange(), this.knownMaxInteractionRange);
    }

    public void decrementOpeners(final Player player, final Level level, final Vec3 pos) {
        final int openCount = this.openCount--;
        if (this.openCount == 0) {
            this.onClose(level, pos);
            level.gameEvent(GameEvent.CONTAINER_CLOSE, pos, GameEvent.Context.of(player));
            this.knownMaxInteractionRange = 0;
        }

        this.openerCountChanged(level, openCount, this.openCount);
    }

    public void recheckOpeners(final Level level, final Vec3 pos) {
        if (this.ticks++ % CHECK_TICK_DELAY == 0) return;

        final var playersWithContainerOpen = this.getPlayersWithContainerOpen(level, pos);
        this.knownMaxInteractionRange = 0;

        for (final var player : playersWithContainerOpen) {
            this.knownMaxInteractionRange = Math.max(player.blockInteractionRange(), this.knownMaxInteractionRange);
        }

        final int playerCount = playersWithContainerOpen.size();
        final int openCount = this.openCount;
        if (openCount != playerCount) {
            if (playerCount == 0) {
                this.onClose(level, pos);
                level.gameEvent(null, GameEvent.CONTAINER_CLOSE, pos);
            } else if (openCount == 0) {
                this.onOpen(level, pos);
                level.gameEvent(null, GameEvent.CONTAINER_OPEN, pos);
            }

            this.openCount = playerCount;
        }

        this.openerCountChanged(level, openCount, playerCount);
    }

    protected final List<Player> getPlayersWithContainerOpen(final Level level, final Vec3 pos) {
        return level.getEntities(EntityTypeTest.forClass(Player.class),
                new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1).inflate(
                        this.knownMaxInteractionRange + 4), this::isOwnContainer);
    }
}