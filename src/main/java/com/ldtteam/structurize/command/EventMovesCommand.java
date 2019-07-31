package com.ldtteam.structurize.command;

import com.ldtteam.structurize.client.gui.WindowBuildTool;
import com.ldtteam.structurize.client.render.EventRenderer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

public class EventMovesCommand extends AbstractCommand
{
    private static final String POS_ARG = "movepos";

    protected static LiteralArgumentBuilder<CommandSource> build()
    {
        return newLiteral("renderEvents").then(newLiteral("moveCurrent").then(newArgument(POS_ARG, BlockPosArgument.blockPos()).executes(s -> move(s))))
            .then(newLiteral("rotateCWcurrent").executes(s -> rotateCW(s)))
            .then(newLiteral("rotateCCWcurrent").executes(s -> rotateCCW(s)))
            .then(newLiteral("mirrorCurrent").executes(s -> mirror(s)))
            .then(newLiteral("closeCurrent").executes(s -> close(s)))
            .then(newLiteral("closeAll").executes(s -> closeAll(s)))
            .then(newLiteral("resetLagStats").executes(s -> resetLS(s)));
    }

    private static int move(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        final BlockPos vec = BlockPosArgument.getBlockPos(command, POS_ARG);
        WindowBuildTool.getEvent().getPosition().moveBy(vec);
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

    private static int mirror(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.getEvent().getStructure().mirror();
        return 1;
    }

    private static int close(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        WindowBuildTool.close();
        return 1;
    }

    private static int closeAll(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        EventRenderer.cancelAllActiveEvents();
        return 1;
    }

    private static int resetLS(final CommandContext<CommandSource> command) throws CommandSyntaxException
    {
        EventRenderer.resetLagStats();
        return 1;
    }
}
