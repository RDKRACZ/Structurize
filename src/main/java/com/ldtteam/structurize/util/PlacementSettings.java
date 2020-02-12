package com.ldtteam.structurize.util;

import net.minecraft.util.Rotation;
import net.minecraft.util.Mirror;

/**
 * Represents all possible states of structure when rorating or mirroring
 */
public enum PlacementSettings
{
    A(Rotation.NONE, Mirror.NONE),
    B(Rotation.CLOCKWISE_90, Mirror.NONE),
    C(Rotation.CLOCKWISE_180, Mirror.NONE),
    D(Rotation.COUNTERCLOCKWISE_90, Mirror.NONE),
    E(Rotation.NONE, Mirror.FRONT_BACK),
    F(Rotation.CLOCKWISE_90, Mirror.FRONT_BACK),
    G(Rotation.CLOCKWISE_180, Mirror.FRONT_BACK),
    H(Rotation.COUNTERCLOCKWISE_90, Mirror.FRONT_BACK);

    static
    {
        A.rotateCW90 = B;
        B.rotateCW90 = C;
        C.rotateCW90 = D;
        D.rotateCW90 = A;
        E.rotateCW90 = F;
        F.rotateCW90 = G;
        G.rotateCW90 = H;
        H.rotateCW90 = E;

        A.rotateCW180 = C;
        B.rotateCW180 = D;
        C.rotateCW180 = A;
        D.rotateCW180 = B;
        E.rotateCW180 = G;
        F.rotateCW180 = H;
        G.rotateCW180 = E;
        H.rotateCW180 = F;

        A.rotateCCW90 = D;
        B.rotateCCW90 = A;
        C.rotateCCW90 = B;
        D.rotateCCW90 = C;
        E.rotateCCW90 = H;
        F.rotateCCW90 = E;
        G.rotateCCW90 = F;
        H.rotateCCW90 = G;

        A.mirrorFB = E;
        B.mirrorFB = F;
        C.mirrorFB = G;
        D.mirrorFB = H;
        E.mirrorFB = A;
        F.mirrorFB = B;
        G.mirrorFB = C;
        H.mirrorFB = D;

        A.mirrorLR = G;
        B.mirrorLR = H;
        C.mirrorLR = E;
        D.mirrorLR = F;
        E.mirrorLR = C;
        F.mirrorLR = D;
        G.mirrorLR = A;
        H.mirrorLR = B;
    }

    private Rotation rotation;
    private Mirror mirror;
    private PlacementSettings rotateCW90;
    private PlacementSettings rotateCW180;
    private PlacementSettings rotateCCW90;
    /**
     * X mirror
     */
    private PlacementSettings mirrorFB;
    /**
     * Z mirror
     */
    private PlacementSettings mirrorLR;

    private PlacementSettings(final Rotation rotation, final Mirror mirror)
    {
        this.rotation = rotation;
        this.mirror = mirror;
    }

    public Rotation getRotation()
    {
        return rotation;
    }

    public Mirror getMirror()
    {
        return mirror;
    }

    public PlacementSettings rotate(final Rotation rotationIn)
    {
        switch (rotationIn)
        {
            case CLOCKWISE_90:
                return rotateCW90;

            case CLOCKWISE_180:
                return rotateCW180;

            case COUNTERCLOCKWISE_90:
                return rotateCCW90;

            case NONE:
            default:
                return this;
        }
    }

    public PlacementSettings mirror(final Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return mirrorLR;

            case FRONT_BACK:
                return mirrorFB;

            case NONE:
            default:
                return this;
        }
    }
}
