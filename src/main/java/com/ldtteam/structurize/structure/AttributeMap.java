package com.ldtteam.structurize.structure;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

public class AttributeMap implements IAttributeMap
{
    private Int2ObjectArrayMap<Object> attributeMap;

    public AttributeMap()
    {
        attributeMap = new Int2ObjectArrayMap<>(Attribute.ATTRIBUTES.size());
        for (final Attribute<?> attribute : Attribute.ATTRIBUTES)
        {
            attributeMap.put(attribute.getId(), attribute.getDefaultValue());
        }
    }

    @Override
    public <T> T getAttributeValue(final Attribute<T> attribute)
    {
        final Object value = attributeMap.get(attribute.getId());
        return attribute.getType().cast(value);
    }

    public <T> void setAttributeValue(final Attribute<T> attribute, final T value)
    {
        attributeMap.put(attribute.getId(), value);
    }
}
