package fr.modcraftmc.skyblock.events;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;

public class PistonEvent extends net.minecraftforge.event.world.PistonEvent {
    /**
     * @param world
     * @param pos       - The position of the piston
     * @param direction - The direction of the piston
     * @param moveType
     */

    private World world;

    public PistonEvent(World world, BlockPos pos, Direction direction, PistonMoveType moveType) {
        super(world, pos, direction, moveType);
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public static class Post extends PistonEvent
    {

        public Post(World world, BlockPos pos, Direction direction, PistonMoveType moveType)
        {
            super(world, pos, direction, moveType);
        }

    }

    /**
     * Fires before the piston has updated block states. Cancellation prevents movement.
     */
    @Cancelable
    public static class Pre extends PistonEvent
    {

        public Pre(World world, BlockPos pos, Direction direction, PistonMoveType moveType)
        {
            super(world, pos, direction, moveType);
        }

    }



}
