package com.ldtteam.structurize.world.schematic;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.world.ModDimensions;
import com.mojang.datafixers.Dynamic;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class SchematicDimension extends OverworldDimension
{
    public SchematicDimension(final World worldIn, final DimensionType typeIn)
    {
        super(worldIn, typeIn);
        worldIn.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
        worldIn.getGameRules().get(GameRules.DO_MOB_SPAWNING).set(false, null);
        worldIn.getGameRules().get(GameRules.DO_WEATHER_CYCLE).set(false, null);
        worldIn.getGameRules().get(GameRules.DO_MOB_SPAWNING).set(false, null);
        worldIn.getGameRules().get(GameRules.MOB_GRIEFING).set(false, null);
        worldIn.getGameRules().get(GameRules.KEEP_INVENTORY).set(true, null);
        worldIn.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES).set(false, null);
        worldIn.getGameRules().get(GameRules.DISABLE_RAIDS).set(true, null);
    }

    @Override
    public boolean canRespawnHere()
    {
        return true;
    }

    @Override
    public ChunkGenerator<? extends GenerationSettings> createChunkGenerator()
    {
        final WorldType worldtype = world.getWorldInfo().getGenerator();
        if (worldtype != Structurize.getSchematicWorldType())
        {
            Structurize.getLogger().warn("Creating schematic dimension in non schematic world type! Things may break.");
        }
        final FlatGenerationSettings flatgenerationsettings =
            FlatGenerationSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, world.getWorldInfo().getGeneratorOptions()));
        final SingleBiomeProviderSettings singlebiomeprovidersettings1 = BiomeProviderType.FIXED.createSettings().setBiome(flatgenerationsettings.getBiome());
        return ChunkGeneratorType.FLAT.create(world, BiomeProviderType.FIXED.create(singlebiomeprovidersettings1), flatgenerationsettings);
    }

    @Override
    public boolean doesXZShowFog(final int x, final int z)
    {
        return false;
    }

    @Override
    public BlockPos findSpawn(final int posX, final int posZ, final boolean checkValid)
    {
        if (posX == 0 && posZ == 0)
        {
            return getSpawnPoint();
        }
        return null;
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return true;
    }

    @Override
    public boolean doesWaterVaporize()
    {
        return false;
    }

    @Override
    public BlockPos getSpawnCoordinate()
    {
        return getSpawnPoint();
    }

    @Override
    public boolean isNether()
    {
        return false;
    }

    @Override
    public void onWorldSave()
    {
        // TODO Auto-generated method stub, save entire data
        super.onWorldSave();
    }

    @Override
    public void tick()
    {
        // TODO Auto-generated method stub
        super.tick();
    }

    @Override
    public boolean canDoLightning(final Chunk chunk)
    {
        return false;
    }

    @Override
    public boolean canDoRainSnowIce(final Chunk chunk)
    {
        return false;
    }

    @Override
    public boolean canMineBlock(final PlayerEntity player, final BlockPos pos)
    {
        // TODO Auto-generated method stub, permission manager
        return super.canMineBlock(player, pos);
    }

    @Override
    public SleepResult canSleepAt(final PlayerEntity player, final BlockPos pos)
    {
        return SleepResult.ALLOW;
    }

    @Override
    public double getMovementFactor()
    {
        return 1.0d;
    }

    @Override
    public MusicType getMusicType()
    {
        return MusicType.CREATIVE;
    }

    @Override
    public DimensionType getRespawnDimension(final ServerPlayerEntity player)
    {
        return getType();
    }

    @Override
    public BlockPos getSpawnPoint()
    {
        return BlockPos.ZERO.up(100);
    }

    @Override
    public ICapabilityProvider initCapabilities()
    {
        // TODO Auto-generated method stub
        return super.initCapabilities();
    }

    @Override
    public boolean isHighHumidity(final BlockPos pos)
    {
        return false;
    }

    @Override
    public void setAllowedSpawnTypes(final boolean allowHostile, final boolean allowPeaceful)
    {
        // TODO Auto-generated method stub ???
        super.setAllowedSpawnTypes(allowHostile, allowPeaceful);
    }

    @Override
    public void setSpawnPoint(final BlockPos pos)
    {
        /*
         * We don't care about this
         */
    }

    @Override
    public boolean shouldMapSpin(final String entity, final double x, final double z, final double rotation)
    {
        return false;
    }
}
