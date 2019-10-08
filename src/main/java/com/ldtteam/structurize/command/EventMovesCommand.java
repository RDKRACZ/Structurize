package com.ldtteam.structurize.command;

import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.client.gui.WindowBuildTool;
import com.ldtteam.structurize.pipeline.build.RawPlacer;
import com.ldtteam.structurize.pipeline.defaults.build.InstantBuildProvider;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

/**
 * Temporary util class
 */
public class EventMovesCommand extends AbstractCommand
{
    private static final String POS_ARG = "movepos";

    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        return newLiteral("renderEvents")
            .then(newLiteral("moveCurrent").then(newArgument(POS_ARG, BlockPosArgument.blockPos()).executes(s -> move(s))))
            .then(newLiteral("rotateCWcurrent").executes(s -> rotateCW(s)))
            .then(newLiteral("rotateCCWcurrent").executes(s -> rotateCCW(s)))
            .then(newLiteral("mirrorXCurrent").executes(s -> mirrorX(s)))
            .then(newLiteral("mirrorZCurrent").executes(s -> mirrorZ(s)))
            .then(newLiteral("closeCurrent").executes(s -> close(s)))
            .then(newLiteral("closeAll").executes(s -> closeAll(s)))
            .then(newLiteral("placeCurrent").executes(s -> placeCurrent(s)))
            .then(newLiteral("stageCurrentBP").executes(s -> stageCurrentBP(s)));
    }

    private static int move(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        final BlockPos vec = BlockPosArgument.getBlockPos(command, POS_ARG);
        WindowBuildTool.getEvent().getPosition().moveBy(vec);
        WindowBuildTool.getEvent().getRenderer().recompile();
        return 1;
    }

    private static int rotateCW(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.getEvent().getStructure().rotateClockwise();
        return 1;
    }

    private static int rotateCCW(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.getEvent().getStructure().rotateCounterClockwise();
        return 1;
    }

    private static int mirrorX(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.getEvent().getStructure().mirrorX();
        return 1;
    }

    private static int mirrorZ(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.getEvent().getStructure().mirrorZ();
        return 1;
    }

    private static int close(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.closeAndCancel();
        return 1;
    }

    private static int closeAll(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        Instances.getEventRenderer().cancelAllActiveEvents();
        return 1;
    }

    private static int placeCurrent(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        try
        {
            new InstantBuildProvider().build(new RawPlacer(WindowBuildTool.getEvent()), false);
            WindowBuildTool.closeAndCancel();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }

    private static int stageCurrentBP(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        try
        {
            InstantBuildProvider.runStage();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }
}
