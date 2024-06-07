package com.alekiponi.alekiships.util;

import com.alekiponi.alekiships.common.entity.vehiclehelper.compartment.vanilla.crafting.CraftingTableCompartment;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.*;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommonHelper {
    @Nullable
    public static Entity getAnyEntityAtCrosshair(Entity entity, double range) {
        Vec3 from = entity.getEyePosition(1);
        Vec3 view = entity.getViewVector(1);
        Vec3 to = from.add(view.x * range, view.y * range, view.z * range);
        AABB aabb = entity.getBoundingBox().expandTowards(view.scale(range)).inflate(1, 1, 1);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(entity, from, to, aabb,
                e -> !e.isSpectator() && e.isPickable(), range * range);
        return hit != null && from.distanceTo(hit.getLocation()) < range ? hit.getEntity() : null;
    }

    public static double vec2ToWrappedDegrees(Vec2 vec2) {
        double x = vec2.normalized().x;
        double y = vec2.normalized().y;
        double direction = 0;
        if (y != 0 && x != 0) {
            direction = Math.round(Math.toDegrees(Math.atan(y / x)));
            //quadrant correction because probably I'm bad at math?
            if (x <= 0 && y <= 0) {
                direction += 90;
            } else if (x >= 0 && y >= 0) {
                direction -= 90;
            } else if (x <= 0 && y >= 0) {
                direction += 90;
            } else if (x >= 0 && y <= 0) {
                direction -= 90;
            }
        }

        return Mth.wrapDegrees(direction);
    }

    public static float sailForceMultiplierTable(float sailForceAngle) {
        sailForceAngle = (Math.abs(Mth.wrapDegrees(sailForceAngle)));
        float multiplier = 0;
        if (sailForceAngle < 15) {
            multiplier = 0f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 20) {
            multiplier = 8f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 25) {
            multiplier = 15f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 30) {
            multiplier = 20f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 45) {
            multiplier = 23f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 60) {
            multiplier = 27f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 75) {
            multiplier = 32f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 90) {
            multiplier = 35f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 105) {
            multiplier = 32f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 120) {
            multiplier = 30f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 135) {
            multiplier = 27f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 150) {
            multiplier = 20f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 165) {
            multiplier = 10f;
            return multiplier / 35f;
        }
        if (sailForceAngle < 180) {
            multiplier = 0f;
            return multiplier / 35f;
        }
        return multiplier / 35f;
    }




    public static boolean everyNthTickUnique(int id, int tickCount, int n){
        if((id + tickCount) % n == 0){
            return true;
        }
        return false;
    }


    /**
     * Copied from Forge to support multiloader
     */

    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    /**
     * Copied from Forge to support multiloader
     */

    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
        if (!stack.isEmpty()) {
            IItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
            Level level = player.level();
            ItemStack remainder = stack;
            if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
                remainder = inventory.insertItem(preferredSlot, stack, false);
            }

            if (!remainder.isEmpty()) {
                remainder = insertItemStacked(inventory, remainder, false);
            }

            if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
                level.playSound((Player)null, player.getX(), player.getY() + 0.5, player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            if (!remainder.isEmpty() && !level.isClientSide) {
                ItemEntity entityitem = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
                entityitem.setPickUpDelay(40);
                entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0.0, 1.0, 0.0));
                level.addFreshEntity(entityitem);
            }

        }
    }

    /**
     * Copied from Forge to support multiloader
     */
    public static @NotNull ItemStack insertItemStacked(IItemHandler inventory, @NotNull ItemStack stack, boolean simulate) {
        if (inventory != null && !stack.isEmpty()) {
            if (!stack.isStackable()) {
                return insertItem(inventory, stack, simulate);
            } else {
                int sizeInventory = inventory.getSlots();

                int i;
                for(i = 0; i < sizeInventory; ++i) {
                    ItemStack slot = inventory.getStackInSlot(i);
                    if (canItemStacksStackRelaxed(slot, stack)) {
                        stack = inventory.insertItem(i, stack, simulate);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }

                if (!stack.isEmpty()) {
                    for(i = 0; i < sizeInventory; ++i) {
                        if (inventory.getStackInSlot(i).isEmpty()) {
                            stack = inventory.insertItem(i, stack, simulate);
                            if (stack.isEmpty()) {
                                break;
                            }
                        }
                    }
                }

                return stack;
            }
        } else {
            return stack;
        }
    }

    /**
     * Copied from Forge to support multiloader
     */

    public static @NotNull ItemStack insertItem(IItemHandler dest, @NotNull ItemStack stack, boolean simulate) {
        if (dest != null && !stack.isEmpty()) {
            for(int i = 0; i < dest.getSlots(); ++i) {
                stack = dest.insertItem(i, stack, simulate);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

            return stack;
        } else {
            return stack;
        }
    }

    /**
     * Copied from Forge to support multiloader
     */
    public static boolean canItemStacksStackRelaxed(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (!a.isEmpty() && !b.isEmpty() && a.getItem() == b.getItem()) {
            if (!a.isStackable()) {
                return false;
            } else if (a.hasTag() != b.hasTag()) {
                return false;
            } else {
                return (!a.hasTag() || a.getTag().equals(b.getTag())) && a.areCapsCompatible(b);
            }
        } else {
            return false;
        }
    }

    /**
     * Creates a map of each enum constant to the value as provided by the value mapper.
     */
    public static <E extends Enum<E>, V> EnumMap<E, V> mapOfKeys(final Class<E> enumClass,
            final Function<E, V> valueMapper) {
        return mapOfKeys(enumClass, key -> true, valueMapper);
    }

    /**
     * Creates a map of each enum constant to the value as provided by the value mapper, only using enum constants that match the provided predicate.
     */
    public static <E extends Enum<E>, V> EnumMap<E, V> mapOfKeys(final Class<E> enumClass,
            final Predicate<E> keyPredicate, final Function<E, V> valueMapper) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(keyPredicate).collect(
                Collectors.toMap(Function.identity(), valueMapper, (v, v2) -> v, () -> new EnumMap<>(enumClass)));
    }

    /**
     * Safely grab the entity out of a nullable hit result.
     * <p>
     * Null always means there wasn't an {@link EntityHitResult} to get the entity from
     * <p>
     * Example usage:
     * <pre>
     * {@code
     * final Entity entity = AlekiShipsHelper.getEntity(Minecraft.getInstance().hitResult);
     * if (entity != null) {
     *     // Something that uses the entity
     * }
     * }
     * </pre>
     */
    @Nullable
    public static Entity getEntity(final @Nullable HitResult hitResult) {
        if (hitResult == null) return null;

        if (hitResult.getType() != HitResult.Type.ENTITY) return null;

        return ((EntityHitResult) hitResult).getEntity();
    }

    public static void dropContents(Level pLevel, double pX, double pY, double pZ, Container pInventory) {
        for(int i = 0; i < pInventory.getContainerSize(); ++i) {
            Containers.dropItemStack(pLevel, pX, pY, pZ, pInventory.getItem(i));
        }

    }

    /**
     * @param entity The entity which should be checked for collisions
     * @return The maximum height of the colliding {@link AABB}s or {@link Entity#getY()}
     */
    public static double maxHeightOfCollidableEntities(final Entity entity) {
        return maxHeightOfCollidableEntities(entity, entity.level(), entity.getBoundingBox(), entity.getY());
    }

    /**
     * @param entity      The entity
     * @param level       The level to check for entities
     * @param boundingBox The {@link AABB} to check for collisions
     * @param yPos        The starting Y position
     * @return The passed in yPos or {@link AABB#maxY} of the highest colliding {@link AABB}
     */
    public static double maxHeightOfCollidableEntities(@Nullable final Entity entity, final Level level,
            final AABB boundingBox, double yPos) {
        for (final Entity collidableEntity : level.getEntities(entity, boundingBox, Entity::canBeCollidedWith)) {
            final double maxY = collidableEntity.getBoundingBox().maxY;
            if (maxY > yPos) yPos = maxY;
        }
        return yPos;
    }

    /**
     * Creates a {@link ContainerLevelAccess} that'll pass valid {@link Level} and {@link BlockPos} objects
     * from the passed in entity.
     *
     * @param entity The entity the returned {@link ContainerLevelAccess} is bound to
     * @return {@link ContainerLevelAccess} with valid {@link Level} and {@link BlockPos} objects
     * @apiNote While this returns a valid {@link ContainerLevelAccess} most vanilla block menus such as {@link CraftingMenu}
     * check for a block in world to see if the menu should close. As such you will likely need to override at the very
     * least {@link AbstractContainerMenu#stillValid(Player)} however this can usually be done anonymously. For example
     * {@link CraftingTableCompartment} returns an anonymous {@link CraftingMenu} with {@link AbstractContainerMenu#stillValid(Player)}
     * overridden like so
     * <pre>{@code
     *      return new CraftingMenu(id, playerInventory, CommonHelper.createEntityContainerLevelAccess(this)) {
     *          @Override
     *          public boolean stillValid(final Player player) {
     *              return CraftingTableCompartment.this.stillValid(player);
     *          }
     *      };}</pre>
     */
    public static ContainerLevelAccess createEntityContainerLevelAccess(final Entity entity) {
        return new ContainerLevelAccess() {
            @Override
            public <T> Optional<T> evaluate(final BiFunction<Level, BlockPos, T> function) {
                return Optional.of(function.apply(entity.level(), entity.blockPosition()));
            }
        };
    }

    /**
     * Like {@link Container#stillValidBlockEntity(BlockEntity, Player)} but for entities
     *
     * @return If the passed entity is in range for a menu to be open
     */
    public static boolean stillValidEntity(final Entity entity, final Player player) {
        return stillValidEntity(entity, player, 8);
    }

    /**
     * Like {@link Container#stillValidBlockEntity(BlockEntity, Player, int)} but for entities
     *
     * @param maxDistance The max distance to the entity
     * @return If the passed entity is in range for a menu to be open
     */
    public static boolean stillValidEntity(final Entity entity, final Player player, final int maxDistance) {
        return !entity.isRemoved() && entity.position().closerThan(player.position(), maxDistance);
    }
}