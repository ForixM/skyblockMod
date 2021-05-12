package fr.modcraftmc.skyblock.init;

import fr.modcraftmc.skyblock.SkyBlock;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class DimensionInit {

    //public static final DeferredRegister<ModDimension> MOD_DIMENSION = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, SkyBlock.MOD_ID);

    //public static final RegistryObject<ModDimension> SKYBLOCK_DIM = MOD_DIMENSION.register("skyblock_dim", SkyBlockModDimension::new);

    public static final RegistryKey<Dimension> SKYBLOCK_ID = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation(SkyBlock.MOD_ID, "skyblock_dim"));

    public static final RegistryKey<DimensionType> DIMENSION_TYPE_ID = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(SkyBlock.MOD_ID, "skyblock_dim"));
}
