package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener extends ListenerHandler implements Listener{

    public RedstoneListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getBlock().getWorld())) return;
        if (getClaimUtils().getClaimManager().getClaimData(e.getBlock().getLocation()) == null) e.setNewCurrent(0);
    }

}
