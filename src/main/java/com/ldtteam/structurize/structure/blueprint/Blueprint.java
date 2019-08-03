package com.ldtteam.structurize.structure.blueprint;

import java.util.ArrayList;
import java.util.List;
import com.ldtteam.structurize.structure.StructureBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import static com.ldtteam.structurize.util.constants.MathConstants.NINETY_DEGREES;

/**
 * The blueprint class which contains the file format for the schematics.
 */
public class Blueprint
{
    /**
     * The list of required mods.
     */
    private final List<String> requiredMods;

    /**
     * The size of the blueprint.
     */
    private short sizeX, sizeY, sizeZ;

    /**
     * The palette of different blocks.
     */
    private List<BlockState> palette;

    /**
     * The name of the blueprint.
     */
    private String name;

    /**
     * The name of the builders.
     */
    private List<String> architects;

    /**
     * A list of missing modids that were missing while this schematic was loaded.
     */
    private List<String> missingMods;

    /**
     * The Schematic Data, each short represents an entry in the {@link Blueprint#palette}.
     */
    private short[][][] structure;

    /**
     * The tileentities.
     */
    private List<CompoundNBT> tileEntities;

    /**
     * The entities.
     */
    private List<CompoundNBT> entities = new ArrayList<>();

    /**
     * Constructor of a new Blueprint.
     *
     * @param structBB     the structure bounding box.
     * @param palette      the palette.
     * @param structure    the structure data.
     * @param tileEntities the tileEntities.
     * @param requiredMods the required mods.
     */
    protected Blueprint(
        final StructureBB structBB,
        final List<BlockState> palette,
        final short[][][] structure,
        final List<CompoundNBT> tileEntities,
        final List<String> requiredMods)
    {
        this((short) structBB.getXSize(), (short) structBB.getYSize(), (short) structBB.getZSize(), palette, structure, tileEntities, requiredMods);
    }

