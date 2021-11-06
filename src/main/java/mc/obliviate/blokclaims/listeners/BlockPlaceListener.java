package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener extends ListenerHandler implements Listener {

	public BlockPlaceListener(BlokClaims plugin) {
		super(plugin);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;

		final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.PLACE_BREAK_BLOCK, e.getBlock().getLocation());
		final boolean spPermState = checkPermission(e.getPlayer(), ClaimPermissionType.PLACE_BREAK_SPAWNER, e.getBlock().getLocation());

		if (permState && e.getBlock().getType().equals(Material.SPAWNER) && !spPermState) {
			e.setCancelled(true);
			e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.spawner-place-cancel"));
			return;
		}

		if (!permState) {
			e.setCancelled(true);
			e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.block-place-cancel"));
		}

	}


	@EventHandler
	public void onPlace(BlockMultiPlaceEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
		final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.PLACE_BREAK_BLOCK, e.getBlock().getLocation());
		if (!permState) {
			e.setCancelled(false);
			e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.block-place-cancel"));
		}

	}

}
