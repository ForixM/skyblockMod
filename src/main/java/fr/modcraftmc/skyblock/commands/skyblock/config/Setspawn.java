package fr.modcraftmc.skyblock.commands.skyblock.config;

import com.google.gson.JsonArray;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.modcraftmc.skyblock.SkyBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class Setspawn implements Command<CommandSource> {

    private static Setspawn CMD = new Setspawn();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher){
        return Commands.literal("setspawn")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        if (ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
            if (SkyBlock.config.haveIsland(ctx.getSource().getEntity().getDisplayName().getString().toLowerCase())) {
                JsonArray spawnLocation = new JsonArray();
                spawnLocation.add(player.getX());
                spawnLocation.add(player.getY());
                spawnLocation.add(player.getZ());
                SkyBlock.config.setSpawnLocation(player.getDisplayName().getString().toLowerCase(), spawnLocation);
            } else {
                player.sendMessage(new StringTextComponent("Vous n'avez pas d'île !"), player.getUUID());
            }
        } else
            SkyBlock.LOGGER.info("Only players can execute this command");
        return 0;
    }
}
