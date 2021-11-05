package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GrowListener extends ListenerHandler implements Listener {

	public GrowListener(BlokClaims plugin) {
		super(plugin);
	}

	@EventHandler
	public void onGrow(StructureGrowEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getWorld())) return;
		final Claim cd = getClaimUtils().getClaimManager().getClaimData(e.getLocation());
		if (cd != null) return;


		final List<Chunk> chunkList = new ArrayList<>();
		final HashMap<Chunk, Boolean> checkedChunks = new HashMap<>();
		for (final BlockState blockState : e.getBlocks().toArray(new BlockState[0])) {
			final Block block = blockState.getBlock();
			if (!chunkList.contains(block.getChunk())) {
				chunkList.add(block.getChunk());
				checkedChunks.put(block.getChunk(), getClaimUtils().getClaimManager().getClaimData(new ChunkID(block.getChunk())) != null);
			}
			if (checkedChunks.get(block.getChunk())) {
				e.getBlocks().remove(blockState);
			}
		}

	}


}
