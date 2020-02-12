package com.ldtteam.structurize.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.ldtteam.structurize.util.constant.DataVersion;
import net.minecraft.util.Direction;

/**
 * Structure attributes
 *
 * @param <T> attribute type
 */
public class Attribute<T>
{
    /**
     * Pritable structure name.
     * Required thus always present
     */
    public static final Attribute<String> NAME = new Attribute<>(0, "Name", String.class, null);
    /**
     * Printable author username.
     * Default is empty string
     */
    public static final Attribute<String> AUTHOR = new Attribute<>(1, "Author", String.class, "");
    /**
     * Entrance or main side which should face player's camera when opening structure preview.
     * DOWN and UP are ignored and NORTH is used as fallback
     */
    public static final Attribute<Direction> ENTRANCE_SIDE = new Attribute<>(2, "Entrance side", Direction.class, Direction.NORTH);
    /**
     * Relative Y pos which should be on same level as player's foot when opening structure preview.
     * Default is zero
     */
    public static final Attribute<Short> GROUND_LEVEL = new Attribute<>(3, "Ground level", Short.class, Short.valueOf((short) 0));
    /**
     * Author system identifier.
     * Null if structure was not uploaded on or created for structure server
     */
    public static final Attribute<UUID> AUTHOR_UUID = new Attribute<>(4, "account identifier", UUID.class, null);
    /**
     * Schematic system identifier.
     * Null if structure was not uploaded on or created for structure server
     */
    public static final Attribute<UUID> SCHEMATIC_UUID = new Attribute<>(5, "schematic identifier", UUID.class, null);
    /**
     * Minecraft data version.
     * Default is data version of this running Minecraft instance
     */
    public static final Attribute<DataVersion> DATA_VERSION = new Attribute<>(6,
        "Minecraft version",
        DataVersion.class,
        DataVersion.CURRENT);
    /**
     * Mods required to use this structure.
     * Default is empty list
     * TODO: add mod dependency class, equals modId + version equals or newer
     */
    public static final Attribute<List> REQUIRED_MODS = new Attribute<List>(7, "Required mods", List.class, new ArrayList<>());

    /**
     * Collection of all attributes
     */
    public static final List<Attribute<?>> ATTRIBUTES = new ArrayList<>();

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

    private Attribute(final int id, final String name, final Class<T> type, final T defaultValue)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        ATTRIBUTES.add(this);
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
}
