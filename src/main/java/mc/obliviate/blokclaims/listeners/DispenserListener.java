package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

public class DispenserListener extends ListenerHandler implements Listener {

    public DispenserListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        String chunkID = ClaimUtils.getChunkID(e.getBlock().getLocation()).toString();
        ClaimData cd = cm.getClaimData(chunkID);

        if (cd == null) {
            e.setCancelled(true);
        }



    }

}
