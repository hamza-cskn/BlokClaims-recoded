package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.Message;
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
        String chunkID = ClaimUtils.getChunkID(e.getBlock().getLocation()).toString();
        ClaimData cd = cm.getClaimData(chunkID);
        if (cd == null) return;
        ClaimPermission cps = cd.getPermissionState(e.getPlayer());
        boolean permState = cps != null && cps.hasPermission("USE_BUCKET");

        if (!permState) {
            e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.use-bucket-cancel"));
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        String chunkID = ClaimUtils.getChunkID(e.getBlock().getLocation()).toString();
        ClaimData cd = cm.getClaimData(chunkID);

        if (cd == null) {
            if (e.getBucket().equals(Material.LAVA_BUCKET)) {
                e.setCancelled(true);
                e.getPlayer().sendActionBar(Message.getConfigMessage("you-can-not-empty-lava-bucket-here"));
            }
        } else {
            ClaimPermission cps = cd.getPermissionState(e.getPlayer());
            boolean permState = cps != null && cps.hasPermission("USE_BUCKET");

            if (!permState) {

                e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.use-bucket-cancel"));
                e.setCancelled(true);

            }
        }

    }
}
