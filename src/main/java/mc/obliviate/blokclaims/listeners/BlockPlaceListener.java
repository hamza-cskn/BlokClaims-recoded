package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.claim.ClaimCore;
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
        ClaimData cd = cm.getClaimData(e.getBlock().getLocation());
        if (cd == null) return;
        ClaimPermission cps = cd.getPermissionState(e.getPlayer());
        boolean permState = cps != null && cps.hasPermission("PLACE_BREAK_BLOCK");
        boolean spPermState = cps != null && cps.hasPermission("PLACE_BREAK_SPAWNER");

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
        ClaimData cd = cm.getClaimData(e.getBlock().getLocation());
        if (cd == null) return;
        ClaimPermission cps = cd.getPermissionState(e.getPlayer());
        boolean permState = cps != null && cps.hasPermission("PLACE_BREAK_BLOCK");
        e.setCancelled(permState);
        if (permState) {
            e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.block-place-cancel"));
        }

    }

}
