package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener extends ListenerHandler implements Listener {

	public TeleportListener(BlokClaims plugin) {
		super(plugin);
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent e) {

		if (!ClaimUtils.isClaimWorld(e.getTo().getWorld())) return;
		final PlayerTeleportEvent.TeleportCause cause = e.getCause();
		switch (cause) {
			case CHORUS_FRUIT:
			case ENDER_PEARL:
			case NETHER_PORTAL:
				final Claim cd = getClaimUtils().getClaimManager().getClaimData(e.getTo());
				if (cd == null) return;
				if (cd.getMembers().containsKey(e.getPlayer().getUniqueId())) return;
				e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.teleport-cancel"));
				e.setCancelled(true);
		}
	}


}
