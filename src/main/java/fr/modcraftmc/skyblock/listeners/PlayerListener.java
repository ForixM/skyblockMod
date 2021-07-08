package fr.modcraftmc.skyblock.listeners;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.database.Connector;
import fr.modcraftmc.skyblock.network.PacketChangedDimension;
import fr.modcraftmc.skyblock.network.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;

public class PlayerListener {

    public static int islandSize = -1;

    @OnlyIn(Dist.CLIENT)
    public static BlockPos pos1, pos2;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onItemRightClick(PlayerInteractEvent.RightClickItem event){
        PlayerEntity player = event.getPlayer();
        Vector3d blockVector = Minecraft.getInstance().hitResult.getLocation();
        double bX = blockVector.x; double bY = blockVector.y(); double bZ = blockVector.z();
        double pX = Minecraft.getInstance().player.getX(); double pY = Minecraft.getInstance().player.getY(); double pZ = Minecraft.getInstance().player.getZ();

        if(bX == Math.floor(bX) && bX <= pX){bX--;}
        if(bY == Math.floor(bY) && bY <= pY+1){bY--;} // +1 on Y to get y from player eyes instead of feet
        if(bZ == Math.floor(bZ) && bZ <= pZ){bZ--;}

        BlockState block = Minecraft.getInstance().level.getBlockState(new BlockPos(bX, bY, bZ));
        if (player.hasPermissions(4)) {
            if (player.isCrouching() && block.getBlock().equals(Blocks.AIR)) {
                ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
                if (heldItem.getItem().equals(Items.GOLDEN_AXE) && heldItem.getTagElement("creator") != null){
                    pos1 = null;
                    pos2 = null;
                    player.sendMessage(new StringTextComponent("Both positions have been reset"), player.getUUID());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerInteractRightClick(PlayerInteractEvent.RightClickBlock event){
        PlayerEntity player = event.getPlayer();
        if (player.hasPermissions(4)) {
            if (event.getHand() == Hand.MAIN_HAND) {
                ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
                if (heldItem.getItem().equals(Items.GOLDEN_AXE)) {
                    if (heldItem.getTagElement("creator") != null) {
                        pos2 = event.getPos();
                        if (pos1 != null) {
                            player.sendMessage(new StringTextComponent("Now, please execute \"/skyblock save\" to save your island region schematic"), player.getUUID());
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerInteractLeftClick(PlayerInteractEvent.LeftClickBlock event){
        PlayerEntity player = event.getPlayer();
        System.out.println("test");
        if (player.hasPermissions(4)) {
            ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
            if (heldItem.getItem().equals(Items.GOLDEN_AXE)) {
                if (heldItem.getTagElement("creator") != null) {
                    pos1 = event.getPos();
                    if (pos2 != null){
                        player.sendMessage(new StringTextComponent("Now, please execute \"/skyblock save\" to save your island region schematic"), player.getUUID());
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) {
            if (pos1 != null & pos2 != null){
                for (int x = Math.min(pos1.getX(), pos2.getX()); x <= Math.max(pos1.getX(), pos2.getX())+1; x++){
                    for (int y = Math.min(pos1.getY(), pos2.getY()); y <= Math.max(pos1.getY(), pos2.getY())+1; y++){
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, x, y, Math.min(pos1.getZ(), pos2.getZ()), 0, 0, 0);
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, x, y, Math.max(pos1.getZ(), pos2.getZ())+1, 0, 0, 0);
                    }
                }
                for (int z = Math.min(pos1.getZ(), pos2.getZ()); z <= Math.max(pos1.getZ(), pos2.getZ()); z++){
                    for (int y = Math.min(pos1.getY(), pos2.getY()); y <= Math.max(pos1.getY(), pos2.getY())+1; y++){
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, Math.min(pos1.getX(), pos2.getX()), y, z, 0, 0, 0);
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, Math.max(pos1.getX(), pos2.getX())+1, y, z, 0, 0, 0);
                    }
                }
            }

            if (islandSize != -1) {
                for (int x = -(islandSize / 2); x < islandSize / 2 + 1; x += 2) {
                    for (int i = -2; i <= 2; i += 2) {
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, x, Math.floor(event.player.getY()) + i, islandSize / 2 + 1, 0, 0, 0);
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, x, Math.floor(event.player.getY()) + i, -(islandSize / 2), 0, 0, 0);
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, islandSize / 2 + 1, Math.floor(event.player.getY()) + i, x, 0, 0, 0);
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, -(islandSize / 2), Math.floor(event.player.getY()) + i, x, 0, 0, 0);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        sendDimensionPacket((ServerPlayerEntity) event.getPlayer());
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onPlayerConnected(PlayerEvent.PlayerLoggedInEvent event){
        if (!Connector.isConnected()) {
            ((ServerPlayerEntity)event.getPlayer()).connection.disconnect(new StringTextComponent("Database not connected"));
            return;
        }

        if (SkyBlock.config.isNewPlayer(event.getPlayer().getDisplayName().getString().toLowerCase())) {
            SkyBlock.config.newPlayer(event.getPlayer().getDisplayName().getString().toLowerCase());
            SkyBlock.LOGGER.info("New player added to database");
        }
        sendDimensionPacket((ServerPlayerEntity) event.getPlayer());
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event){
        String playerName = event.getPlayer().getDisplayName().getString().toLowerCase();
        if (SkyBlock.config.haveIsland(playerName)){
            Vector3d spawnLocation = SkyBlock.config.getSpawnLocation(playerName);
            event.getPlayer().teleportToWithTicket(spawnLocation.x, spawnLocation.y, spawnLocation.z);
        }
        sendDimensionPacket((ServerPlayerEntity) event.getPlayer());
    }

//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public void onServerTick(TickEvent.ServerTickEvent event){
//        if (event.phase == TickEvent.Phase.START)
//            return;
//        if (Connector.isConnected()) {
//            for (ServerPlayerEntity player : SkyBlock.DEDICATED_SERVER.getPlayerList().getPlayers()) {
//                if (player.getCommandSenderWorld().getDimensionKey().getLocation().getNamespace().equalsIgnoreCase("modcraftsb")) {
//                    sendDimensionPacket(player);
//                }
//            }
//        }
//    }

    public static void sendDimensionPacket(ServerPlayerEntity player){
        if (player.getCommandSenderWorld().dimension().location().getNamespace().equalsIgnoreCase(SkyBlock.MOD_ID)){
            PacketHandler.INSTANCE.sendTo(new PacketChangedDimension(SkyBlock.config.getIslandSize(player.getCommandSenderWorld().dimension().location().getPath())), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        } else
            PacketHandler.INSTANCE.sendTo(new PacketChangedDimension(-1), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
