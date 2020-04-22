package com.hazdryx.blockship.db;

import java.util.UUID;

import com.hazdryx.blockship.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public interface IBlockRecordProvider {
	public abstract BlockRecord select(Block block, BlockPos pos);
	public abstract boolean insert(Block block, BlockPos pos, UUID owner);
}
