package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

public class DispenserListener extends ListenerHandler implements Listener {

    public DispenserListener(final BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDispense(final BlockDispenseEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        final String chunkID = ClaimUtils.getChunkID(e.getBlock().getLocation()).toString();
        final Claim cd = getClaimUtils().getClaimManager().getClaimData(chunkID);

        if (cd == null) {
            e.setCancelled(true);
        }



    }

}
