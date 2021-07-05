package fr.modcraftmc.skyblock.network;

import com.google.gson.JsonArray;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.Request;
import fr.modcraftmc.skyblock.util.Constants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetSpawn implements PacketBasic {

    String owner;

    public PacketSetSpawn(String owner){
        this.owner = owner;
    }

    public PacketSetSpawn(PacketBuffer buf){
        owner = buf.readUtf(Constants.MAX_STRING_SIZE);
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(owner);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player.getCommandSenderWorld().dimension().location().getPath().equalsIgnoreCase(owner)) {
                if (player.getDisplayName().getString().equals(owner)) {
                    JsonArray spawnLocation = new JsonArray();
                    spawnLocation.add(player.position().x);
                    spawnLocation.add(player.position().y());
                    spawnLocation.add(player.position().z());
                    SkyBlock.config.setSpawnLocation(owner, spawnLocation);
                } else if (SkyBlock.config.isOfficier(owner, player.getDisplayName().getString())) {
                    JsonArray spawnLocation = new JsonArray();
                    spawnLocation.add(player.position().x);
                    spawnLocation.add(player.position().y());
                    spawnLocation.add(player.position().z());
                    SkyBlock.config.setSpawnLocation(owner, spawnLocation);
                } else {
                    PacketHandler.INSTANCE.sendTo(new PacketOpenGUI(Request.ERROR, "How have you access to "+owner+" island panel config??", GuiCommand.EMPTY), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            } else {
                PacketHandler.INSTANCE.sendTo(new PacketOpenGUI(Request.ERROR, "You have to be in your island to update spawn location", GuiCommand.EMPTY), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
