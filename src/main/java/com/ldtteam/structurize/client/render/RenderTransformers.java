package com.ldtteam.structurize.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

/**
 * Set of transformers that are applied before rendering.
 */
public class RenderTransformers
{
    private static Map<Predicate<BlockState>, Function<BlockState, BlockState>> blockStateTransformers = new HashMap<>();
    private static Map<Predicate<CompoundNBT>, Function<CompoundNBT, CompoundNBT>> tileEntityTransformers = new HashMap<>();
    private static Map<Predicate<CompoundNBT>, Function<CompoundNBT, CompoundNBT>> entityTransformers = new HashMap<>();

    /**
     * Private constructor to hide implicit public one.
     */
    private RenderTransformers()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Adds blockstate transformer.
     *
     * @param transformPredicate predicate to check if this transformer needs to be applied.
     * @param transformHandler   transformer
     */
    public static void addBlockStateTransformer(
        @NotNull final Predicate<BlockState> transformPredicate,
        @NotNull final Function<BlockState, BlockState> transformHandler)
    {
        blockStateTransformers.put(transformPredicate, transformHandler);
    }

    /**
     * Adds tile entity transformer.
     *
     * @param transformPredicate predicate to check if this transformer needs to be applied.
     * @param transformHandler   transformer
     */
    public static void addTileEntityTransformer(
        @NotNull final Predicate<CompoundNBT> transformPredicate,
        @NotNull final Function<CompoundNBT, CompoundNBT> transformHandler)
    {
        tileEntityTransformers.put(transformPredicate, transformHandler);
    }

    /**
     * Adds entity transformer.
     *
     * @param transformPredicate predicate to check if this transformer needs to be applied.
     * @param transformHandler   transformer
     */
    public static void addEntityTransformer(
        @NotNull final Predicate<CompoundNBT> transformPredicate,
        @NotNull final Function<CompoundNBT, CompoundNBT> transformHandler)
    {
        entityTransformers.put(transformPredicate, transformHandler);
    }

    /**
     * Process blockState. Checks transformers and applies first one found.
     *
     * @param blockState blockState to transform
     * @return transformed blockState
     */
    public static BlockState transformBlockState(@NotNull final BlockState blockState)
    {
        return blockStateTransformers.keySet()
            .stream()
            .filter(p -> p.test(blockState))
            .findFirst()
            .map(p -> blockStateTransformers.get(p))
            .orElse(Function.identity())
            .apply(blockState);
    }

    /**
     * Process tileEntity. Checks transformers and applies first one found.
     *
     * @param tileEntity tileEntity to transform
     * @return transformed tileEntity
     */
    public static CompoundNBT transformTileEntity(@NotNull final CompoundNBT tileEntity)
    {
        return tileEntityTransformers.keySet()
            .stream()
            .filter(p -> p.test(tileEntity))
            .findFirst()
            .map(p -> tileEntityTransformers.get(p))
            .orElse(Function.identity())
            .apply(tileEntity);
    }

    /**
     * Process entity. Checks transformers and applies first one found.
     *
     * @param entity entity to transform
     * @return transformed entity
     */
    public static CompoundNBT transformEntity(@NotNull final CompoundNBT entity)
    {
        return entityTransformers.keySet()
            .stream()
            .filter(p -> p.test(entity))
            .findFirst()
            .map(p -> entityTransformers.get(p))
            .orElse(Function.identity())
            .apply(entity);
    }
}