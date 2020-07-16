package com.ldtteam.structurize.util;

import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigValue<T, U extends ConfigValue<T, U>>
{
    protected final ConfigContext<T, U> context;
    protected T value;
    protected Function<String, T> setValueConvertor;

    protected ConfigValue(final ConfigContext<T, U> context, final Function<String, T> setValueConvertor)
    {
        this.context = context;
        this.value = context.defaultValue;
        this.setValueConvertor = setValueConvertor;
    }

    public void setValueFromString(final String value)
    {
        if (context.valueValidator.test(this))
        {
            this.value = setValueConvertor.apply(value);
        }
    }

    public void setValue(final T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return value;
    }

    public String getNameKey()
    {
        return context.nameKey;
    }

    public String getDescriptionKey()
    {
        return context.descriptionKey;
    }

    public Predicate<GenericConfiguration> getEnabledPredicate()
    {
        return context.enabledPredicate;
    }

    public Predicate<ConfigValue<T, U>> getValueValidator()
    {
        return context.valueValidator;
    }

    public String getInvalidValueKey()
    {
        return context.invalidValueKey;
    }

    public T getDefaultValue()
    {
        return context.defaultValue;
    }

    public abstract static class ConfigContext<T, U extends ConfigValue<T, U>>
    {
        private String nameKey;
        private String descriptionKey;
        private Predicate<GenericConfiguration> enabledPredicate;
        private Predicate<ConfigValue<T, U>> valueValidator;
        private String invalidValueKey;
        private T defaultValue;

        protected ConfigContext()
        {
        }

        public ConfigContext<T, U> setNameKey(final String nameKey)
        {
            this.nameKey = nameKey;
            return this;
        }

        public ConfigContext<T, U> setDescriptionKey(final String descriptionKey)
        {
            this.descriptionKey = descriptionKey;
            return this;
        }

        public ConfigContext<T, U> setEnabledPredicate(final Predicate<GenericConfiguration> enabledPredicate)
        {
            this.enabledPredicate = enabledPredicate;
            return this;
        }

        public ConfigContext<T, U> setValueValidator(Predicate<ConfigValue<T, U>> valueValidator)
        {
            this.valueValidator = valueValidator;
            return this;
        }

        public ConfigContext<T, U> setInvalidValueKey(String invalidValueKey)
        {
            this.invalidValueKey = invalidValueKey;
            return this;
        }

        public ConfigContext<T, U> setDefaultValue(final T defaultValue)
        {
            this.defaultValue = defaultValue;
            return this;
        }

        public abstract U build();

        public abstract U build(final Function<String, T> setValueConvertor);
    }
}
