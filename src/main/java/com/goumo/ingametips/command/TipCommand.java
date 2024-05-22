package com.goumo.ingametips.command;

import com.goumo.ingametips.network.Networking;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TipCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ingametips").requires((c) -> c.hasPermission(2)).then(
                Commands.literal("add").then(
                    Commands.argument("targets", EntityArgument.players()).then(
                    Commands.argument("id", ResourceLocationArgument.id())
                        .executes((a) -> {
                            ResourceLocation id = ResourceLocationArgument.getId(a, "id");
                            int i = 0;

                            for(ServerPlayer sp : EntityArgument.getPlayers(a, "targets")) {
                                Networking.send(sp, id);
                                i++;
                            }

                            return i;
                        })))).then(
                Commands.literal("custom").then(
                    Commands.argument("targets", EntityArgument.players()).then(
                    Commands.argument("id", StringArgumentType.word()).then(
                    Commands.argument("visible_time", IntegerArgumentType.integer()).then(
                    Commands.argument("history", BoolArgumentType.bool()).then(
                    Commands.argument("title", StringArgumentType.string()).then(
                    Commands.argument("content", ComponentArgument.textComponent()).executes(TipCommand::sendCustom))))))
                ))
        );
    }

    private static int sendCustom(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "id");
        int visibleTime = IntegerArgumentType.getInteger(ctx, "visible_time");
        boolean history = BoolArgumentType.getBool(ctx, "history");
        Component title = Component.literal(StringArgumentType.getString(ctx, "title"));
        Component content = ComponentArgument.getComponent(ctx, "content");

        int i = 0;
        for(ServerPlayer sp : EntityArgument.getPlayers(ctx, "targets")) {
            Networking.sendCustom(sp, id, title, content, visibleTime, history);
            i++;
        }

        return i;
    }
}
