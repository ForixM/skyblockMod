package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.listeners.PlayerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChangedDimension implements PacketBasic{

    private final int islandSize;

    public PacketChangedDimension(int islandSize){
        this.islandSize = islandSize;
    }

    public PacketChangedDimension(PacketBuffer buf){
        this.islandSize = buf.readInt();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(islandSize);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> PlayerListener.islandSize = islandSize);
        ctx.get().setPacketHandled(true);
    }
}
