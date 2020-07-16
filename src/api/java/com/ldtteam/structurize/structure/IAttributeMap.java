package com.ldtteam.structurize.structure;

/**
 * Structure {@link Attribute} map
 */
public interface IAttributeMap
{
    /**
     * Getter for attribute.
     *
     * @param <T>       attribute type
     * @param attribute attribute enum
     * @return value of attribute in this map, if not found then it's default value
     */
    <T> T getAttributeValue(Attribute<T, ?> attribute);
}
