package fr.modcraftmc.skyblock.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface PacketBasic {

    void toBytes(PacketBuffer buf);

    void handle(Supplier<NetworkEvent.Context> ctx);

}
