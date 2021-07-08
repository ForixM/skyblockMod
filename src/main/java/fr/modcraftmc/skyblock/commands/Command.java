package fr.modcraftmc.skyblock.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.commands.skyblock.Config;
import fr.modcraftmc.skyblock.commands.skyblock.Home;
import fr.modcraftmc.skyblock.commands.skyblock.weather.Clear;
import fr.modcraftmc.skyblock.commands.skyblock.weather.Rain;
import fr.modcraftmc.skyblock.network.PacketRequestTwoPoses;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import fr.modcraftmc.skyblock.schematic.SchemReader;
import fr.modcraftmc.skyblock.world.Region;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import java.io.*;

public class Command {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdWeather = dispatcher.register(Commands.literal("weather").then(Clear.register(dispatcher)).then(Rain.register(dispatcher)));
        LiteralCommandNode<CommandSource> cmdSkyblock = dispatcher.register(Commands.literal("skyblock")
                .then(Home.register(dispatcher))
                .then(Config.register(dispatcher))
                .then(cmdWeather)
                .then(Commands.literal("creator").executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                    ItemStack creator = new ItemStack(Items.GOLDEN_AXE);
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putString("owner", player.getDisplayName().getString());
                    creator.addTagElement("creator", nbt);
                    player.addItem(creator);
                    return 0;
                }))
                .then(Commands.literal("save").executes(ctx -> {
                    PacketHandler.INSTANCE.sendTo(new PacketRequestTwoPoses(), ctx.getSource().getPlayerOrException().connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    return 0;
                })));
        dispatcher.register(Commands.literal("skyblock").executes(ctx -> {
            if (ctx.getSource().getEntity() instanceof PlayerEntity){
                display((PlayerEntity) ctx.getSource().getEntity());
                return 0;
            } else return -1;
        }).redirect(cmdSkyblock));

        dispatcher.register(Commands.literal("connectiondatabase").requires(ctx -> ctx.hasPermission(4)).executes(ctx -> {
            SkyBlock.config.connect();
            return 0;
        }));

        dispatcher.register(Commands.literal("loaderr").requires(ctx -> ctx.hasPermission(4))
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
