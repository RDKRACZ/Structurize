package com.ldtteam.structurize.command;

import com.ldtteam.structurize.Instances;
import com.ldtteam.structurize.client.gui.WindowBuildTool;
import com.ldtteam.structurize.network.messages.TestMessage;
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
        return newLiteral("buildtoolevent").then(newLiteral("move").then(newArgument(POS_ARG, BlockPosArgument.blockPos()).executes(s -> move(s))))
            .then(newLiteral("rotateCW").executes(s -> rotateCW(s)))
            .then(newLiteral("rotateCCW").executes(s -> rotateCCW(s)))
            .then(newLiteral("mirrorX").executes(s -> mirrorX(s)))
            .then(newLiteral("mirrorZ").executes(s -> mirrorZ(s)))
            .then(newLiteral("close").executes(s -> close(s)));
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
        WindowBuildTool.close();
        return 1;
    }
}
