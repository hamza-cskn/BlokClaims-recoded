package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.utils.claim.ClaimCore;

public abstract class ListenerHandler {

    public final BlokClaims plugin;
    public final ClaimCore cm;

    public ListenerHandler(BlokClaims plugin) {
        this.plugin = plugin;
        this.cm = plugin.getClaimCore();
    }

}
