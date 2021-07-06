package fr.modcraftmc.skyblock.schematic;

import fr.modcraftmc.skyblock.world.Region;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SchemReader {

    public static Region readFromFile(File schematicFile){
        try {
            Region region;
            CompoundNBT nbt = CompressedStreamTools.readCompressed(new FileInputStream(schematicFile));
            short width = nbt.getShort("Width");
            short height = nbt.getShort("Height");
            short length = nbt.getShort("Length");
            region = new Region(width, height, length);
            if (nbt.contains("Palette")){
                CompoundNBT paletteNBT = nbt.getCompound("Palette");
                List<String> keyset = new ArrayList<>();
                keyset.addAll(paletteNBT.getAllKeys());
                Map<Integer, String> palette = new HashMap<>();
                for (String registry : keyset){
                    palette.put(paletteNBT.getInt(registry), registry);
                }

                byte blockData[] = nbt.getByteArray("BlockData");
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        for (int z = 0; z < length; z++) {
                            int index = (y * length + z) * width + x;
                            BlockState state = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(palette.get((int)blockData[index]))).defaultBlockState();
                            region.addBlock(new BlockPos(x, y, z), state, false);
                        }
                    }
                }

                return region;
            } else {

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean islandReferenceExist(File file){
        return file.exists();
    }

    public static void writeRegionToFile(Region region, File file){
        try {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putShort("Width", region.getxSize());
            nbt.putShort("Height", region.getySize());
            nbt.putShort("Length", region.getzSize());
            nbt.putInt("DataVersion", 2230);
            nbt.putInt("Version", 2);
            List<BlockState> blocks = region.getBlockTypes();
            nbt.putInt("PaletteMax", blocks.size());
            CompoundNBT paletteNBT = new CompoundNBT();
            for (int i = 0; i < blocks.size(); i++){
                paletteNBT.putInt(blocks.get(i).getBlock().getRegistryName().toString(), i);
            }
            nbt.put("Palette", paletteNBT);
            byte[] blockData = new byte[region.getBlockAmount()];
            for (int x = 0; x < region.getxSize(); x++){
                for (int y = 0; y < region.getySize(); y++){
                    for (int z = 0; z < region.getzSize(); z++){
                        int index = (y * region.getzSize() + z) * region.getxSize() + x;
                        blockData[index] = (byte) paletteNBT.getInt(region.getBlockStateAt(x, y, z).getBlock().getRegistryName().toString());
                    }
                }
            }
            nbt.putByteArray("BlockData", blockData);
            CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
