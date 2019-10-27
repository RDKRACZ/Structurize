package com.ldtteam.structurize.pipeline.scan;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class ComponentScanner<T, U extends ForgeRegistryEntry<U>> extends ForgeRegistryEntry<U>
{
    private final Scanner<T> scanner;

    private ComponentScanner(final String registryName, final Scanner<T> scanner)
    {
        this.scanner = scanner;
        super.setRegistryName(registryName);
    }

    public T scan(final T thing, final World world, final BlockPos pos)
    {
        return scanner.scan(thing, world, pos);
    }

    @FunctionalInterface
    public interface Scanner<T>
    {
        T scan(T thing, World world, BlockPos pos);
    }

    public abstract static class Builder<T, U extends ComponentScanner<T, U>>
    {
        private Scanner<T> scanner;
        private String registryName;

        public Builder<T, U> setScanner(final Scanner<T> scanner)
        {
            this.scanner = scanner;
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

        public Scanner<T> getScanner()
        {
            return scanner;
        }

        public String getRegistryName()
        {
            return registryName;
        }

        public abstract void buildAndRegister(final IForgeRegistry<U> registry);
    }

    public static class BlockStateComponentScanner extends ComponentScanner<BlockState, BlockStateComponentScanner>
    {
        private BlockStateComponentScanner(final Builder<BlockState, BlockStateComponentScanner> builder)
        {
            super(builder.getRegistryName(), builder.getScanner());
        }

        public static ComponentScanner.Builder<BlockState, BlockStateComponentScanner> newBuilder()
        {
            return new ComponentScanner.Builder<BlockState, BlockStateComponentScanner>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<BlockStateComponentScanner> registry)
                {
                    registry.register(new BlockStateComponentScanner(this));
                }
            };
        }
    }

    public static class TileEntityComponentScanner extends ComponentScanner<TileEntity, TileEntityComponentScanner>
    {
        private TileEntityComponentScanner(final Builder<TileEntity, TileEntityComponentScanner> builder)
        {
            super(builder.getRegistryName(), builder.getScanner());
        }

        public static ComponentScanner.Builder<TileEntity, TileEntityComponentScanner> newBuilder()
        {
            return new ComponentScanner.Builder<TileEntity, TileEntityComponentScanner>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<TileEntityComponentScanner> registry)
                {
                    registry.register(new TileEntityComponentScanner(this));
                }
            };
        }
    }

    public static class EntityComponentScanner extends ComponentScanner<Entity, EntityComponentScanner>
    {
        private EntityComponentScanner(final Builder<Entity, EntityComponentScanner> builder)
        {
            super(builder.getRegistryName(), builder.getScanner());
        }

        public static ComponentScanner.Builder<Entity, EntityComponentScanner> newBuilder()
        {
            return new ComponentScanner.Builder<Entity, EntityComponentScanner>()
            {
                @Override
                public void buildAndRegister(IForgeRegistry<EntityComponentScanner> registry)
                {
                    registry.register(new EntityComponentScanner(this));
                }
            };
        }
    }
}
