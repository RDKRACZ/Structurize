package com.ldtteam.structurize.util.constant;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataVersionTest
{
    @Test
    public void testFinders()
    {
        assertTrue(DataVersion.findFromDataVersion(0) == DataVersion.DEFAULT);
        assertTrue(DataVersion.findFromDataVersion(1139) == DataVersion.v1_12);
        assertTrue(DataVersion.findFromDataVersion(1240) == DataVersion.v1_12);
        assertTrue(DataVersion.findFromDataVersion(Integer.MAX_VALUE) == DataVersion.UPCOMING);
        assertTrue(DataVersion.findFromMcVersion("") == DataVersion.DEFAULT);
        assertTrue(DataVersion.findFromMcVersion("1.12") == DataVersion.v1_12);
    }
}
