package com.hazdryx.blockship.util;

import java.util.UUID;

import net.minecraftforge.event.world.BlockEvent;

/**
 * A data structure for holding x, y, z, and dimension ID of a block.
 * 
 * @author Hazdryx
 */
public class BlockPos {
	private int dimensionId;
	private int x, y, z;
	
	public BlockPos(int dimensionId, int x, int y, int z) {
		this.dimensionId = dimensionId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public BlockPos(BlockEvent event) {
		this(event.world.provider.dimensionId, event.x, event.y, event.z);
	}
	
	public int getDimensionId() { return dimensionId; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getZ() { return z; }
	public int getChunkX() { return (int) Math.floor(x / 16) * 16; }
	public int getChunkZ() { return (int) Math.floor(z / 16) * 16; }
	
	public boolean isEqual(BlockPos pos) {
		return this.dimensionId == pos.dimensionId && this.x == pos.x && this.y == pos.y && this.z == pos.z;
	}
}
