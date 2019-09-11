package com.ldtteam.structurize.util;

import java.util.function.BiConsumer;

/**
 * Generic stage enum interface
 *
 * @param <T> StageData value
 * @param <U> base to run methods on
 */
public interface Stage<T, U>
{
    BiConsumer<U, StageData<T, U>> getRunMethod();

    BiConsumer<U, Stage<T, U>> getThenMethod();

    default void runStage(final U base, final StageData<T, U> stageData)
    {
        getRunMethod().accept(base, stageData);
    }

    default void endStage(final U base)
    {
        getThenMethod().accept(base, this);
    }

    default StageData<T, U> createData(final T data)
    {
        return new StageData<>(this, data);
    }

    default StageData<T, U> createEmptyData()
    {
        return new StageData<>(this, null);
    }

    public static class StageData<T, U>
    {
        private final Stage<T, U> stage;
        private final T data;

        public StageData(final Stage<T, U> stage, final T data)
        {
            this.stage = stage;
            this.data = data;
        }

        public void runStage(final U base)
        {
            stage.runStage(base, this);
        }

        public void endStage(final U base)
        {
            stage.endStage(base);
        }

        public Stage<T, U> getStage()
        {
            return stage;
        }

        public T getData()
        {
            return data;
        }
    }
}