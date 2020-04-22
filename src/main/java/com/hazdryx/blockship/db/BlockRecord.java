package com.hazdryx.blockship.db;

import java.util.UUID;

import com.hazdryx.blockship.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

/**
 * A simple structure which holds a block's position as well
 * as the owner's UUID. 
 * 
 * @author Hazdryx
 */
public class BlockRecord {
	private BlockPos pos;
	private UUID owner;
	
	public BlockRecord(BlockPos pos, UUID owner) {
		this.pos = pos;
		this.owner = owner;
	}
	public BlockRecord(BlockPos pos, String owner) {
		this(pos, UUID.fromString(owner));
	}
	
	public BlockPos getBlockPosition() { return pos; }
	public UUID getOwner() { return owner; }
	public void setOwner(UUID owner) { this.owner = owner; }
}
