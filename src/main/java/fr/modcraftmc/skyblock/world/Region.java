package fr.modcraftmc.skyblock.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class Region {
    Map<BlockPos, BlockState> region;
    BlockPos firstPos, secondPos;
    World world;
    short xSize, ySize, zSize;

    public Region(BlockPos firstPos, BlockPos secondPos, World world){
        updateRegion(firstPos, secondPos, world);
    }

    public Region(short xSize, short ySize, short zSize){
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        region = new HashMap<>();
    }

    public short getxSize() {
        return xSize;
    }

    public short getySize() {
        return ySize;
    }

    public short getzSize() {
        return zSize;
    }

    public void updateRegion(BlockPos firstPos, BlockPos secondPos, World world){
        this.firstPos = firstPos;
        this.secondPos = secondPos;
        xSize = (short) (secondPos.getX() - firstPos.getX());
        ySize = (short) (secondPos.getY() - firstPos.getY());
        zSize = (short) (secondPos.getZ() - firstPos.getZ());
        region = new HashMap<>();
        this.world = world;
        getLevelRegion();
    }

    public void addBlock(BlockPos pos, BlockState block, boolean force){
        if (force){
            region.put(pos, block);
        } else {
            region.putIfAbsent(pos, block);
        }
    }

    public void removeBlock(BlockPos pos){
        region.remove(pos);
    }

    public List<BlockState> getBlockTypes(){
        List<BlockState> blocks = new ArrayList<>();
        for (int x = 0; x < xSize; x++){
            for (int y = 0; y < ySize; y++){
                for (int z = 0; z < zSize; z++){
                    BlockState block = getBlockStateAt(x, y, z);
                    if (!blocks.contains(block)){
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public int getBlockAmount(){
        return region.size();
    }

    private void getLevelRegion(){
        int xS = 1;
        int yS = 1;
        int zS = 1;

        if (xSize < 0){
            xS = -1;
            xSize *= xS;
        }
        xSize++;
        if (ySize < 0){
            yS = -1;
            ySize *= yS;
        }
        ySize++;
        if (zSize < 0){
            zS = -1;
            zSize *= zS;
        }
        zSize++;

        for (int x = 0; x < xSize; x++){
            for (int y = 0; y < ySize; y++){
                for (int z = 0; z < zSize; z++){
                    region.put(new BlockPos(x, y, z), world.getBlockState(new BlockPos(firstPos.getX()+x*xS, firstPos.getY()+y*yS, firstPos.getZ()+z*zS)));
                }
            }
        }
    }

    public BlockState getBlockStateAt(int x, int y, int z){
        return region.get(new BlockPos(x, y, z));
    }

    public Map<BlockPos, BlockState> getRegion(){
        return region;
    }

    public void buildRegion(World world, BlockPos pos){
        for (int x = 0; x < getxSize(); x++) {
            for (int y = 0; y < getySize(); y++) {
                for (int z = 0; z < getzSize(); z++) {
                    world.setBlockAndUpdate(pos, getBlockStateAt(x, y, z));
                }
            }
        }
    }
}
