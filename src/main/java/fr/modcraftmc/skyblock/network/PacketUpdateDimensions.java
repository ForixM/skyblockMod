package fr.modcraftmc.skyblock.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public class PacketUpdateDimensions implements PacketBasic {

    private final ResourceLocation worldId;
    private final boolean add;

    public PacketUpdateDimensions(ResourceLocation worldId, boolean add){
        this.worldId = worldId;
        this.add = add;
    }

    public PacketUpdateDimensions(PacketBuffer buf){
        this.worldId = buf.readResourceLocation();
        this.add = buf.readBoolean();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(worldId);
        buf.writeBoolean(add);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(this::handleUpdateDimensionsPacket);
        ctx.get().setPacketHandled(true);
    }

    private void handleUpdateDimensionsPacket()
    {
        @SuppressWarnings("resource")
        ClientPlayerEntity player = Minecraft.getInstance().player;
        RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, worldId);
        if (player == null || key == null)
            return;

        Set<RegistryKey<World>> worlds = player.connection.levels();
        if (worlds == null)
            return;

        if (add)
        {
            worlds.add(key);
        }
        else
        {
            worlds.remove(key);
        }
    }
}
