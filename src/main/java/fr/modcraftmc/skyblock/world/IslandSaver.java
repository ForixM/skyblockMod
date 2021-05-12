package fr.modcraftmc.skyblock.world;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.schematic.SchemReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;

public class IslandSaver {

    private BlockPos firstPos, secondPos;
    private World weWorld;

    public IslandSaver(){
    }

    public void setFirstPos(BlockPos firstPos){
        this.firstPos = firstPos;
    }

    public void setSecondPos(BlockPos secondPos){
        this.secondPos = secondPos;
    }

    public void save(World world){
        Region region = new Region(firstPos, secondPos, world);
        if (!SkyBlock.CONFIG_DIR.exists())
            SkyBlock.CONFIG_DIR.mkdir();
        File schematic = new File(SkyBlock.CONFIG_DIR.toString()+"/island.schematic");
        if (!schematic.getParentFile().exists())
            schematic.mkdir();
        SchemReader.writeRegionToFile(region, schematic);
    }
}
