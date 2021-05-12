package fr.modcraftmc.skyblock.commands.skyblock;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Lifecycle;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketUpdateDimensions;
import fr.modcraftmc.skyblock.schematic.SchemReader;
import fr.modcraftmc.skyblock.world.Region;
import fr.modcraftmc.skyblock.world.SkyblockChunkGenerator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Home implements Command<CommandSource> {

    private static Home CMD = new Home();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher){
        return Commands.literal("home")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD).then(Commands.argument("pseudo", StringArgumentType.string()).executes(Home::teleportToPlayerSkyblock));
    }

    private static int teleportToPlayerSkyblock(CommandContext<CommandSource> ctx){
        if (ctx.getSource().getEntity() instanceof PlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
            String destination = StringArgumentType.getString(ctx, "pseudo");

            try {
                JsonArray members = SkyBlock.config.getMembers(destination.toLowerCase());
                for (JsonElement element : members){
                    if (element.getAsString().equalsIgnoreCase(player.getDisplayName().getString())){
                        if (SkyBlock.config.haveIsland(destination.toLowerCase())) {
                            RegistryKey<World> skyblock_world = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(SkyBlock.MOD_ID, destination.toLowerCase()));
                            Map<RegistryKey<World>, ServerWorld> map = SkyBlock.DEDICATED_SERVER.forgeGetWorldMap();

                            ServerWorld serverWorld = map.get(skyblock_world);
                            if (serverWorld == null)
                                serverWorld = createAndRegisterWorldAndDimension(SkyBlock.DEDICATED_SERVER, map, skyblock_world, Home::createDimension);
                            try {
                                player.changeDimension(serverWorld, new ITeleporter() {
                                    @Override
                                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                        Entity repositionedEntity = repositionEntity.apply(false);
                                        repositionedEntity.setPos(player.getX(), player.getY(), player.getZ());
                                        try {
                                            Vector3d location = SkyBlock.config.getSpawnLocation(destination.toLowerCase());
                                            player.teleportToWithTicket(location.x(), location.y(), location.z());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return repositionedEntity;
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                return -1;
                            }
                        }
                        player.sendMessage(new StringTextComponent("Tu as été téléporté à l'île de: " + destination), player.getUUID());
                        return 0;
                    }
                }
                if (SkyBlock.config.isPublic(destination) && !SkyBlock.config.isBanned(destination, player.getDisplayName().getString())){
                    RegistryKey<World> skyblock_world = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(SkyBlock.MOD_ID, destination.toLowerCase()));
                    Map<RegistryKey<World>, ServerWorld> map = SkyBlock.DEDICATED_SERVER.forgeGetWorldMap();

                    ServerWorld serverWorld = map.get(skyblock_world);
                    if (serverWorld == null)
                        serverWorld = createAndRegisterWorldAndDimension(SkyBlock.DEDICATED_SERVER, map, skyblock_world, Home::createDimension);
                    try {
                        player.changeDimension(serverWorld, new ITeleporter() {
                            @Override
                            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                Entity repositionedEntity = repositionEntity.apply(false);
                                repositionedEntity.setPos(player.getX(), player.getY(), player.getZ());
                                try {
                                    Vector3d location = SkyBlock.config.getSpawnLocation(destination.toLowerCase());
                                    player.teleportToWithTicket(location.x(), location.y(), location.z());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return repositionedEntity;
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        return -1;
                    }
                }
                player.sendMessage(new StringTextComponent("Tu n'as pas acces a cette ile"), player.getUUID());

            } catch (NullPointerException e){
                player.sendMessage(new StringTextComponent("Cette personne ne possède pas d'île"), player.getUUID());
            }
        } else {
            SkyBlock.LOGGER.info("Va rêver sale fou");
        }
        return 0;
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        PlayerEntity playerEntity = null;
        if (context.getSource().getEntity() instanceof PlayerEntity) {
            playerEntity = (PlayerEntity) context.getSource().getEntity();
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerEntity;
            if (SchemReader.islandReferenceExist(new File(SkyBlock.CONFIG_DIR+"/island.schematic"))) {
                boolean spawnIsland = !SkyBlock.config.haveIsland(playerEntity.getDisplayName().getString().toLowerCase());

                RegistryKey<World> skyblock_world = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(SkyBlock.MOD_ID, serverPlayerEntity.getDisplayName().getString().toLowerCase()));
                ServerWorld serverWorld;

                Map<RegistryKey<World>, ServerWorld> map = SkyBlock.DEDICATED_SERVER.forgeGetWorldMap();
                if (map.containsKey(skyblock_world)) {
                    serverWorld = map.get(skyblock_world);
                } else {
                    SkyBlock.LOGGER.info("Registring new dimension");
                    serverWorld = createAndRegisterWorldAndDimension(SkyBlock.DEDICATED_SERVER, map, skyblock_world, Home::createDimension);
                }

                boolean finalSpawnIsland = spawnIsland;
                serverPlayerEntity.changeDimension(serverWorld, new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        Entity repositionedEntity = repositionEntity.apply(false);
                        //@TODO not sure for setPos because before that was setPosAndUpdate
                        repositionedEntity.setPos(serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ());
                        if (finalSpawnIsland) {
                            try {
                                buildIsland(destWorld);
                                SkyBlock.config.createIsland(entity.getDisplayName().getString().toLowerCase());
                                SkyBlock.LOGGER.info("Island builded");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Vector3d location = SkyBlock.config.getSpawnLocation(entity.getDisplayName().getString().toLowerCase());
                            serverPlayerEntity.teleportToWithTicket(location.x(), location.y(), location.z());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return repositionedEntity;
                    }
                });
            } else {
                serverPlayerEntity.sendMessage(new StringTextComponent("Reference island doesn't exist. Please contact an Administrator."), serverPlayerEntity.getUUID());
            }
        }
        return 0;
    }

    public static Dimension createDimension(MinecraftServer server, RegistryKey<Dimension> key){
        return new Dimension(() -> getDimensionType(server), new SkyblockChunkGenerator(server));
    }

    public static DimensionType getDimensionType(MinecraftServer server)
    {
        return server.registryAccess() // get dynamic registries
                .dimensionTypes().getOrThrow(SkyBlock.DIMENSION_TYPE_KEY);
                //.getOrThrow(SkyBlock.DIMENSION_TYPE_KEY);
    }

    private static void buildIsland(World world){
        Region region = SchemReader.readFromFile(new File(SkyBlock.CONFIG_DIR+"/island.schematic"));
        for (int x = 0; x < region.getxSize(); x++) {
            for (int y = 0; y < region.getySize(); y++) {
                for (int z = 0; z < region.getzSize(); z++) {
                    world.setBlockAndUpdate(new BlockPos(0+x, 70+y, 0+z), region.getBlockStateAt(x, y, z));
                }
            }
        }
    }

    private static ServerWorld createAndRegisterWorldAndDimension(MinecraftServer server, Map<RegistryKey<World>, ServerWorld> map, RegistryKey<World> worldKey, BiFunction<MinecraftServer, RegistryKey<Dimension>, Dimension> dimensionFactory)
    {
        ServerWorld overworld = server.getLevel(World.OVERWORLD);
        RegistryKey<Dimension> dimensionKey = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
        Dimension dimension = dimensionFactory.apply(server, dimensionKey);

        IChunkStatusListener chunkListener = server.progressListenerFactory.create(11);
        Executor executor = server.executor;
        SaveFormat.LevelSave anvilConverter = server.storageSource;

        IServerConfiguration serverConfig = server.getWorldData();
        DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
        dimensionGeneratorSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());
        DerivedWorldInfo derivedWorldInfo = new DerivedWorldInfo(serverConfig, serverConfig.overworldData());

        ServerWorld newWorld = new ServerWorld(
                server,
                executor,
                anvilConverter,
                derivedWorldInfo,
                worldKey,
                dimension.type(),
                chunkListener,
                dimension.generator(),
                dimensionGeneratorSettings.isDebug(), // boolean: is-debug-world
                BiomeManager.obfuscateSeed(dimensionGeneratorSettings.seed()),
                ImmutableList.of(), // "special spawn list"
                false); // "tick time", true for overworld, always false for everything else

        //overworld.getLevelBorder().addListener(new IBorderListener.Impl(newWorld.getLevelBorder()));
        server.getPlayerList().setLevel(newWorld);

        map.put(worldKey, newWorld);

        server.markWorldsDirty();

        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld));

        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketUpdateDimensions(worldKey.getRegistryName(), true));

        return newWorld;
    }
}
