package com.ldtteam.structurize.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import com.ldtteam.structurize.util.constants.MathConstants;

public final class RenderUtil
{
    /**
     * Private constructor to hide implicit public one.
     */
    private RenderUtil()
    {
        /**
         * Intentionally left empty
         */
    }

    public static void applyRotationToYAxis(@NotNull final Rotation rotation, @NotNull final BlockPos appliedPrimaryBlockOff)
    {
        GlStateManager.translatef(appliedPrimaryBlockOff.getX() - 0.5f, 0F, appliedPrimaryBlockOff.getZ() - 0.5f);

        final double angle;
        switch (rotation)
        {
            case NONE:
                angle = 0F;
                break;
            case CLOCKWISE_90:
                angle = -MathConstants.NINETY_DEGREES;
                break;
            case CLOCKWISE_180:
                angle = -MathConstants.ONE_HUNDRED_EIGHTY_DEGREES;
                break;
            case COUNTERCLOCKWISE_90:
                angle = MathConstants.NINETY_DEGREES;
                break;
            default:
                angle = 0F;
                break;
        }

        GlStateManager.rotated(angle, 0, 1, 0);

        GlStateManager.translatef(-appliedPrimaryBlockOff.getX() + 0.5f, 0F, -appliedPrimaryBlockOff.getZ() + 0.5f);
    }

    public static void applyMirror(@NotNull final Mirror mirror, @NotNull final BlockPos appliedPrimaryBlockOff)
    {
        switch (mirror)
        {
            case NONE:
                // GlStateManager.scaled(1, 1, 1);
                break;
            case FRONT_BACK:
                GlStateManager.translated((2 * appliedPrimaryBlockOff.getX()) + 1, 0, 0);
                GlStateManager.scaled(-1, 1, 1);
                break;
            case LEFT_RIGHT:
                GlStateManager.translated(0, 0, (2 * appliedPrimaryBlockOff.getZ()) + 1);
                GlStateManager.scaled(1, 1, -1);
                break;
            default:
                // Should never occur.
                break;
        }
    }
}