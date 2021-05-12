package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static fr.modcraftmc.skyblock.util.Constants.MAX_STRING_SIZE;

public class PacketSetPublicIsland implements PacketBasic{

    private boolean open;
    private String owner;

    public PacketSetPublicIsland(boolean open, String owner){
        this.open = open;
        this.owner = owner;
    }

    public PacketSetPublicIsland(PacketBuffer buf){
        this.open = buf.readBoolean();
        this.owner = buf.readUtf(MAX_STRING_SIZE);
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(open);
        buf.writeUtf(owner);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            String pseudo = ctx.get().getSender().getDisplayName().getString();
            if (pseudo.equals(owner)){
                SkyBlock.config.setPublic(pseudo, open);
            } else {
                if (SkyBlock.config.isOfficier(owner, pseudo))
                    SkyBlock.config.setPublic(owner, open);
                else
                    PacketHandler.INSTANCE.sendTo(new PacketOpenGUI(Request.ERROR, "Tu ne peux pas modifier cette configuration", GuiCommand.EMPTY), ctx.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
