package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.PermissionSelector;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static fr.modcraftmc.skyblock.util.Constants.MAX_STRING_SIZE;

public class PacketModifyPermission implements PacketBasic{

    private PermissionSelector selector;
    private String pseudo;
    private String owner;

    public PacketModifyPermission(PermissionSelector selector, String pseudo, String owner){
        this.selector = selector;
        this.pseudo = pseudo;
        this.owner = owner;
    }

    public PacketModifyPermission(PacketBuffer buf){
        this.selector = buf.readEnum(PermissionSelector.class);
        this.pseudo = buf.readUtf(MAX_STRING_SIZE);
        this.owner = buf.readUtf(MAX_STRING_SIZE);
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeEnum(selector);
        buf.writeUtf(pseudo);
        buf.writeUtf(owner);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            String pseudo = ctx.get().getSender().getDisplayName().getString();
            if (pseudo.equals(owner)){
                modifyPermission(owner);
            } else {
                if (SkyBlock.config.isOfficier(owner, pseudo)){
                    modifyPermission(owner);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void modifyPermission(String owner){
        switch (selector) {
            case NEUTRAL:
                SkyBlock.config.setNeutral(owner, pseudo);
                break;
            case ADDGUEST:
                SkyBlock.config.addGuest(owner, pseudo);
                break;
            case ADDMEMBER:
                SkyBlock.config.addMember(owner, pseudo);
                break;
            case ADDOFFICIER:
                SkyBlock.config.addOfficier(owner, pseudo);
                break;
            case BAN:
                SkyBlock.config.addBan(owner, pseudo);
                break;
        }
    }
}
