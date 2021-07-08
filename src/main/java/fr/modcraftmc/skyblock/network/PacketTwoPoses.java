package fr.modcraftmc.skyblock.network;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.schematic.SchemReader;
import fr.modcraftmc.skyblock.world.Region;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.File;
import java.util.function.Supplier;

public class PacketTwoPoses implements PacketBasic {

    private BlockPos pos1, pos2;

    public PacketTwoPoses(BlockPos pos1, BlockPos pos2){
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public PacketTwoPoses(PacketBuffer buf){
        this.pos1 = buf.readBlockPos();
        this.pos2 = buf.readBlockPos();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos1);
        buf.writeBlockPos(pos2);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getNetworkManager().getDirection() == PacketDirection.SERVERBOUND){
                ServerPlayerEntity player = ctx.get().getSender();
                if (player.hasPermissions(4)){
                    Region region = new Region(pos1, pos2, ctx.get().getSender().getCommandSenderWorld());
                    if (!SkyBlock.CONFIG_DIR.exists())
                        SkyBlock.CONFIG_DIR.mkdir();
                    File schematic = new File(SkyBlock.CONFIG_DIR.toString()+"/island.schematic");
                    if (!schematic.getParentFile().exists())
                        schematic.mkdir();
                    SchemReader.writeRegionToFile(region, schematic);
                    player.sendMessage(new StringTextComponent("Region written to file"), player.getUUID());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
