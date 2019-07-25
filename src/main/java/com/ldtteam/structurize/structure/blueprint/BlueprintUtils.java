package com.ldtteam.structurize.structure.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.ldtteam.structurize.structure.StructureBB;
import org.apache.logging.log4j.LogManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import static com.ldtteam.structurize.util.constants.MathConstants.CHUNK_BLOCK_SIZE;

/**
 * @see <a href="http://dark-roleplay.net/other/blueprint_format.php">Blueprint V1 Specification</a>
 * @since 0.1.0
 *        State: not completed
 */
public class BlueprintUtils
{
    /**
     * Private constructor to hide implicit public one.
     */
    private BlueprintUtils()
    {
        /*
         * Intentionally left empty
         */
    }

    /**
     * Generates a Blueprint objects from the world.
     *
     * @param world The World that is used for the Blueprint
     * @param start The Position of the Blueprint
     * @param end   The End Position of the Blueprint
     * @return the generated Blueprint
     */
    public static Blueprint createBlueprint(final World world, final BlockPos start, final BlockPos end)
    {
        return createBlueprint(world, start, end, null, new ArrayList<>());
    }

    /**
     * Generates a Blueprint objects from the world.
     *
     * @param world      The World that is used for the Blueprint
     * @param start      The Position of the Blueprint
     * @param end        The End Position of the Blueprint
     * @param name       a Name for the Structure
     * @param architects an Array of Architects for the structure
     * @return the generated Blueprint
     */
    public static Blueprint createBlueprint(final World world, final BlockPos start, final BlockPos end, final String name, final List<String> architects)
    {
        final List<BlockState> pallete = new ArrayList<>();
        // Always add AIR to Pallete
        pallete.add(Blocks.AIR.getDefaultState());
        final StructureBB structBB = new StructureBB(start, end);
        final short[][][] structure = new short[structBB.getYSize()][structBB.getZSize()][structBB.getXSize()];

        final List<CompoundNBT> tileEntities = new ArrayList<>();

        final List<String> requiredMods = new ArrayList<>();

        for (final BlockPos pos : structBB.getPosIterator())
        {
            final BlockState state = world.getBlockState(pos);
            final String modName = state.getBlock().getRegistryName().getNamespace();

            final BlockPos posInStruct = pos.subtract(structBB.getAnchor());

            if (!requiredMods.contains(modName))
            {
                if (ModList.get().isLoaded(modName))
                {
                    requiredMods.add(modName);
                }
            }
            else if (!ModList.get().isLoaded(modName))
            {
                structure[posInStruct.getY()][posInStruct.getZ()][posInStruct.getX()] = (short) pallete.indexOf(Blocks.AIR.getDefaultState());
                continue;
            }

            final TileEntity te = world.getTileEntity(pos);
            if (te != null)
            {
                final CompoundNBT teTag = te.serializeNBT();
                teTag.putShort("x", (short) posInStruct.getX());
                teTag.putShort("y", (short) posInStruct.getY());
                teTag.putShort("z", (short) posInStruct.getZ());
                tileEntities.add(teTag);
            }
            if (!pallete.contains(state))
            {
                pallete.add(state);
            }
            structure[posInStruct.getY()][posInStruct.getZ()][posInStruct.getX()] = (short) pallete.indexOf(state);
        }

        final CompoundNBT[] tes = tileEntities.toArray(new CompoundNBT[0]);

        final List<CompoundNBT> entitiesTag = new ArrayList<>();

        final List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, structBB.toAABB());

        for (final Entity entity : entities)
        {
            final Vec3d oldPos = entity.getPositionVector();
            final CompoundNBT entityTag = entity.serializeNBT();
            final ListNBT posList = new ListNBT();
            posList.add(new DoubleNBT(oldPos.x - structBB.getAnchor().getX()));
            posList.add(new DoubleNBT(oldPos.y - structBB.getAnchor().getY()));
            posList.add(new DoubleNBT(oldPos.z - structBB.getAnchor().getZ()));

            BlockPos entityPos = entity.getPosition();
            if (entity instanceof HangingEntity)
            {
                entityPos = ((HangingEntity) entity).getHangingPosition();
            }
            entityTag.put("Pos", posList);
            entityTag.put("TileX", new IntNBT(entityPos.getX() - structBB.getAnchor().getX()));
            entityTag.put("TileY", new IntNBT(entityPos.getY() - structBB.getAnchor().getY()));
            entityTag.put("TileZ", new IntNBT(entityPos.getZ() - structBB.getAnchor().getZ()));
            entitiesTag.add(entityTag);
        }

        final Blueprint schem = new Blueprint(structBB, pallete, structure, tes, requiredMods);
        schem.setEntities(entitiesTag);

        if (name != null)
        {
            schem.setName(name);
        }

        schem.setArchitects(architects == null ? new ArrayList<>() : architects);

