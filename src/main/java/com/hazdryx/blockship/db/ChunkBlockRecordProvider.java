package com.hazdryx.blockship.db;

import java.util.ArrayList;
import java.util.UUID;

import com.hazdryx.blockship.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class ChunkBlockRecordProvider implements IBlockRecordProvider {
	protected ArrayList<BlockRecord> blocks = new ArrayList<BlockRecord>();
	private int dimensionId;
	private int x;
	private int z;
	
	public ChunkBlockRecordProvider(int dimensionId, int x, int z) {
		this.dimensionId = dimensionId;
		this.x = x;
		this.z = z;
	}
	
	public int getDimensionId() { return dimensionId; }
	public int getChunkX() { return x; }
	public int getChunkZ() { return z; }
	
	@Override
	public BlockRecord select(Block block, BlockPos pos) {
		// Check if valid chunk
		if(pos.getDimensionId() != dimensionId || pos.getChunkX() != x || pos.getChunkZ() != z) return null;
		
		// Check if tile entity
		WorldServer world = DimensionManager.getWorld(pos.getDimensionId());
		TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
		if(te != null) {
			pos = new BlockPos(pos.getDimensionId(), te.xCoord, te.yCoord, te.zCoord);
		}
		// Check if door.
		if(block instanceof BlockDoor) {
			if(world.getBlock(pos.getX(), pos.getY() - 1, pos.getZ()) instanceof BlockDoor) {
				pos = new BlockPos(pos.getDimensionId(), pos.getX(), pos.getY() - 1, pos.getZ());
			}
		}
		
		// Search for block.
		for(BlockRecord record : blocks) {
			if(record.getBlockPosition().isEqual(pos)) return record;
		}
		return null;
	}
	@Override
	public boolean insert(Block block, BlockPos pos, UUID owner) {
		// Checks if BlockPos is part of this chunk.
		if(pos.getDimensionId() != dimensionId || pos.getChunkX() != x || pos.getChunkZ() != z) return false;
		
		// Claim block.
		BlockRecord record = select(block, pos);
		if(record == null) {
			blocks.add(new BlockRecord(pos, owner));
		}
		else { 
			record.setOwner(owner);
		}
		return true;
	}
}
