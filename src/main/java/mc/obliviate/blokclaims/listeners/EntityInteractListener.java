package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class EntityInteractListener extends ListenerHandler implements Listener {

	public EntityInteractListener(BlokClaims plugin) {
		super(plugin);
	}

	@EventHandler
	public void onArmorStandManipule(final PlayerArmorStandManipulateEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getRightClicked().getWorld())) return;
		final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.ARMOR_STAND_INTERACT, e.getRightClicked().getLocation());

		if (permState) return;
		e.setCancelled(true);
		e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.armor-stand-interact-cancel"));
	}

	@EventHandler
	public void onItemFrameInteract(final PlayerInteractEntityEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getRightClicked().getWorld())) return;
		if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
			final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.ITEM_FRAME_INTERACT, e.getRightClicked().getLocation());
			if (permState) return;
			e.setCancelled(true);
			e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.item-frame-interact-cancel"));
		}
	}

	//DESTROY EVENTS
	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
		if (!e.getDamager().getType().equals(EntityType.PLAYER)) return;

		final Claim cd = getClaimUtils().getClaimManager().getClaimData(e.getEntity().getLocation());
		if (cd == null) return;

		final Player p = (Player) e.getDamager();
		boolean permState;

		switch (e.getEntityType()) {
			case PAINTING:
			case ITEM_FRAME:
				permState = checkPermission(p, ClaimPermissionType.ITEM_FRAME_INTERACT, e.getEntity().getLocation());
				if (permState) return;
				e.setCancelled(true);
				p.sendActionBar(Message.getConfigMessage("claim-guard.item-frame-interact-cancel"));
				return;
			case ARMOR_STAND:
				permState = checkPermission(p, ClaimPermissionType.ARMOR_STAND_INTERACT, e.getEntity().getLocation());
				if (permState) return;
				e.setCancelled(true);
				p.sendActionBar(Message.getConfigMessage("claim-guard.armor-stand-interact-cancel"));
				return;
			default:
				permState = checkPermission(p, ClaimPermissionType.INTERACT_MOBS, e.getEntity().getLocation());
				if (permState) return;
				e.setCancelled(true);
		}
	}

	//DESTROY EVENTS
	@EventHandler
	public void onHangingBreakEntity(final HangingBreakByEntityEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
		if (e.getRemover() != null && e.getRemover().getType().equals(EntityType.PLAYER)) {
			switch (e.getEntity().getType()) {
				case ITEM_FRAME:
				case ARMOR_STAND: //actually armor stands does not need it.
				case PAINTING:

					final Player p = (Player) e.getRemover();
					final boolean permState = checkPermission(p, ClaimPermissionType.USE_BUCKET, e.getEntity().getLocation());

					if (permState) return;
					e.setCancelled(true);
					p.sendActionBar(Message.getConfigMessage("claim-guard.item-frame-interact-cancel"));
					break;
			}
		}
	}

	@EventHandler
	public void onShear(final PlayerShearEntityEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;

		final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.INTERACT_MOBS, e.getEntity().getLocation());
		if (permState) return;
		e.setCancelled(true);

	}

	@EventHandler
	public void onTarget(final EntityTargetEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
		if (e.getTarget() != null && e.getTarget().getType().equals(EntityType.PLAYER)) {
			final Player p = (Player) e.getTarget();

			final boolean permState = checkPermission(p, ClaimPermissionType.INTERACT_MOBS, e.getEntity().getLocation());
			if (permState) return;
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onMount(final EntityMountEvent e) {
		if (!ClaimUtils.isClaimWorld(e.getMount().getWorld())) return;
		if (e.getEntityType().equals(EntityType.PLAYER)) {
			final boolean permState = checkPermission(((Player) e.getEntity()), ClaimPermissionType.INTERACT_MOBS, e.getMount().getLocation());
			if (permState) return;
			e.setCancelled(true);
		}
	}

}
