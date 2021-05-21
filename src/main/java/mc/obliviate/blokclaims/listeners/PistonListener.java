package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
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
        //Piston her yerde kapalı olsun.
        // if (!ClaimCore.isClaimWorld(e.getBlock().getWorld())) return;
        ClaimData cd = cm.getClaimData(e.getBlock().getLocation());

        if (cd == null){ e.setCancelled(true); return;}

        Chunk chunk = e.getBlock().getChunk();
        for (Block block : e.getBlocks()) {
            if (!block.getChunk().equals(chunk)) {
                if (!cm.getClaimData(block.getLocation()).equals(cd)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
