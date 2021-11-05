package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener extends ListenerHandler implements Listener {

    public BlockBreakListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;

        final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.PLACE_BREAK_BLOCK, e.getBlock().getLocation());
        final boolean spPermState = checkPermission(e.getPlayer(), ClaimPermissionType.PLACE_BREAK_SPAWNER, e.getBlock().getLocation());

        if (permState && e.getBlock().getType().equals(Material.SPAWNER) && !spPermState) {
            e.setCancelled(true);
            e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.spawner-break-cancel"));
            return;
        }


        if (!permState) {
            e.setCancelled(true);
            e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.block-break-cancel"));
        }

    }
}

//BlockFromTo eventi ne yapabilir ki



