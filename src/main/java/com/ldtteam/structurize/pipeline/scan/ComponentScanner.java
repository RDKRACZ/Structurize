package com.ldtteam.structurize.pipeline.scan;

import com.ldtteam.structurize.Instances;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

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

    public abstract static class Builder<T>
    {
        private Scanner<T> scanner;
        private String registryName;

        public Builder<T> setScanner(final Scanner<T> scanner)
        {
            this.scanner = scanner;
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

        public Scanner<T> getScanner()
        {
            return scanner;
        }

        public String getRegistryName()
        {
            return registryName;
        }

        public abstract void buildAndRegister();
    }

    public static class BlockStateComponentScanner extends ComponentScanner<BlockState, BlockStateComponentScanner>
    {
        private BlockStateComponentScanner(final Builder<BlockState> builder)
        {
            super(builder.getRegistryName(), builder.getScanner());
        }

        public static ComponentScanner.Builder<BlockState> newBuilder()
        {
            return new ComponentScanner.Builder<BlockState>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getBlockStateScannerRegistry().register(new BlockStateComponentScanner(this));
                }
            };
        }
    }

    public static class TileEntityComponentScanner extends ComponentScanner<TileEntity, TileEntityComponentScanner>
    {
        private TileEntityComponentScanner(final Builder<TileEntity> builder)
        {
            super(builder.getRegistryName(), builder.getScanner());
        }

        public static ComponentScanner.Builder<TileEntity> newBuilder()
        {
            return new ComponentScanner.Builder<TileEntity>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getTileEntityScannerRegistry().register(new TileEntityComponentScanner(this));
                }
            };
        }
    }

    public static class EntityComponentScanner extends ComponentScanner<Entity, EntityComponentScanner>
    {
        private EntityComponentScanner(final Builder<Entity> builder)
        {
            super(builder.getRegistryName(), builder.getScanner());
        }

        public static ComponentScanner.Builder<Entity> newBuilder()
        {
            return new ComponentScanner.Builder<Entity>()
            {
                @Override
                public void buildAndRegister()
                {
                    Instances.getComponentRegistries().getEntityScannerRegistry().register(new EntityComponentScanner(this));
                }
            };
        }
    }
}
