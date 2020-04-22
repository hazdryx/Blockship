package com.hazdryx.blockship.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hazdryx.blockship.BlockshipMod;
import com.hazdryx.blockship.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

/**
 * A class which gets persistent block ownership information.
 * 
 * @author Hazdryx
 */
public class BlockDatabase implements IBlockRecordProvider {
	private ArrayList<ChunkBlockRecordProvider> chunks = new ArrayList<ChunkBlockRecordProvider>();
	private transient ArrayList<String> ownableBlocks = new ArrayList<String>();
	
	public ArrayList<String> getOwnableBlocks() { return ownableBlocks; }
	
	public void addOwnableBlocks(String[] ownableBlocks) {
		for(String blockName : ownableBlocks) {
			Block block = Block.getBlockFromName(blockName);
			if(block == null) BlockshipMod.getInstance().getLogger().warn("Failed to add ownable block: " + blockName);
			
			this.ownableBlocks.add(blockName);
		}
	}
	public void addOwnableBlocks(int[] ownableBlockIds) {
		for(int blockId : ownableBlockIds) {
			Block block = Block.getBlockById(blockId);
			if(block == null) BlockshipMod.getInstance().getLogger().warn("Failed to add ownable block: " + blockId);
			
			this.ownableBlocks.add(Block.blockRegistry.getNameForObject(block));
		}
	}
	
	@Override
	public BlockRecord select(Block block, BlockPos pos) {
		// Find block.
		ChunkBlockRecordProvider chunk = null;
		BlockRecord record = null;
		for(ChunkBlockRecordProvider ch : chunks) {
			BlockRecord br = ch.select(block, pos);
			if(br != null) {
				chunk = ch;
				record = br;
				break;
			}
		}
		
		// Process block.
		if(record == null) return null;
		else if (!isBlockOwnable(block)) {
			chunk.blocks.remove(record);
			return null;
		}
		else return record;
	}
	@Override
	public boolean insert(Block block, BlockPos pos, UUID owner) {
		if (!isBlockOwnable(block)) return false;
		
		// Attempt to add to existing chunk.
		for(ChunkBlockRecordProvider chunk : chunks) {
			if(chunk.insert(block, pos, owner)) return true;
		}
		
		// Add new chunk.
		ChunkBlockRecordProvider chunk = new ChunkBlockRecordProvider(pos.getDimensionId(), pos.getChunkX(), pos.getChunkZ());
		chunk.insert(block, pos, owner); // Should never fail.
		chunks.add(chunk);
		return true;
	}
	private boolean isBlockOwnable(Block b) {
		String name = Block.blockRegistry.getNameForObject(b);
		for(String ownableBlock : ownableBlocks) {
			if (name.equals(ownableBlock)) return true;
		}
		return false;
	}
	
	/**
	 * Gets whether a specific block is accessible to the player.
	 * @param block
	 * @param pos
	 * @param player
	 * @return
	 */
	public boolean isBlockAccessible(Block block, BlockPos pos, UUID player) {
		BlockRecord record = select(block, pos);
		if(record == null) return true;
		if(record.getOwner().equals(player)) return true;
		return BlockshipMod.getInstance().getFriendDatabase().areFriends(record.getOwner(), player);
	}
	
	/**
	 * Save file to path.
	 * @param path
	 * @throws IOException 
	 */
	public void save(File file) throws IOException {
		// Encode BlockDatabase into json
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(this);
		
		// Write file.
		if(!file.exists() && !file.createNewFile()) throw new IOException("Failed to create BlockDatabase file.");
		Files.write(file.toPath(), json.getBytes(StandardCharsets.UTF_16));
	}
	
	/**
	 * Load file from path.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static BlockDatabase load(File file) throws IOException {
		if(file.exists()) {
			// Read file.
			String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_16);
			
			// Decode json into BlockDatabase.
			Gson gson = new Gson();
			return gson.fromJson(json, BlockDatabase.class);
		}
		else {
			return new BlockDatabase();
		}
	}
}
