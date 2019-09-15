package com.ldtteam.structurize.pipeline.build;

import java.util.List;
import com.ldtteam.structurize.Instances;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

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

    public boolean place(final T thing, final World world, final BlockPos pos)
    {
        return placer.place(thing, world, pos);
    }

    public List<ResourceLocation> getSubstitutions(final T thing, final World world, final BlockPos pos)
    {
        return substitutions.getSubstitutions(thing, world, pos);
    }

    public List<ItemStack> getRequirements(final T thing, final World world, final BlockPos pos)
    {
        return requirements.getRequirements(thing, world, pos);
    }

    @FunctionalInterface
    public interface Placer<T>
    {
        boolean place(T thing, World world, BlockPos pos);
    }

    @FunctionalInterface
    public interface Substitutions<T>
    {
        List<ResourceLocation> getSubstitutions(T thing, World world, BlockPos pos);
    }

    @FunctionalInterface
    public interface Requirements<T>
    {
        List<ItemStack> getRequirements(T thing, World world, BlockPos pos);
    }

    public abstract static class Builder<T>
    {
        private Placer<T> placer;
        private Substitutions<T> substitutions;
        private Requirements<T> requirements;
        private String registryName;

        public Builder<T> setPlacer(final Placer<T> placer)
        {
            this.placer = placer;
            return this;
        }

        public Builder<T> setSubstitutions(final Substitutions<T> substitutions)
        {
            this.substitutions = substitutions;
            return this;
        }

        public Builder<T> setRequirements(final Requirements<T> requirements)
        {
            this.requirements = requirements;
            return this;
        }

        public Builder<T> setRegistryName(final String registryName)
        {
            this.registryName = registryName;
            return this;
        }

        public Builder<T> setRegistryName(final ResourceLocation registryName)
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

        public abstract void buildAndRegister();
    }

    public static class BlockStateComponentPlacer extends ComponentPlacer<BlockState, BlockStateComponentPlacer>
    {
        private BlockStateComponentPlacer(final Builder<BlockState> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<BlockState> newBuilder()
        {
            return new ComponentPlacer.Builder<BlockState>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getBlockStatePlacerRegistry().register(new BlockStateComponentPlacer(this));
                }
            };
        }
    }

    public static class FluidStateComponentPlacer extends ComponentPlacer<IFluidState, FluidStateComponentPlacer>
    {
        private FluidStateComponentPlacer(final Builder<IFluidState> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<IFluidState> newBuilder()
        {
            return new ComponentPlacer.Builder<IFluidState>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getFluidStatePlacerRegistry().register(new FluidStateComponentPlacer(this));
                }
            };
        }
    }

    public static class TileEntityComponentPlacer extends ComponentPlacer<TileEntity, TileEntityComponentPlacer>
    {
        private TileEntityComponentPlacer(final Builder<TileEntity> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<TileEntity> newBuilder()
        {
            return new ComponentPlacer.Builder<TileEntity>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getTileEntityPlacerRegistry().register(new TileEntityComponentPlacer(this));
                }
            };
        }
    }

    public static class EntityComponentPlacer extends ComponentPlacer<Entity, EntityComponentPlacer>
    {
        private EntityComponentPlacer(final Builder<Entity> builder)
        {
            super(builder.getRegistryName(), builder.getPlacer(), builder.getSubstitutions(), builder.getRequirements());
        }

        public static ComponentPlacer.Builder<Entity> newBuilder()
        {
            return new ComponentPlacer.Builder<Entity>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getEntityPlacerRegistry().register(new EntityComponentPlacer(this));
                }
            };
        }
    }
}
