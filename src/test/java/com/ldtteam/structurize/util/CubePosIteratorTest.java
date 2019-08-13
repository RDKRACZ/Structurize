package com.ldtteam.structurize.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Iterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import net.minecraft.util.math.BlockPos;

@RunWith(MockitoJUnitRunner.class)
public class CubePosIteratorTest
{
    @Test
    public void test555Cube()
    {
        final int max = 5;
        final Iterator<BlockPos> ourIterator = new CubePosIterator(BlockPos.ZERO, new BlockPos(max, max, max)).iterator();

        for (int y = 0; y <= max; y++)
        {
            for (int z = 0; z <= max; z++)
            {
                for (int x = 0; x <= max; x++)
                {
                    assertTrue(ourIterator.hasNext());
                    assertTrue(ourIterator.next().equals(new BlockPos(x, y, z)));
                }
            }
        }
        assertFalse(ourIterator.hasNext());
    }
}