        return schem;
    }

    /**
     * Serializes a given Blueprint to an CompoundNBT.
     *
     * @param schem The Blueprint to serialize
     * @return An CompoundNBT containing the Blueprint Data
     */
    public static CompoundNBT writeBlueprintToNBT(final Blueprint schem)
    {
        final CompoundNBT tag = new CompoundNBT();
        // Set Blueprint Version
        tag.putByte("version", (byte) 1);
        // Set Blueprint Size
        tag.putShort("size_x", schem.getSizeX());
        tag.putShort("size_y", schem.getSizeY());
        tag.putShort("size_z", schem.getSizeZ());

        // Create Pallete
        final List<BlockState> palette = schem.getPalette();
        final ListNBT paletteTag = new ListNBT();
        for (final BlockState bs : palette)
        {
            paletteTag.add(NBTUtil.writeBlockState(bs));
        }
        tag.put("palette", paletteTag);

        // Adding blocks
        final int[] blockInt = convertBlocksToSaveData(schem.getStructure(), schem.getSizeX(), schem.getSizeY(), schem.getSizeZ());
        tag.putIntArray("blocks", blockInt);

        // Adding Tile Entities
        final ListNBT finishedTes = new ListNBT();
        final CompoundNBT[] tes =
            Arrays.stream(schem.getTileEntities()).flatMap(Arrays::stream).flatMap(Arrays::stream).filter(Objects::nonNull).toArray(CompoundNBT[]::new);
        for (final CompoundNBT te : tes)
        {
            finishedTes.add(te);
        }
        tag.put("tile_entities", finishedTes);

        // Adding Entities
        final ListNBT finishedEntities = new ListNBT();
        final List<CompoundNBT> entities = schem.getEntities();
        for (final CompoundNBT entity : entities)
        {
            finishedEntities.add(entity);
        }
        tag.put("entities", finishedEntities);

        // Adding Required Mods
        final List<String> requiredMods = schem.getRequiredMods();
        final ListNBT modsList = new ListNBT();
        for (final String requiredMod : requiredMods)
        {
            // modsList.set(i,);
            modsList.add(new StringNBT(requiredMod));
        }
        tag.put("required_mods", modsList);

        final String name = schem.getName();
        final List<String> architects = schem.getArchitects();

        if (name != null)
        {
            tag.putString("name", name);
        }
        if (architects != null)
        {
            final ListNBT architectsTag = new ListNBT();
            for (final String architect : architects)
            {
                architectsTag.add(new StringNBT(architect));
            }
            tag.put("architects", architectsTag);
        }

        return tag;
    }

    /**
     * Deserializes a Blueprint form the Given CompoundNBT.
     * //param fixer the data fixer.
     *
     * @param nbtTag The CompoundNBT containing the Blueprint Data
     * @return A desserialized Blueprint
     */
    public static Blueprint readBlueprintFromNBT(final CompoundNBT nbtTag) // , final DataFixer fixer)
    {
        final CompoundNBT tag = nbtTag; // fixer.process(FixTypes.STRUCTURE, nbtTag);
        final byte version = tag.getByte("version");
        if (version == 1)
        {
            final short sizeX = tag.getShort("size_x"), sizeY = tag.getShort("size_y"), sizeZ = tag.getShort("size_z");

            // Reading required Mods
            final ListNBT modsList = (ListNBT) tag.get("required_mods");
            final List<String> requiredMods = new ArrayList<>();
            final List<String> missingMods = new ArrayList<>();
            for (final INBT mod : modsList)
            {
                final String modName = ((StringNBT) mod).getString();
                requiredMods.add(modName);
                if (!ModList.get().isLoaded(modName))
                {
                    LogManager.getLogger().warn("Found missing mod \"{}\" for Blueprint, some blocks may be missing!", modName);
                    missingMods.add(modName);
                }
            }

            // Reading Pallete
            final ListNBT paletteTag = (ListNBT) tag.get("palette");
            final List<BlockState> palette = new ArrayList<>(paletteTag.size());
            for (short i = 0; i < paletteTag.size(); i++)
            {
                palette.add(i, NBTUtil.readBlockState(paletteTag.getCompound(i)));
            }

            // Reading Blocks
            final short[][][] blocks = convertSaveDataToBlocks(tag.getIntArray("blocks"), sizeX, sizeY, sizeZ);

            // Reading Tile Entities
            final ListNBT teTag = (ListNBT) tag.get("tile_entities");
            final CompoundNBT[] tileEntities = new CompoundNBT[teTag.size()];
            for (short i = 0; i < tileEntities.length; i++)
            {
                tileEntities[i] = teTag.getCompound(i);
            }

            // Reading Entities
            final ListNBT entitiesTag = (ListNBT) tag.get("entities");
            final List<CompoundNBT> entities = new ArrayList<>(entitiesTag.size());
            for (short i = 0; i < entitiesTag.size(); i++)
            {
                entities.add(entitiesTag.getCompound(i));
            }

            final Blueprint schem = new Blueprint(sizeX, sizeY, sizeZ, palette, blocks, tileEntities, requiredMods).setMissingMods(missingMods).setEntities(entities);

            if (tag.contains("name"))
            {
                schem.setName(tag.getString("name"));
            }
            if (tag.contains("architects"))
            {
                final ListNBT architectsTag = (ListNBT) tag.get("architects");
                final List<String> architects = new ArrayList<>(architectsTag.size());
                for (int i = 0; i < architectsTag.size(); i++)
                {
                    architects.add(architectsTag.getString(i));
                }
                schem.setArchitects(architects);
            }

            return schem;
        }
        return null;
    }

    /**
     * Attempts to write a Blueprint to a Path.
     *
     * @param location The Path to write to
     * @param schem    The Blueprint to write
     */
    public static void writeToStream(final Path location, final Blueprint schem)
    {
        try
        {
            writeToStream(Files.newOutputStream(location, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), schem);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to write a Blueprint to an Output Stream.
     *
     * @param os    The Output Stream to write to
     * @param schem The Blueprint to write
     */
    public static void writeToStream(final OutputStream os, final Blueprint schem)
    {
        try
        {
            CompressedStreamTools.writeCompressed(writeBlueprintToNBT(schem), os);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to read a Blueprint from a Path.
     *
     * @param location The Path to read from
     * @return null when failed, blueprint otherwise
     */
    public static Blueprint readFromStream(final Path location)
    {
        try
        {
            return readFromStream(Files.newInputStream(location, StandardOpenOption.READ));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Attempts to read a Blueprint from an Input Stream.
     *
     * @param is The Input Stream to read from
     * @return null when failed, blueprint otherwise
     */
    public static Blueprint readFromStream(final InputStream is)
    {
        try
        {
            return readBlueprintFromNBT(CompressedStreamTools.readCompressed(is));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a 3 Dimensional short Array to a one Dimensional int Array.
     *
     * @param multDimArray 3 Dimensional short Array
     * @param sizeX        Sturcture size on the X-Axis
     * @param sizeY        Sturcture size on the Y-Axis
     * @param sizeZ        Sturcture size on the Z-Axis
     * @return An 1 Dimensional int array
     */
    private static int[] convertBlocksToSaveData(final short[][][] multDimArray, final short sizeX, final short sizeY, final short sizeZ)
    {
        // Converting 3 Dimensional Array to One DImensional
        final short[] oneDimArray = new short[sizeX * sizeY * sizeZ];

        int j = 0;
        for (short y = 0; y < sizeY; y++)
        {
            for (short z = 0; z < sizeZ; z++)
            {
                for (short x = 0; x < sizeX; x++)
                {
                    oneDimArray[j++] = multDimArray[y][z][x];
                }
            }
        }

        // Converting short Array to int Array
        final int[] ints = new int[(int) Math.ceil(oneDimArray.length / 2f)];

        int currentInt;
        for (int i = 1; i < oneDimArray.length; i += 2)
        {
            currentInt = oneDimArray[i - 1];
            currentInt = currentInt << CHUNK_BLOCK_SIZE | oneDimArray[i];
            ints[(int) Math.ceil(i / 2f) - 1] = currentInt;
        }
        if (oneDimArray.length % 2 == 1)
        {
            currentInt = oneDimArray[oneDimArray.length - 1] << CHUNK_BLOCK_SIZE;
            ints[ints.length - 1] = currentInt;
        }
        return ints;
    }

    /**
     * Converts a 1 Dimensional int Array to a 3 Dimensional short Array.
     *
     * @param ints  1 Dimensioanl int Array
     * @param sizeX Sturcture size on the X-Axis
     * @param sizeY Sturcture size on the Y-Axis
     * @param sizeZ Sturcture size on the Z-Axis
     * @return An 3 Dimensional short array
     */
    private static short[][][] convertSaveDataToBlocks(final int[] ints, final short sizeX, final short sizeY, final short sizeZ)
    {
        // Convert int array to short array
        final short[] oneDimArray = new short[ints.length * 2];

        for (int i = 0; i < ints.length; i++)
        {
            oneDimArray[i * 2] = (short) (ints[i] >> CHUNK_BLOCK_SIZE);
            oneDimArray[(i * 2) + 1] = (short) (ints[i]);
        }

        // Convert 1 Dimensional Array to 3 Dimensional Array
        final short[][][] multDimArray = new short[sizeY][sizeZ][sizeX];

        int i = 0;
        for (short y = 0; y < sizeY; y++)
        {
            for (short z = 0; z < sizeZ; z++)
            {
                for (short x = 0; x < sizeX; x++)
                {
                    multDimArray[y][z][x] = oneDimArray[i++];
                }
            }
        }
        return multDimArray;
    }
}
