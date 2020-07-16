package com.ldtteam.structurize.structure.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;
import com.ldtteam.structurize.structure.Structure;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;

/**
 * TODO: modes: chunk format, blueprint format, etc. (simply mode enum)
 */
public class BlueprintUtils
{
    private enum BlueprintVersion
    {
        /**
         * old Structurize, any version before major 2
         */
        OLD(1, (nbt) -> {
            throw new RuntimeException("Use old mod version to create porting files.");
        }, (structure) -> {
            throw new RuntimeException("Cannot serialize to old format!");
        }),
        /**
         * This structurize, version major exactly 2
         */
        NEW(2, Structure::new, Structure::serializeNBT),
        /**
         * Chunk format used to upgrade data
         */
        CHUNK(3, ChunkFormatUtils::load, ChunkFormatUtils::save);

        private final int version;
        private final Function<Structure, CompoundNBT> serializer;
        private final Function<CompoundNBT, Structure> deserializer;

        BlueprintVersion(final int version,
            final Function<CompoundNBT, Structure> deserializer,
            final Function<Structure, CompoundNBT> serializer)
        {
            this.version = version;
            this.deserializer = deserializer;
            this.serializer = serializer;
        }

        /**
         * Tries to find given version and use its deserializer to process given NBT into structure.
         *
         * @param versionIn version to look for
         * @param nbt       NBT compound to deserialize
         * @return structure
         * @throws RuntimeException if version not found or if deserializer failed to do something
         */
        public static Structure deserialize(final int versionIn, final CompoundNBT nbt) throws RuntimeException
        {
            for (final BlueprintVersion bpVersion : values())
            {
                if (bpVersion.version == versionIn)
                {
                    return bpVersion.deserializer.apply(nbt);
                }
            }
            throw new RuntimeException("Unknown Blueprint version, ver: " + versionIn + ", nbt: " + nbt.toString());
        }
    }

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
     * Converts structure to blueprint NBT compound.
     *
     * @param structure structure to serialize
     * @param version   saving mode
     * @return blueprint NBT compound
     * @throws RuntimeException may throw if anything goes wrong
     */
    private static CompoundNBT writeStructureToBlueprintNBT(final Structure structure, final BlueprintVersion version)
        throws RuntimeException
    {
        final CompoundNBT nbt = version.serializer.apply(structure);
        nbt.putInt("version", version.version);
        return nbt;
    }

    /**
     * Converts and upgrades (if necessarry) blueprint NBT compound to structure.
     *
     * @param blueprintNbt blueprint to deserialize
     * @return structure
     * @throws RuntimeException may throw if anything goes wrong
     */
    private static Structure readStructureFromBlueprintNBT(final CompoundNBT blueprintNbt) throws RuntimeException
    {
        return BlueprintVersion.deserialize(blueprintNbt.getInt("version"), blueprintNbt);
    }

    /**
     * Attempts to write structure to file given by location.
     *
     * @param location  file to write to
     * @param structure structure to be written
     * @param version   saving mode
     * @throws RuntimeException may throw if anything goes wrong, eg. IO
     */
    public static void writeToFile(final Path location, final Structure structure, final BlueprintVersion version) throws RuntimeException
    {
        try (OutputStream os = Files.newOutputStream(location, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            writeToStream(os, structure, version);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("IO error", e);
        }
    }

    /**
     * Attempts to write structure to given output stream. Output stream is not closed by this method.
     *
     * @param os        output stream to write to
     * @param structure structure to be written
     * @param version   saving mode
     * @throws RuntimeException may throw if anything goes wrong, eg. IO
     */
    public static void writeToStream(final OutputStream os, final Structure structure, final BlueprintVersion version)
        throws RuntimeException
    {
        try
        {
            CompressedStreamTools.writeCompressed(writeStructureToBlueprintNBT(structure, version), os);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("IO error", e);
        }
    }

    /**
     * Attempts to read structure from file given by location.
     *
     * @param location file to read from
     * @return structure
     * @throws RuntimeException may throw if anything goes wrong, eg. IO
     */
    public static Structure readFromFile(final Path location) throws RuntimeException
    {
        try (InputStream is = Files.newInputStream(location, StandardOpenOption.READ))
        {
            return readFromStream(is);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("IO error", e);
        }
    }

    /**
     * Attempts to read structure from given input stream. Input stream is not closed by this method.
     *
     * @param is input stream to read from
     * @return structure
     * @throws RuntimeException may throw if anything goes wrong, eg. IO
     */
    public static Structure readFromStream(final InputStream is) throws RuntimeException
    {
        try
        {
            return readStructureFromBlueprintNBT(CompressedStreamTools.readCompressed(is));
        }
        catch (final IOException e)
        {
            throw new RuntimeException("IO error", e);
        }
    }
}
