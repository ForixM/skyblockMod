package fr.modcraftmc.skyblock.listeners;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.database.Connector;
import fr.modcraftmc.skyblock.network.PacketChangedDimension;
import fr.modcraftmc.skyblock.network.PacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class PlayerListener {

    public static int islandSize = -1;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) {
            if (islandSize != -1) {
                System.out.println("islandSize = " + islandSize);
                for (int x = -(islandSize / 2); x < islandSize / 2 + 1; x += 2) {
                    for (int i = -2; i <= 2; i += 2) {
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, x, Math.floor(event.player.getY()) + i, islandSize / 2 + 1, 0, 0, 0);
                    }
                }
                for (int x = -(islandSize / 2); x < islandSize / 2 + 1; x += 2) {
                    for (int i = -2; i <= 2; i += 2) {
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, x, Math.floor(event.player.getY()) + i, -(islandSize / 2), 0, 0, 0);
                    }
                }
                for (int x = -(islandSize / 2); x < islandSize / 2 + 1; x += 2) {
                    for (int i = -2; i <= 2; i += 2) {
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, islandSize / 2 + 1, Math.floor(event.player.getY()) + i, x, 0, 0, 0);
                    }
                }
                for (int x = -(islandSize / 2); x < islandSize / 2 + 1; x += 2) {
                    for (int i = -2; i <= 2; i += 2) {
                        event.player.getCommandSenderWorld().addParticle(ParticleTypes.EFFECT, -(islandSize / 2), event.player.getY() + i, x, 0, 0, 0);
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
