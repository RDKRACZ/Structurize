package com.ldtteam.structurize.structure;

public class StructureId
{
    public static final StructureId EMPTY = new StructureId();
    private static final int PARTS_COUNT = 8;
    private static final String VERSION_REGEX = "^\\d+(\\.\\d+)*$";
    private String origin;
    private String author;
    private String stylePack;
    private String style;
    private String group;
    private String type;
    private String name;
    private String version;
    private String joined;

    private StructureId()
    {
    }

    public static StructureId of(final String structurePath) throws InvalidStructureIdException
    {
        if (structurePath.isEmpty())
        {
            return EMPTY;
        }

        final StructureId result = new StructureId();
        final String[] parts = structurePath.split(":");

        if (parts.length != PARTS_COUNT)
        {
            throw new InvalidStructureIdException("Path does not have " + PARTS_COUNT + " parts, found: " + parts.length);
        }

        int part = 0;
        result.origin = parts[part++];
        result.author = parts[part++];
        result.stylePack = parts[part++];
        result.style = parts[part++];
        result.group = parts[part++];
        result.type = parts[part++];
        result.name = parts[part++];
        result.version = parts[part++];
        result.joined = structurePath;

        if (!(result.origin.equals("local") || result.origin.equals("online") || result.origin.equals("mod")))
        {
            throw new InvalidStructureIdException("Invalid path origin, has to be local|online|mod, found: " + result.origin);
        }
        if (result.author.isEmpty())
        {
            throw new InvalidStructureIdException("Empty path author");
        }
        if (result.version.isEmpty())
        {
            throw new InvalidStructureIdException("Empty path version");
        }
        if (!(result.version.equals("latest") || result.version.matches(VERSION_REGEX)))
        {
            throw new InvalidStructureIdException(
                "Invalid path version, does not match regexp \"" + VERSION_REGEX + "\", found: " + result.version);
        }

        return result;
    }

    public String getOrigin()
    {
        return origin;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getStylePack()
    {
        return stylePack;
    }

    public String getStyle()
    {
        return style;
    }

    public String getGroup()
    {
        return group;
    }

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return joined;
    }

    public static final class InvalidStructureIdException extends RuntimeException
    {
        public InvalidStructureIdException(final String message)
        {
            super(message);
        }
    }
}
