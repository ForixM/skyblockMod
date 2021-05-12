package fr.modcraftmc.skyblock.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.javafx.geom.Vec3d;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.database.Connector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockListeners {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockEvent.BreakEvent event){
        RegistryKey<World> world = event.getPlayer().getCommandSenderWorld().dimension();
        if (world.getRegistryName().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
            String owner = world.getRegistryName().getPath();
            JsonArray members = Connector.getMembers(owner);
            int size = SkyBlock.config.getIslandSize(world.getRegistryName().getPath());
            for (JsonElement member : members) {
                if (member.getAsString().equalsIgnoreCase(event.getPlayer().getDisplayName().getString())) {
                    if (isInside(size, event.getPos()))
                        return;
                }
            }
            if (owner.equalsIgnoreCase(event.getPlayer().getDisplayName().getString()) && isInside(size, event.getPos()))
                return;
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        RegistryKey<World> world = event.getEntity().getCommandSenderWorld().dimension();
        if (world.getRegistryName().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
            int size = SkyBlock.config.getIslandSize(world.getRegistryName().getPath());
            if (event.getEntity() instanceof PlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
                String owner = world.getRegistryName().getPath();
                JsonArray members = Connector.getMembers(owner);
                for (JsonElement member : members) {
                    if (member.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                        if (isInside(size, event.getPos()))
                            return;
                    }
                }
                if (owner.equalsIgnoreCase(player.getDisplayName().getString()) && isInside(size, event.getPos()))
                    return;
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            } else {
                if (isInside(size, event.getPos())){
                    return;
                }
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnMultiblockPlaced(BlockEvent.EntityMultiPlaceEvent event){
        RegistryKey<World> world = event.getEntity().getCommandSenderWorld().dimension();
        int size = SkyBlock.config.getIslandSize(world.getRegistryName().getPath());
        for (BlockSnapshot blockSnapshot : event.getReplacedBlockSnapshots()){
            if (!isInside(size, blockSnapshot.getPos())){
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerTick(TickEvent.ServerTickEvent event){
        if (event.phase == TickEvent.Phase.START)
            return;
        if (Connector.isConnected()) {
            for (ServerPlayerEntity player : SkyBlock.DEDICATED_SERVER.getPlayerList().getPlayers()) {
                if (player.getCommandSenderWorld().dimension().location().getNamespace().equalsIgnoreCase("modcraftsb")) {
                    PlayerListener.sendDimensionPacket(player);
                    BlockPos playerLocation = player.blockPosition();
                    int size = SkyBlock.config.getIslandSize(player.getCommandSenderWorld().dimension().location().getPath());
//                    System.out.println("size = " + size);
                    if (isInside(size, playerLocation))
                        return;
                    System.out.println("isn't inside");
                    Vector3d correctPlayerPosition = getCorrectPosition(player.position(), size);
                    player.teleportToWithTicket(correctPlayerPosition.x, correctPlayerPosition.y, correctPlayerPosition.z);
                }
            }
        }
    }

    private Vector3d getCorrectPosition(Vector3d pos, int worldSize){
        Vec3d vec3d = new Vec3d();

        if (Math.floor(Math.abs(pos.x)) >= worldSize/2){
            vec3d.x = worldSize/2+0.999;
            if (pos.x < 0)
                vec3d.x = vec3d.x*(-1)+1;
        } else {
            vec3d.x = pos.x;
        }

        if (Math.floor(Math.abs(pos.z)) >= worldSize/2){
            vec3d.z = worldSize/2+0.999;
            if (pos.z < 0)
                vec3d.z = vec3d.z*(-1)+1;
        } else {
            vec3d.z = pos.z;
        }
        return new Vector3d(vec3d.x, pos.y, vec3d.z);
    }

    private boolean isInside(int worldSize, BlockPos pos){
        return Math.abs(pos.getX()) <= worldSize / 2 && Math.abs(pos.getZ()) <= worldSize / 2;
    }
}
