package com.ldtteam.structurize.structure.data;

import com.ldtteam.structurize.structure.Attribute;
import com.ldtteam.structurize.structure.IAttributeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundNBT;

public class AttributeMap implements IAttributeMap
{
    private final Int2ObjectMap<Object> attributeMap = new Int2ObjectArrayMap<>(Attribute.VALUES.size());

    public AttributeMap()
    {
        for (final Attribute<?, ?> attribute : Attribute.VALUES)
        {
            attributeMap.put(attribute.getId(), attribute.getDefaultValue());
        }
    }

    public AttributeMap(final CompoundNBT nbt)
    {
        for (final Attribute<?, ?> attribute : Attribute.VALUES)
        {
            final String key = Integer.toString(attribute.getId());
            if (nbt.contains(key))
            {
                attributeMap.put(attribute.getId(), attribute.deserialize(nbt.get(key)));
            }
            else
            {
                // TODO: notify chat when opening?
                attributeMap.put(attribute.getId(), attribute.getDefaultValue());
            }
        }
    }

    @Override
    public <T> T getAttributeValue(final Attribute<T, ?> attribute)
    {
        final Object value = attributeMap.get(attribute.getId());
        return attribute.cast(value);
    }

    public <T> void setAttributeValue(final Attribute<T, ?> attribute, final T value)
    {
        attributeMap.put(attribute.getId(), value);
    }

    public CompoundNBT serializeNBT()
    {
        final CompoundNBT nbt = new CompoundNBT();
        for (final Attribute<?, ?> attribute : Attribute.VALUES)
        {
            nbt.put(Integer.toString(attribute.getId()), attribute.serialize(attributeMap.get(attribute.getId())));
        }
        return nbt;
    }
}
