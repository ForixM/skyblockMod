package fr.modcraftmc.skyblock.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.commands.skyblock.Config;
import fr.modcraftmc.skyblock.commands.skyblock.Home;
import fr.modcraftmc.skyblock.commands.skyblock.weather.Clear;
import fr.modcraftmc.skyblock.commands.skyblock.weather.Rain;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import fr.modcraftmc.skyblock.schematic.SchemReader;
import fr.modcraftmc.skyblock.world.IslandSaver;
import fr.modcraftmc.skyblock.world.Region;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import java.io.*;

public class Command {

    private static IslandSaver saver;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdWeather = dispatcher.register(Commands.literal("weather").then(Clear.register(dispatcher)).then(Rain.register(dispatcher)));
        LiteralCommandNode<CommandSource> cmdSkyblock = dispatcher.register(Commands.literal("skyblock").then(Home.register(dispatcher)).then(Config.register(dispatcher)).then(cmdWeather));
        dispatcher.register(Commands.literal("skyblock").executes(ctx -> {
            if (ctx.getSource().getEntity() instanceof PlayerEntity){
                display((PlayerEntity) ctx.getSource().getEntity());
                return 0;
            } else return -1;
        }).redirect(cmdSkyblock));

        dispatcher.register(Commands.literal("connectiondatabase").executes(ctx -> {
            SkyBlock.config.connect();
            return 0;
        }));

        LiteralCommandNode<CommandSource> cmdTut = dispatcher.register(Commands.literal("bonjour").then(CommandSkyblock.register(dispatcher)));
        dispatcher.register(Commands.literal("vingt").redirect(cmdTut));

        dispatcher.register(Commands.literal("firstpos").executes(ctx -> registerFirstPos(ctx)));
        dispatcher.register(Commands.literal("secondpos").executes(ctx -> registerSecondPos(ctx)));
        dispatcher.register(Commands.literal("loaderr")
                .executes(ctx -> {
                    try {
                        Region region = SchemReader.readFromFile(new File(SkyBlock.CONFIG_DIR.toString() + "/island.schematic"));
                        World world = ctx.getSource().getLevel();
                        PlayerEntity player = (PlayerEntity) ctx.getSource().getEntity();
                        for (int x = 0; x < region.getxSize(); x++) {
                            for (int y = 0; y < region.getySize(); y++) {
                                for (int z = 0; z < region.getzSize(); z++) {
                                    BlockPos pos = new BlockPos(player.getX() + x, player.getY() + y, player.getZ() + z);
                                    world.setBlock(pos, region.getBlockStateAt(x, y, z), 2);
                                }
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }));
    }

    private static int registerFirstPos(CommandContext<CommandSource> context){
        if (context.getSource().getEntity() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) context.getSource().getEntity();
            World world = player.getCommandSenderWorld();
            BlockPos block = findBlockPos(world, player);
            if (world.getBlockState(block).getBlock() != Blocks.AIR){
                try {
                    saver = new IslandSaver();
                    saver.setFirstPos(block);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    private static BlockPos findBlockPos(World world, PlayerEntity player){
        Vector3d start = new Vector3d(player.getX(), player.getY()+player.getEyeHeight(), player.getZ());
        Vector3d end = start.add(player.getLookAngle().normalize().scale(6));
        return world.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player)).getBlockPos();
    }

    private static int registerSecondPos(CommandContext<CommandSource> context){
        if (context.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) context.getSource().getEntity();
            World world = player.getCommandSenderWorld();
            BlockPos block = findBlockPos(world, player);
            if (world.getBlockState(block).getBlock() != Blocks.AIR){
                saver.setSecondPos(block);
            } else
                return -1;
            return 0;
        } else
            return -1;
    }

    private static int display(PlayerEntity player){
        try {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            PacketHandler.INSTANCE.sendTo(new PacketOpenGUI(Request.MAIN, String.valueOf(SkyBlock.config.haveIsland(serverPlayerEntity.getDisplayName().getString().toLowerCase())), GuiCommand.EMPTY), serverPlayerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
