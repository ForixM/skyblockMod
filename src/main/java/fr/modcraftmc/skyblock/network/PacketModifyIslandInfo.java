package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static fr.modcraftmc.skyblock.util.Constants.MAX_STRING_SIZE;
public class PacketModifyIslandInfo implements PacketBasic {

    private String name;
    private String description;
    private String owner;

    public PacketModifyIslandInfo(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public PacketModifyIslandInfo(PacketBuffer buf){
        this.name = buf.readUtf(MAX_STRING_SIZE);
        this.description = buf.readUtf(MAX_STRING_SIZE);
        this.owner = buf.readUtf(MAX_STRING_SIZE);
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(name);
        buf.writeUtf(description);
        buf.writeUtf(owner);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getNetworkManager().getDirection() == PacketDirection.SERVERBOUND) {
                String pseudo = ctx.get().getSender().getDisplayName().getString().toLowerCase();
                if (pseudo.equalsIgnoreCase(owner)){
                    SkyBlock.config.setName(pseudo, name);
                    SkyBlock.config.setDescription(pseudo, description);
                    System.out.println("Is owner");
                } else {
                    if (SkyBlock.config.isOfficier(owner, pseudo)){
                        SkyBlock.config.setName(owner, name);
                        SkyBlock.config.setDescription(owner, description);
                        System.out.println("is officer");
                    } else
                        System.out.println("isn't officer");
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
