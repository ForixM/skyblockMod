package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.listeners.PlayerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRequestTwoPoses implements PacketBasic {

    public PacketRequestTwoPoses(){

    }

    public PacketRequestTwoPoses(PacketBuffer buf){

    }

    @Override
    public void toBytes(PacketBuffer buf) {

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND){
                BlockPos pos1 = PlayerListener.pos1;
                BlockPos pos2 = PlayerListener.pos2;
                PacketHandler.INSTANCE.sendToServer(new PacketTwoPoses(pos1, pos2));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
