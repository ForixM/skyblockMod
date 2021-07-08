package fr.modcraftmc.skyblock.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.database.Connector;
import fr.modcraftmc.skyblock.util.Vector3d;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Predicate;

public class BlockListeners {

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockInteraction(BlockEvent.BlockToolInteractEvent event){
        PlayerEntity player = event.getPlayer();
        if (player.hasPermissions(4))
            return;

        RegistryKey<World> world = player.getCommandSenderWorld().dimension();
        if (world.location().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
            String owner = world.location().getPath();
            JsonArray members = Connector.getMembers(owner);
            JsonArray officers = Connector.getOfficers(owner);
            int size = SkyBlock.config.getIslandSize(world.location().getPath());
            if (officers != null) {
                for (JsonElement officer : officers) {
                    if (officer.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                        if (isInside(size, event.getPos()))
                            return;
                    }
                }
            }
            if (members != null) {
                for (JsonElement member : members) {
                    if (member.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                        if (isInside(size, event.getPos()))
                            return;
                    }
                }
            }
            if (owner.equalsIgnoreCase(player.getDisplayName().getString()) && isInside(size, event.getPos()))
                return;
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockEvent.BreakEvent event){
        PlayerEntity player = event.getPlayer();
        if (player.getItemInHand(Hand.MAIN_HAND).getTagElement("creator") != null && player.hasPermissions(4)){
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
            return;
        }

        if (player.hasPermissions(4))
            return;
        
        RegistryKey<World> world = player.getCommandSenderWorld().dimension();
        if (world.location().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
            String owner = world.location().getPath();
            JsonArray members = Connector.getMembers(owner);
            JsonArray officers = Connector.getOfficers(owner);
            int size = SkyBlock.config.getIslandSize(world.location().getPath());
            if (officers != null) {
                for (JsonElement officer : officers) {
                    if (officer.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                        if (isInside(size, event.getPos()))
                            return;
                    }
                }
            }
            if (members != null) {
                for (JsonElement member : members) {
                    if (member.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                        if (isInside(size, event.getPos()))
                            return;
                    }
                }
            }
            if (owner.equalsIgnoreCase(player.getDisplayName().getString()) && isInside(size, event.getPos()))
                return;
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        RegistryKey<World> world = event.getEntity().getCommandSenderWorld().dimension();
        if (world.location().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
            int size = SkyBlock.config.getIslandSize(world.location().getPath());
            if (event.getEntity() instanceof PlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
                if (player.hasPermissions(4))
                    return;
                String owner = world.location().getPath();
                JsonArray members = Connector.getMembers(owner);
                JsonArray officers = Connector.getOfficers(owner);
                if (officers != null){
                    for (JsonElement officer : officers) {
                        if (officer.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                            if (isInside(size, event.getPos()))
                                return;
                        }
                    }
                }
                if (members != null) {
                    for (JsonElement member : members) {
                        if (member.getAsString().equalsIgnoreCase(player.getDisplayName().getString())) {
                            if (isInside(size, event.getPos()))
                                return;
                        }
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

    @SubscribeEvent
    public void blockMoved(PistonEvent.Pre event){
        ServerWorld serverWorld = (ServerWorld) event.getWorld();
        RegistryKey<World> world = serverWorld.dimension();
        if (world.location().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
            int size = SkyBlock.config.getIslandSize(world.location().getPath());
            Direction dir = event.getDirection();
            BlockPos pos = event.getPos();
            switch (event.getDirection().getAxis()) {
                case X:
                    if (isInside(size, new BlockPos(pos.getX() + dir.getAxisDirection().getStep() * 2, pos.getY(), pos.getZ())))
                        return;
                    event.setCanceled(true);
                case Z:
                    if (isInside(size, new BlockPos(pos.getX(), pos.getY(), pos.getZ() + dir.getAxisDirection().getStep() * 2)))
                        return;
                    event.setCanceled(true);
                case Y:
                    return;
                default:
                    throw new Error("Someone's been tampering with the universe! it's minecraft who want to say that not me (refer to 'Direction' class)");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnMultiblockPlaced(BlockEvent.EntityMultiPlaceEvent event){
        RegistryKey<World> world = event.getEntity().getCommandSenderWorld().dimension();
        int size = SkyBlock.config.getIslandSize(world.location().getPath());
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
                if (player.getCommandSenderWorld().dimension().location().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)) {
                    PlayerListener.sendDimensionPacket(player);
                    BlockPos playerLocation = player.blockPosition();
                    int size = SkyBlock.config.getIslandSize(player.getCommandSenderWorld().dimension().location().getPath());
                    if (isInside(size, playerLocation))
                        return;
                    Vector3d correctPlayerPosition = getCorrectPosition(new Vector3d(player.getX(), player.getY(), player.getZ()), size);
                    player.teleportToWithTicket(correctPlayerPosition.x, correctPlayerPosition.y, correctPlayerPosition.z);
                }
            }
        }
    }

    private Vector3d getCorrectPosition(Vector3d pos, int worldSize){
        Vector3d vec3d = new Vector3d(0,0,0);

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
