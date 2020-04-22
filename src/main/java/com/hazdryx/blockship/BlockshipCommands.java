package com.hazdryx.blockship;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.swing.text.html.parser.Entity;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class BlockshipCommands extends CommandBase {
	
	@Override
	public String getCommandName() {
		return "bls";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Used for giving and removing access to break owned blocks.";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(sender instanceof EntityPlayerMP) {
			EntityPlayerMP p = (EntityPlayerMP) sender;
			if(args == null || args.length < 1) {
				ChatComponentText text = new ChatComponentText("[Blockship] Invalid command.");
				text.getChatStyle().setColor(EnumChatFormatting.RED);
				p.addChatMessage(text);
				return;
			}
			
			// Attempt to obtain player id.
			UUID player = null;
			System.out.println(MinecraftServer.getServer().getConfigurationManager().playerEntityList.size());
			for(Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
				EntityPlayerMP pl = (EntityPlayerMP) obj;
				if(pl.getCommandSenderName().equalsIgnoreCase(args[0]) && !p.getUniqueID().equals(pl.getUniqueID())) {
					player = pl.getUniqueID();
					break;
				}
			}
			
			// Friend player if the player exists.
			if(player == null) {
				ChatComponentText text = new ChatComponentText("[Blockship] Cannot friend/unfriend an offline player.");
				text.getChatStyle().setColor(EnumChatFormatting.RED);
				p.addChatMessage(text);
			}
			else {
				boolean areFriends = BlockshipMod.getInstance().getFriendDatabase().toggleFriendship(p.getUniqueID(), player);
				p.addChatMessage(new ChatComponentText("[Blockship] You have " + (areFriends ? "friended " : "unfriended ") + args[0] + "."));
				
				BlockshipMod.getInstance().saveFriends();
			}
		}
		else {
			// Reload mod by saving blocks, friend and reloading the config file.
			if(args == null || args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
				ChatComponentText text = new ChatComponentText("[Blockship] Invalid command.");
				text.getChatStyle().setColor(EnumChatFormatting.RED);
				sender.addChatMessage(text);
				return;
			}
			else {
				BlockshipMod.getInstance().saveBlocks();
				BlockshipMod.getInstance().loadConfig();
				BlockshipMod.getInstance().getLogger().info("Successfully reloaded blockship.");
			}
		}
	}
	
}
