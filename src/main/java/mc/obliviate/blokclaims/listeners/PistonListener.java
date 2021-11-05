package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class PistonListener extends ListenerHandler implements Listener {

	public PistonListener(BlokClaims plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPistonExtend(BlockPistonExtendEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
		//Piston her yerde kapalı olsun.
		// if (!ClaimCore.isClaimWorld(e.getBlock().getWorld())) return;
		final Claim cd = getClaimUtils().getClaimManager().getClaimData(e.getBlock().getLocation());

		if (cd == null) {
			e.setCancelled(true);
			return;
		}

		final Chunk chunk = e.getBlock().getChunk();
		for (final Block block : e.getBlocks()) {
            //if piston extends to another chunk
			if (!block.getChunk().equals(chunk)) {
				if (!getClaimUtils().getClaimManager().getClaimData(block.getLocation()).equals(cd)) {
					e.setCancelled(true);
				}
			}
		}
	}
}
