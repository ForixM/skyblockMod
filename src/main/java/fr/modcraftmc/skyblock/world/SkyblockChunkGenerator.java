package fr.modcraftmc.skyblock.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class SkyblockChunkGenerator extends ChunkGenerator {

    public static final Codec<SkyblockChunkGenerator> CODEC =
            // the registry lookup doesn't actually serialize, so we don't need a field for it
            RegistryLookupCodec.create(Registry.BIOME_REGISTRY)
                    .xmap(SkyblockChunkGenerator::new, SkyblockChunkGenerator::getBiomeRegistry)
                    .codec();

    private final Registry<Biome> biomes;
    public Registry<Biome> getBiomeRegistry() { return this.biomes; }

    public SkyblockChunkGenerator(MinecraftServer server) {
        this(server.registryAccess() // get dynamic registry
                .registryOrThrow(Registry.BIOME_REGISTRY));
    }

    public SkyblockChunkGenerator(Registry<Biome> biomes){
        super(new SingleBiomeProvider(biomes.getOrThrow(Biomes.PLAINS)), new DimensionStructuresSettings(false));
        this.biomes = biomes;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long p_230349_1_) {
        return this;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {

    }

    @Override
    public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {

    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type heightmapType) {
        return 0;
    }

    @Override
    public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
        return new Blockreader(new BlockState[0]);
    }
}
