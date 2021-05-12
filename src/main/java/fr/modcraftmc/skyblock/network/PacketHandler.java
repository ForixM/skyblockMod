package fr.modcraftmc.skyblock.network;


import fr.modcraftmc.skyblock.SkyBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    private static int Id = 0;

    private static int nextId(){
        return Id++;
    }

    public static void registerMessages(){
        INSTANCE =  NetworkRegistry.newSimpleChannel(
                new ResourceLocation(SkyBlock.MOD_ID, "caelestibus"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        INSTANCE.registerMessage(nextId(), PacketOpenGUI.class, PacketOpenGUI::toBytes, PacketOpenGUI::new, PacketOpenGUI::handle);
        INSTANCE.registerMessage(nextId(), PacketSetOwner.class, PacketSetOwner::toBytes, PacketSetOwner::new, PacketSetOwner::handle);
        INSTANCE.registerMessage(nextId(), PacketModifyIslandInfo.class, PacketModifyIslandInfo::toBytes, PacketModifyIslandInfo::new, PacketModifyIslandInfo::handle);
        INSTANCE.registerMessage(nextId(), PacketSetSpawn.class, PacketSetSpawn::toBytes, PacketSetSpawn::new, PacketSetSpawn::handle);
        INSTANCE.registerMessage(nextId(), PacketUpdateDimensions.class, PacketUpdateDimensions::toBytes, PacketUpdateDimensions::new, PacketUpdateDimensions::handle);
        INSTANCE.registerMessage(nextId(), PacketChangedDimension.class, PacketChangedDimension::toBytes, PacketChangedDimension::new, PacketChangedDimension::handle);
        INSTANCE.registerMessage(nextId(), PacketSetPublicIsland.class, PacketSetPublicIsland::toBytes, PacketSetPublicIsland::new, PacketSetPublicIsland::handle);
        INSTANCE.registerMessage(nextId(), PacketModifyPermission.class, PacketModifyPermission::toBytes, PacketModifyPermission::new, PacketModifyPermission::handle);
    }
}
