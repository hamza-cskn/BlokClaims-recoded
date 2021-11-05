package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class BurnListener extends ListenerHandler implements Listener {

    public BurnListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
        /*

        String chunkID = ClaimManager.getChunkID(e.getBlock().getLocation()).toString();
        ClaimData cd = Main.claimDataList.get(Main.allChunkList.get(chunkID));
        if (cd != null) {
            e.setCancelled(true);
        }
         */
    }
    @EventHandler
    public void onBurn(BlockBurnEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        final String chunkID = ClaimUtils.getChunkID(e.getBlock().getLocation()).toString();
        final Claim cd = getClaimUtils().getClaimManager().getClaimData(chunkID);
        if (cd != null) {
            e.setCancelled(true);
        }
    }

}
