package fr.modcraftmc.skyblock.commands.skyblock;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.modcraftmc.skyblock.commands.skyblock.config.Addmember;
import fr.modcraftmc.skyblock.commands.skyblock.config.Removemember;
import fr.modcraftmc.skyblock.commands.skyblock.config.Setspawn;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.NetworkDirection;

public class Config implements Command<CommandSource> {

    private static Config CMD = new Config();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher){
        return Commands.literal("config").executes(CMD)
                .then(Addmember.register(dispatcher))
                .then(Removemember.register(dispatcher))
                .then(Setspawn.register(dispatcher));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        try {
            PacketHandler.INSTANCE.sendTo(new PacketOpenGUI(Request.CONFIGURATION, null, GuiCommand.EMPTY), context.getSource().getPlayerOrException().connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
