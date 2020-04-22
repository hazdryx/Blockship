package com.hazdryx.blockship.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FriendDatabase {
	private HashMap<UUID, ArrayList<UUID>> records = new HashMap<UUID, ArrayList<UUID>>();
	
	/**
	 * Toggles the friendship and returns whether you are friends or not.
	 * @param owner
	 * @param player
	 * @return
	 */
	public boolean toggleFriendship(UUID owner, UUID player) {
		ArrayList<UUID> friends = records.get(owner);
		if(friends == null) {
			friends = new ArrayList<UUID>();
			friends.add(player);
			records.put(owner, friends);
			return true;
		}
		else {
			if(friends.contains(player)) {
				friends.remove(player);
				return false;
			}
			else {
				friends.add(player);
				return true;
			}
		}
	}
	
	/**
	 * Gets whether the owner is friends with player (only one way).
	 * @param owner
	 * @param player
	 * @return
	 */
	public boolean areFriends(UUID owner, UUID player) {
		ArrayList<UUID> friends = records.get(owner);
		if(friends == null) return false;
		else return friends.contains(player);
	}
	
	/**
	 * Save file to path.
	 * @param path
	 * @throws IOException 
	 */
	public void save(File file) throws IOException {
		// Encode AccessDatabase into json
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
	public static FriendDatabase load(File file) throws IOException {
		if(file.exists()) {
			// Read file.
			String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_16);
			
			// Decode json into AccessDatabase
			Gson gson = new Gson();
			return gson.fromJson(json, FriendDatabase.class);
		}
		else {
			return new FriendDatabase();
		}
	}
}
