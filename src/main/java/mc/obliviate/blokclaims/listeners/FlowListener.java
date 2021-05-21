package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class FlowListener extends ListenerHandler implements Listener {

    public FlowListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        if (e.getBlock().getType().equals(Material.WATER) || e.getBlock().getType().equals(Material.LAVA)) {
            ClaimData cdTo = cm.getClaimData(e.getToBlock().getLocation());
            ClaimData cdFrom = cm.getClaimData(e.getBlock().getLocation());
            if (cdTo == null) return;
            if (cdTo.equals(cdFrom)) return;
            e.setCancelled(true);
        }
    }
}
