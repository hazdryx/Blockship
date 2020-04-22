package com.hazdryx.blockship;

import com.hazdryx.blockship.db.BlockDatabase;
import com.hazdryx.blockship.db.BlockRecord;
import com.hazdryx.blockship.util.BlockPos;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;

public class BlockEventListener {
	private BlockDatabase db;
	
	public BlockEventListener(BlockDatabase db) {
		this.db = db;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		// Stops players from breaking blocks.
		EntityPlayerMP p = (EntityPlayerMP) event.getPlayer();
		if(!db.isBlockAccessible(event.block, new BlockPos(event), event.getPlayer().getUniqueID())) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockEvent.PlaceEvent event) {
		EntityPlayerMP p = (EntityPlayerMP) event.player;
		db.insert(event.block, new BlockPos(event), p.getUniqueID());
	}
}
