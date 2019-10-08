package com.ldtteam.structurize.pipeline.build;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class ComponentPlacer<T, U extends ForgeRegistryEntry<U>> extends ForgeRegistryEntry<U>
{
    private final Placer<T> placer;
    private final Substitutions<T> substitutions;
    private final Requirements<T> requirements;

    private ComponentPlacer(final String registryName, final Placer<T> placer, final Substitutions<T> substitutions, final Requirements<T> requirements)
    {
        this.placer = placer;
        this.substitutions = substitutions;
        this.requirements = requirements;
        super.setRegistryName(registryName);
    }

    public boolean place(final T thing, final World world, final BlockPos pos, final boolean triggerPlayerActions)
    {
        return placer.place(thing, world, pos, triggerPlayerActions);
    }

    public List<ResourceLocation> getSubstitutions(final T thing, final World world, final BlockPos pos)
    {
        return substitutions.getSubstitutions(thing, world, pos);
    }

    public List<ItemStack> getRequirements(final T thing, final World world, final BlockPos pos, final boolean triggerPlayerActions)
    {
        return requirements.getRequirements(thing, world, pos, triggerPlayerActions);
    }

    @FunctionalInterface
    public interface Placer<T>
    {
        boolean place(T thing, World world, BlockPos pos, boolean triggerPlayerActions);
    }

    @FunctionalInterface
    public interface Substitutions<T>
    {
        List<ResourceLocation> getSubstitutions(T thing, World world, BlockPos pos);
    }

    @FunctionalInterface
    public interface Requirements<T>
    {
        List<ItemStack> getRequirements(T thing, World world, BlockPos pos, boolean triggerPlayerActions);
    }

    public abstract static class Builder<T, U extends ComponentPlacer<T, U>>
    {
        private Placer<T> placer;
        private Substitutions<T> substitutions;
        private Requirements<T> requirements;
        private String registryName;

        public Builder<T, U> setPlacer(final Placer<T> placer)
        {
            this.placer = placer;
            return this;
        }

        public Builder<T, U> setSubstitutions(final Substitutions<T> substitutions)
        {
            this.substitutions = substitutions;
            return this;
        }

        public Builder<T, U> setRequirements(final Requirements<T> requirements)
        {
            this.requirements = requirements;
            return this;
        }

        public Builder<T, U> setRegistryName(final String registryName)
        {
            this.registryName = registryName;
            return this;
        }

        public Builder<T, U> setRegistryName(final ResourceLocation registryName)
        {
            this.registryName = registryName.toString();
            return this;
        }

        public Placer<T> getPlacer()
        {
            return placer;
        }

        public Substitutions<T> getSubstitutions()
        {
            return substitutions;
        }

        public Requirements<T> getRequirements()
        {
            return requirements;
        }

        public String getRegistryName()
        {
            return registryName;
        }

        public abstract void buildAndRegister(final IForgeRegistry<U> registry);
    }

    public static class BlockStateComponentPlacer extends ComponentPlacer<BlockState, BlockStateComponentPlacer>
    {
        private BlockStateComponentPlacer(final Builder<BlockState, BlockStateComponentPlacer> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<BlockState, BlockStateComponentPlacer> newBuilder()
        {
            return new ComponentPlacer.Builder<BlockState, BlockStateComponentPlacer>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<BlockStateComponentPlacer> registry)
                {
                    registry.register(new BlockStateComponentPlacer(this));
                }
            };
        }
    }

    public static class FluidStateComponentPlacer extends ComponentPlacer<IFluidState, FluidStateComponentPlacer>
    {
        private FluidStateComponentPlacer(final Builder<IFluidState, FluidStateComponentPlacer> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<IFluidState, FluidStateComponentPlacer> newBuilder()
        {
            return new ComponentPlacer.Builder<IFluidState, FluidStateComponentPlacer>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<FluidStateComponentPlacer> registry)
                {
                    registry.register(new FluidStateComponentPlacer(this));
                }
            };
        }
    }

    public static class TileEntityComponentPlacer extends ComponentPlacer<TileEntity, TileEntityComponentPlacer>
    {
        private TileEntityComponentPlacer(final Builder<TileEntity, TileEntityComponentPlacer> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<TileEntity, TileEntityComponentPlacer> newBuilder()
        {
            return new ComponentPlacer.Builder<TileEntity, TileEntityComponentPlacer>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<TileEntityComponentPlacer> registry)
                {
                    registry.register(new TileEntityComponentPlacer(this));
                }
            };
        }
    }

    public static class EntityComponentPlacer extends ComponentPlacer<Entity, EntityComponentPlacer>
    {
        private EntityComponentPlacer(final Builder<Entity, EntityComponentPlacer> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<Entity, EntityComponentPlacer> newBuilder()
        {
            return new ComponentPlacer.Builder<Entity, EntityComponentPlacer>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<EntityComponentPlacer> registry)
                {
                    registry.register(new EntityComponentPlacer(this));
                }
            };
        }
    }
}
