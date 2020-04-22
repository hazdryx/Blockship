package com.hazdryx.blockship;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.hazdryx.blockship.db.FriendDatabase;
import com.hazdryx.blockship.db.BlockDatabase;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;

@Mod(modid = "blockship", version = "0.1.1", acceptableRemoteVersions = "*")
public class BlockshipMod {
	private static BlockshipMod instance;
	public static BlockshipMod getInstance() { return instance; }
	
	private File configFile;
	private BlockDatabase db;
	private File dbFile;
	private FriendDatabase fdb;
	private File fdbFile;
	private Logger logger;
	
	public BlockDatabase getDatabase() { return db; }
	public FriendDatabase getFriendDatabase() { return fdb; }
	public Logger getLogger() { return logger; }
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// Setup directory.
		File configDir = new File(event.getModConfigurationDirectory().toString() + File.separator + "blockship");
		if(!configDir.exists()) configDir.mkdirs();
		
		// Setup variables
		configFile = event.getSuggestedConfigurationFile();
		db = new BlockDatabase();
		dbFile = new File(configDir.toString() + File.separator + "blocks.json");
		fdb = new FriendDatabase();
		fdbFile = new File(configDir.toString() + File.separator + "friends.json");
		logger = event.getModLog();
		
		// Make sure it's on server.
		if (event.getSide().isClient()) {
			logger.warn("This is not a client side mod and will not load. It can be removed from the mods folder on your client.");
			return;
		}
		
		// Load block database.
		logger.info("Loading owned blocks from \"" + dbFile.getAbsolutePath() + "\"...");
		try {
			db = BlockDatabase.load(dbFile);
			logger.info("Success");
		} catch (IOException e) {
			logger.error("Failed to load block database: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Load access database.
		logger.info("Loading friend database from \"" + fdbFile.getAbsolutePath() + "\"...");
		try {
			fdb = FriendDatabase.load(fdbFile);
			logger.info("Success");
		} catch (IOException e) {
			logger.error("Failed to load friend database: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Load configuration
		loadConfig();
		
		// Register events.
		MinecraftForge.EVENT_BUS.register(new BlockEventListener(db));
		
		// Mark as instance
		instance = this;
	}
	
	/**
	 * Loads the configuration at any time.
	 */
	public void loadConfig() {
		// Clear old values
		db.getOwnableBlocks().clear();
		
		// Load configuration
		Configuration config = new Configuration(configFile);
		config.load();
		db.addOwnableBlocks(config.get("general", "ownableBlocks", new String[] { "minecraft:sand", "minecraft:stone" }).getStringList());
		db.addOwnableBlocks(config.get("general", "ownableBlockIDs", new int[] { 1, 45 }).getIntList());
		
		// Resave config.
		config.save();
	}
	
	/**
	 * Saves the block database.
	 */
	public void saveBlocks() {
		logger.info("Saving owned blocks to \"" + dbFile.getAbsolutePath() + "\"...");
		try {
			db.save(dbFile);
			logger.info("Success");
		} catch (IOException e) {
			logger.error("Failed to save block database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Saves the friend database.
	 */
	public void saveFriends() {
		logger.info("Saving friend database to \"" + fdbFile.getAbsolutePath() + "\"...");
		try {
			fdb.save(fdbFile);
			logger.info("Success");
		} catch (IOException e) {
			logger.error("Failed to save friend database: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		// Register commands.
		event.registerServerCommand(new BlockshipCommands());
	}
	
	@EventHandler
	public void worldSave(WorldEvent.Save event) {
		saveBlocks();
	}
	
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		saveBlocks();
	}
}
