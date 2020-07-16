package com.ldtteam.structurize.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.ldtteam.structurize.util.constant.DataVersion;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Structure attributes
 *
 * @param <T> attribute type
 * @param <N> nbt serialization type
 */
public class Attribute<T, N extends INBT>
{
    /**
     * Pritable structure name.
     * Required thus always present
     */
    public static final Attribute<String, StringNBT> NAME = new Attribute<>(0,
        "Name",
        String.class,
        null,
        StringNBT::valueOf,
        StringNBT::getString);
    /**
     * Printable author username.
     * Default is empty string
     */
    public static final Attribute<String, StringNBT> AUTHOR = new Attribute<>(1,
        "Author",
        String.class,
        "",
        StringNBT::valueOf,
        StringNBT::getString);
    /**
     * Entrance or main side which should face player's camera when opening structure preview.
     * DOWN and UP are ignored and NORTH is used as default value
     */
    public static final Attribute<Direction, ByteNBT> ENTRANCE_SIDE = new Attribute<>(2,
        "Entrance side",
        Direction.class,
        Direction.NORTH,
        direction -> ByteNBT.valueOf((byte) direction.getHorizontalIndex()),
        nbt -> Direction.byHorizontalIndex(nbt.getByte()));
    /**
     * Relative Y pos which should be on same level as player's foot when opening structure preview.
     * Default is zero
     */
    public static final Attribute<Byte, ByteNBT> GROUND_LEVEL = new Attribute<>(3,
        "Ground level",
        Byte.class,
        Byte.valueOf((byte) 0),
        ByteNBT::valueOf,
        ByteNBT::getByte);
    /**
     * Schematic system identifier.
     * Default is {@link StructureId#EMPTY} if structure was not uploaded on or created for schematic server
     */
    public static final Attribute<StructureId, StringNBT> SCHEMATIC_ID = new Attribute<>(4,
        "Schematic ID",
        StructureId.class,
        StructureId.EMPTY,
        id -> StringNBT.valueOf(id.toString()),
        nbt -> StructureId.of(nbt.getString()));
    /**
     * Minecraft data version.
     * Default is data version of this running Minecraft instance
     */
    public static final Attribute<DataVersion, ShortNBT> DATA_VERSION = new Attribute<>(5,
        "Minecraft version",
        DataVersion.class,
        DataVersion.CURRENT,
        dataVersion -> ShortNBT.valueOf((short) dataVersion.getDataVersion()),
        nbt -> DataVersion.findFromDataVersion(nbt.getShort()));
    /**
     * Blockpos containing size of structure.
     * Required thus always present
     */
    public static final Attribute<BlockPos, CompoundNBT> SIZE = new Attribute<>(6,
        "Size",
        BlockPos.class,
        null,
        blockPos -> NBTUtil.writeBlockPos(blockPos),
        nbt -> NBTUtil.readBlockPos(nbt));
    /**
     * Mods required to use this structure.
     * Default is empty list
     * TODO: add mod dependency class, equals modId + version equals or newer
     */
    public static final Attribute<List, ListNBT> REQUIRED_MODS = new Attribute<List, ListNBT>(7,
        "Required mods",
        List.class,
        new ArrayList<>(),
        list -> {
            final ListNBT nbt = new ListNBT();
            return nbt;
        },
        nbt -> {
            final ArrayList list = new ArrayList();
            return list;
        });
    /**
     * Center pos for unrotated and unmirrored structure.
     * Required thus always present, default is (created during structure creating) center of structure for 2x2 center mode
     */
    public static final Attribute<BlockPos, CompoundNBT> DEFAULT_CENTER = new Attribute<>(8,
        "Center position",
        BlockPos.class,
        null,
        blockPos -> NBTUtil.writeBlockPos(blockPos),
        nbt -> NBTUtil.readBlockPos(nbt));
    /**
     * Center mode: single or 2x2.
     * Required thus always present, default is 2x2 mode
     */
    public static final Attribute<CenterMode, ByteNBT> CENTER_MODE = new Attribute<>(9,
        "Center mode",
        CenterMode.class,
        CenterMode.DOUBLE,
        centerMode -> ByteNBT.valueOf((byte) centerMode.getId()),
        nbt -> CenterMode.fromId(nbt.getByte()));

    /**
     * Collection of all attributes
     */
    public static final List<Attribute<?, ?>> VALUES = new ArrayList<>();

    /**
     * Serializable integer id
     */
    private final int id;
    /**
     * Printable attribute name
     */
    private final String name;
    /**
     * Type so it can be easily casted from Object map
     */
    private final Class<T> type;
    /**
     * Type so it can be easily casted from Object map
     */
    private final T defaultValue;
    /**
     * Converts value to nbt type
     */
    private final Function<T, N> nbtSerializer;
    /**
     * Converts nbt type to value
     */
    private final Function<N, T> nbtDeserializer;

    private Attribute(final int id,
        final String name,
        final Class<T> type,
        final T defaultValue,
        final Function<T, N> nbtSerializer,
        final Function<N, T> nbtDeserializer)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.nbtSerializer = nbtSerializer;
        this.nbtDeserializer = nbtDeserializer;
        VALUES.add(this);
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Class<T> getType()
    {
        return type;
    }

    public T getDefaultValue()
    {
        // Might need supplier for things like UUID.random(), please preserve T as return type
        return defaultValue;
    }

    public T cast(final Object object)
    {
        return getType().cast(object);
    }

    public N serialize(final Object value)
    {
        return nbtSerializer.apply(cast(value));
    }

    @SuppressWarnings("unchecked")
    public T deserialize(final Object nbt)
    {
        return nbtDeserializer.apply((N) nbt);
    }
}
