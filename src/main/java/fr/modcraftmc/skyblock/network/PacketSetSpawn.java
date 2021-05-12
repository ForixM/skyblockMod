package fr.modcraftmc.skyblock.network;

import com.google.gson.JsonArray;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.util.Constants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
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
            if (player.getDisplayName().getString().equals(owner)) {
                JsonArray spawnLocation = new JsonArray();
                spawnLocation.add(player.position().x);
                spawnLocation.add(player.position().y());
                spawnLocation.add(player.position().z());
                SkyBlock.config.setSpawnLocation(owner, spawnLocation);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
