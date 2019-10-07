package com.ldtteam.structurize.util;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface FastIterator<E> extends Iterator<E>
{
    default void forNextRemaining(final int stopAfter, final Consumer<E> action)
    {
        for (int i = 0; i < stopAfter && hasNext(); i++)
        {
            action.accept(next());
        }
    }

    default void forNextRemaining(final int stopAfter)
    {
        forNextRemaining(stopAfter, this::fastConsume);
    }

    default void forNextRemaining(final BooleanSupplier stopWhen, final Consumer<E> action)
    {
        while (!stopWhen.getAsBoolean() && hasNext())
        {
            action.accept(next());
        }
    }

    default void forNextRemaining(final BooleanSupplier stopWhen)
    {
        forNextRemaining(stopWhen, this::fastConsume);
    }

    void fastConsume(final E action);
}
