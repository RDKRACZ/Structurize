package com.ldtteam.structurize.event;

/**
 * Base event class
 */
public class Event
{
    private boolean isCanceled = false;
    private boolean isSuspended = false;

    /**
     * Cancels event. Any reference will be removed on next check. Is permanent.
     * Event will not cause any modification anywhere.
     */
    public void cancel()
    {
        isCanceled = true;
    }

    /**
     * Suspends event. Event can be resumed by {@link #resume()}.
     * Event should not cause any modification until resumed. See sub-class for exact behaviour definition.
     * 
     * @see #resume()
     */
    public void suspend()
    {
        isSuspended = true;
    }

    /**
     * Resumes event. Does not check if event was suspended.
     * 
     * @see #suspend()
     */
    public void resume()
    {
        isSuspended = false;
    }

    /**
     * @see    #cancel()
     * @return canceled state
     */
    public boolean isCanceled()
    {
        return isCanceled;
    }

    /**
     * @see    #suspend()
     * @return suspended state
     */
    public boolean isSuspended()
    {
        return isSuspended;
    }
}
