package com.ldtteam.structurize.event;

/**
 * sample text
 */
public interface EventConsumer<E extends Event>
{
    Result consume(E event);

    public static class Result
    {
        private final boolean success;
        private final String errorMessage;

        protected Result(final boolean success)
        {
            this(success, null);
        }

        protected Result(final boolean success, final String errorMessage)
        {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public boolean wasSuccess()
        {
            return success;
        }

        public boolean hasErrorMessage()
        {
            return errorMessage != null;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }
    }
}
