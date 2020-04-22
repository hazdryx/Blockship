# Blocksship v0.1.1
**for Forge 1.7.10**

Blockship is a server side mode which keeps track of block ownership on a block-by-block basis. Once a block is owned it cannot be broken by other players.

## Getting Started
Follow the steps below to get started with this mod.

1. Download this repository and run the build.bat file. This will build the mod in the build/libs folder.
2. Move the jar file to the mods folder on your server.
3. Run the server to generate the blockship.cfg file.
4. Change the ownableBlocks and ownableBlockIDs to decide which blocks can be claimed.
5. Run the `bls reload` command in the console to refresh the list.

Thats all you have to do to get started!

## Sharing Blocks
When you own a block, you are the only one who can break it. If you are working as a team you can allow other players to break the blocks
by using the command `bls <player>`. This will toggle whether that person can break your blocks.

> This is not a two way agreement. If player one shares blocks with player two, only player two and break player one's blocks.
> Player two must share they're blocks with player one for it to be mutual.