    /**
     * Constructor of a new Blueprint.
     *
     * @param sizeX        the x size.
     * @param sizeY        the y size.
     * @param sizeZ        the z size.
     * @param palette      the palette.
     * @param structure    the structure data.
     * @param tileEntities the tileEntities.
     * @param requiredMods the required mods.
     */
    protected Blueprint(
        final short sizeX,
        final short sizeY,
        final short sizeZ,
        final List<BlockState> palette,
        final short[][][] structure,
        final List<CompoundNBT> tileEntities,
        final List<String> requiredMods)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palette = palette;
        this.structure = structure;
        this.tileEntities = tileEntities;
        this.requiredMods = requiredMods;
    }

    /**
     * Constructor of a new Blueprint.
     *
     * @param structBB the structure bounding box.
     */
    public Blueprint(final StructureBB structBB)
    {
        this((short) structBB.getXSize(), (short) structBB.getYSize(), (short) structBB.getZSize());
    }

    /**
     * Constructor of a new Blueprint.
     *
     * @param sizeX the x size.
     * @param sizeY the y size.
     * @param sizeZ the z size.
     */
    public Blueprint(final short sizeX, final short sizeY, final short sizeZ)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.structure = new short[sizeY][sizeZ][sizeX];
        this.tileEntities = new ArrayList<>();

        this.requiredMods = new ArrayList<>();
        this.palette = new ArrayList<>();
        this.palette.add(Blocks.AIR.getDefaultState());
        // this.palette.add(0, ModBlocks.blockSubstitution.getDefaultState()); ??? why though
    }

    /**
     * @return the Size of the Structure on the X-Axis (without rotation and/or mirroring)
     */
    public short getSizeX()
    {
        return sizeX;
    }

    /**
     * @return the Size of the Structure on the Y-Axis (without rotation and/or mirroring)
     */
    public short getSizeY()
    {
        return sizeY;
    }

    /**
     * @return the Size of the Structure on the Z-Axis (without rotation and/or mirroring)
     */
    public short getSizeZ()
    {
        return sizeZ;
    }

    /**
     * @return the pallete (without rotation and/or mirroring)
     */
    public List<BlockState> getPalette()
    {
        return palette;
    }

    /**
     * Add a blockstate to the structure.
     *
     * @param pos   the position to add it to.
     * @param state the state to add.
     */
    public void addBlockState(final BlockPos pos, final BlockState state)
    {
        int index = palette.indexOf(state);

        if (index == -1)
        {
            index = palette.size();
            palette.add(state);
        }

        structure[pos.getY()][pos.getZ()][pos.getX()] = (short) index;
    }

    /**
     * @return the structure (without rotation and/or mirroring) The Coordinate order is: y, z, x
     */
    public short[][][] getStructure()
    {
        return structure;
    }

    /**
     * @return an array of serialized TileEntities (posX, posY and posZ tags have been localized to coordinates within the structure)
     */
    public List<CompoundNBT> getTileEntities()
    {
        return tileEntities;
    }

    /**
     * @return an array of serialized TileEntities (the Pos tag has been localized to coordinates within the structure)
     */
    public List<CompoundNBT> getEntities()
    {
        return entities;
    }

    /**
     * @param entitiesIn an array of serialized TileEntities (the Pos tag need to be localized to coordinates within the structure)
     * @return this object.
     */
    public Blueprint setEntities(final List<CompoundNBT> entitiesIn)
    {
        entities = entitiesIn;
        return this;
    }

    /**
     * @return a list of all required mods as modid's
     */
    public List<String> getRequiredMods()
    {
        return requiredMods;
    }

    /**
     * @return the Name of the Structure
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the Structure.
     *
     * @param nameIn the name to set.
     * @return this object.
     */
    public Blueprint setName(final String nameIn)
    {
        name = nameIn;
        return this;
    }

    /**
     * @return an Array of all architects for this structure
     */
    public List<String> getArchitects()
    {
        return architects;
    }

    /**
     * Sets an Array of all architects for this structure.
     *
     * @param architectsIn an array of architects.
     * @return this blueprint.
     */
    public Blueprint setArchitects(final List<String> architectsIn)
    {
        architects = architectsIn;
        return this;
    }

    /**
     * @return An Array of all missing mods that are required to generate this structure
     *         (only works if structure was loaded from file)
     */
    public List<String> getMissingMods()
    {
        return missingMods;
    }

    /**
     * Sets the missing mods.
     *
     * @param missingModsIn the missing mods list.
     * @return this object.
     */
    public Blueprint setMissingMods(final List<String> missingModsIn)
    {
        missingMods = missingModsIn;
        return this;
    }

    /**
     * Get a list of all entities in the blueprint as a list.
     *
     * @return the list of CompoundNBTs.
     */
    public final List<CompoundNBT> getEntitiesAsList()
    {
        return entities;
    }

    /**
     * Rotate the structure depending on the direction it's facing.
     *
     * @param rotation times to rotateWithMirror.
     * @param mirror   the mirror.
     * @param world    the world.
     */
    public void rotateWithMirror(final Rotation rotation, final Mirror mirror, final World world)
    {
        final BlockPos resultSize = transformedSize(new BlockPos(sizeX, sizeY, sizeZ), rotation);

        final List<BlockState> tempPalette = new ArrayList<>();
        for (int i = 0; i < palette.size(); i++)
        {
            tempPalette.add(i, palette.get(i).getBlockState().mirror(mirror).rotate(rotation));
        }
        palette = tempPalette;

        final BlockPos extremes = transformedBlockPos(sizeX, sizeY, sizeZ, mirror, rotation);
        final int minX = extremes.getX() < 0 ? -extremes.getX() - 1 : 0;
        final int minY = extremes.getY() < 0 ? -extremes.getY() - 1 : 0;
        final int minZ = extremes.getZ() < 0 ? -extremes.getZ() - 1 : 0;

        final short[][][] newStructure = new short[resultSize.getY()][resultSize.getZ()][resultSize.getX()];
        for (short x = 0; x < sizeX; x++)
        {
            for (short y = 0; y < sizeY; y++)
            {
                for (short z = 0; z < sizeZ; z++)
                {
                    final BlockPos tempPos = transformedBlockPos(x, y, z, mirror, rotation).add(minX, minY, minZ);
                    newStructure[tempPos.getY()][tempPos.getZ()][tempPos.getX()] = structure[y][z][x];
                }
            }
        }
        structure = newStructure;

        final List<CompoundNBT> newEntities = new ArrayList<>();
        for (final CompoundNBT entity : entities)
        {
            if (entity != null)
            {
                newEntities.add(transformEntityInfoWithSettings(entity, world, new BlockPos(minX, minY, minZ), rotation, mirror));
            }
        }
        entities = newEntities;

        final List<CompoundNBT> newTileEntities = new ArrayList<>();
        for (final CompoundNBT te : tileEntities)
        {
            if (te != null)
            {
                final BlockPos pos = transformedBlockPos(te.getInt("x"), te.getInt("y"), te.getInt("z"), mirror, rotation).add(minX, minY, minZ);
                te.putInt("x", pos.getX());
                te.putInt("y", pos.getY());
                te.putInt("z", pos.getZ());
                newTileEntities.add(te);
            }
        }
        tileEntities = newTileEntities;

        sizeX = (short) resultSize.getX();
        sizeY = (short) resultSize.getY();
        sizeZ = (short) resultSize.getZ();
    }

    /**
     * Calculate the transformed size from a blockpos.
     *
     * @param pos      the pos to transform
     * @param rotation the rotation to apply.
     * @return the resulting size.
     */
    public static BlockPos transformedSize(final BlockPos pos, final Rotation rotation)
    {
        switch (rotation)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                return new BlockPos(pos.getZ(), pos.getY(), pos.getX());
            default:
                return pos;
        }
    }

    /**
     * Transforms a blockpos with mirror and rotation.
     *
     * @param x        the x input.
     * @param y        the y input.
     * @param z        the z input.
     * @param mirror   the mirror.
     * @param rotation the rotation.
     * @return the resulting position.
     */
    public static BlockPos transformedBlockPos(final int x, final int y, final int z, final Mirror mirror, final Rotation rotation)
    {
        final BlockPos result;

        switch (mirror)
        {
            case LEFT_RIGHT:
                result = new BlockPos(x, y, -z);
                break;
            case FRONT_BACK:
                result = new BlockPos(-x, y, z);
                break;
            default:
                result = new BlockPos(x, y, z);
                break;
        }

        return result.rotate(rotation);
    }

    /**
     * Transform an entity and rotate it.
     *
     * @param entityInfo the entity nbt.
     * @param world      the world.
     * @param pos        the position.
     * @param rotation   the wanted rotation.
     * @param mirror     the mirror.
     * @return the updated nbt.
     */
    private CompoundNBT transformEntityInfoWithSettings(
        final CompoundNBT entityInfo,
        final World world,
        final BlockPos pos,
        final Rotation rotation,
        final Mirror mirror)
    {
        final Entity finalEntity = EntityType.loadEntityUnchecked(entityInfo, world).get();
        if (finalEntity != null)
        {
            final Vec3d entityVec = Blueprint.transformedVec3d(rotation, mirror, finalEntity.getPositionVector()).add(new Vec3d(pos));
            finalEntity.prevRotationYaw = (float) (finalEntity.getMirroredYaw(mirror) - NINETY_DEGREES);
            final double rotationYaw =
                finalEntity.getMirroredYaw(mirror) + ((double) finalEntity.getMirroredYaw(mirror) - (double) finalEntity.getRotatedYaw(rotation));

            if (finalEntity instanceof HangingEntity)
            {
                final BlockPos currentPos = ((HangingEntity) finalEntity).getHangingPosition();
                final BlockPos entityPos = Blueprint.transformedBlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ(), mirror, rotation).add(pos);

                finalEntity.posX = entityVec.x;
                finalEntity.posY = entityVec.y;
                finalEntity.posZ = entityVec.z;

                finalEntity.setPosition(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            }
            else
            {
                finalEntity.setLocationAndAngles(entityVec.x, entityVec.y, entityVec.z, (float) rotationYaw, finalEntity.rotationPitch);
            }

            return finalEntity.serializeNBT();
        }

        return null;
    }

    /**
     * Transform a Vec3d with rotation and mirror.
     *
     * @param rotation the rotation.
     * @param mirror   the mirror.
     * @param vec      the vec to transform.
     * @return the result.
     */
    private static Vec3d transformedVec3d(final Rotation rotation, final Mirror mirror, final Vec3d vec)
    {
        double xCoord = vec.x;
        double zCoord = vec.z;
        boolean flag = true;

        switch (mirror)
        {
            case LEFT_RIGHT:
                zCoord = 1.0D - zCoord;
                break;
            case FRONT_BACK:
                xCoord = 1.0D - xCoord;
                break;
            default:
                flag = false;
        }

        switch (rotation)
        {
            case COUNTERCLOCKWISE_90:
                return new Vec3d(zCoord, vec.y, 1.0D - xCoord);
            case CLOCKWISE_90:
                return new Vec3d(1.0D - zCoord, vec.y, xCoord);
            case CLOCKWISE_180:
                return new Vec3d(1.0D - xCoord, vec.y, 1.0D - zCoord);
            default:
                return flag ? new Vec3d(xCoord, vec.y, zCoord) : vec;
        }
    }
}
