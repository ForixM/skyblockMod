package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.SkyBlock;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static fr.modcraftmc.skyblock.util.Constants.MAX_STRING_SIZE;
public class PacketSetOwner implements PacketBasic {

    private String previousOwner;
    private String newOwner;

    public PacketSetOwner(String previousOwner, String newOwner) {
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    public PacketSetOwner(PacketBuffer buf){
        previousOwner = buf.readUtf(MAX_STRING_SIZE);
        newOwner = buf.readUtf(MAX_STRING_SIZE);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(previousOwner);
        buf.writeUtf(newOwner);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender().getDisplayName().getString().toLowerCase().equals(previousOwner));
                SkyBlock.config.setOwner(newOwner, previousOwner);
        });
        ctx.get().setPacketHandled(true);
    }
}
