package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketListener extends ListenerHandler implements Listener {

    public BucketListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBucket(PlayerBucketFillEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.USE_BUCKET, e.getBlock().getLocation());

        if (!permState) {
            MessageUtils.sendMessage(e.getPlayer(), "claim-guard.use-bucket-cancel");
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        final Claim cd = getClaimUtils().getClaimManager().getClaimData(e.getBlock().getLocation());

        if (cd == null) {
            if (e.getBucket().equals(Material.LAVA_BUCKET)) {
                e.setCancelled(true);
                MessageUtils.sendMessage(e.getPlayer(), "you-can-not-empty-lava-bucket-here");
            }
        } else {
            final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.USE_BUCKET, e.getBlock().getLocation());

            if (!permState) {
                MessageUtils.sendMessage(e.getPlayer(), "claim-guard.use-bucket-cancel");
                e.setCancelled(true);

            }
        }

    }
}
