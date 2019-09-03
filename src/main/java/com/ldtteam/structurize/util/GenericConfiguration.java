package com.ldtteam.structurize.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.ldtteam.structurize.util.ConfigValue.ConfigContext;

public class GenericConfiguration
{
    private List<ConfigValue<?, ?>> values;
    private boolean isFrozen;

    public GenericConfiguration()
    {
        this.values = new ArrayList<>();
        this.isFrozen = false;
    }

    public void freeze()
    {
        isFrozen = true;
    }

    public void addConfigValue(final ConfigValue<?, ?> configValue)
    {
        if (isFrozen)
        {
            throw new RuntimeException("Cannot modify frozen configuration!");
        }
        values.add(configValue);
    }

    public List<ConfigValue<?, ?>> getValues()
    {
        return values;
    }

    public class BooleanConfigValue extends ConfigValue<Boolean, BooleanConfigValue>
    {
        public BooleanConfigValue(final BooleanConfigContext context, final Function<String, Boolean> setValueConvertor)
        {
            super(context, setValueConvertor);
        }
    }

    public class BooleanConfigContext extends ConfigContext<Boolean, BooleanConfigValue>
    {
        @Override
        public BooleanConfigValue build()
        {
            return build(string -> Boolean.valueOf(string));
        }

        @Override
        public BooleanConfigValue build(final Function<String, Boolean> setValueConvertor)
        {
            final BooleanConfigValue result = new BooleanConfigValue(this, setValueConvertor);
            addConfigValue(result);
            return result;
        }
    }

    public BooleanConfigContext newBooleanValue()
    {
        return new BooleanConfigContext();
    }

    public class IntegerConfigValue extends ConfigValue<Integer, IntegerConfigValue>
    {
        public IntegerConfigValue(final IntegerConfigContext context, final Function<String, Integer> setValueConvertor)
        {
            super(context, setValueConvertor);
        }
    }

    public class IntegerConfigContext extends ConfigContext<Integer, IntegerConfigValue>
    {
        @Override
        public IntegerConfigValue build()
        {
            return build(string -> Integer.valueOf(string));
        }

        @Override
        public IntegerConfigValue build(final Function<String, Integer> setValueConvertor)
        {
            final IntegerConfigValue result = new IntegerConfigValue(this, setValueConvertor);
            addConfigValue(result);
            return result;
        }
    }

    public IntegerConfigContext newIntegerValue()
    {
        return new IntegerConfigContext();
    }

    public class FloatConfigValue extends ConfigValue<Float, FloatConfigValue>
    {
        public FloatConfigValue(final FloatConfigContext context, final Function<String, Float> setValueConvertor)
        {
            super(context, setValueConvertor);
        }
    }

    public class FloatConfigContext extends ConfigContext<Float, FloatConfigValue>
    {
        @Override
        public FloatConfigValue build()
        {
            return build(string -> Float.valueOf(string));
        }

        @Override
        public FloatConfigValue build(final Function<String, Float> setValueConvertor)
        {
            final FloatConfigValue result = new FloatConfigValue(this, setValueConvertor);
            addConfigValue(result);
            return result;
        }
    }

    public FloatConfigContext newFloatValue()
    {
        return new FloatConfigContext();
    }

    public class StringConfigValue extends ConfigValue<String, StringConfigValue>
    {
        public StringConfigValue(final StringConfigContext context, final Function<String, String> setValueConvertor)
        {
            super(context, setValueConvertor);
        }
    }

    public class StringConfigContext extends ConfigContext<String, StringConfigValue>
    {
        @Override
        public StringConfigValue build()
        {
            return build(string -> string);
        }

        @Override
        public StringConfigValue build(final Function<String, String> setValueConvertor)
        {
            final StringConfigValue result = new StringConfigValue(this, setValueConvertor);
            addConfigValue(result);
            return result;
        }
    }

    public StringConfigContext newStringValue()
    {
        return new StringConfigContext();
    }
}