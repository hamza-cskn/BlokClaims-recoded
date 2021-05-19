package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;

public abstract class AbstractHandler {

    public BlokClaims plugin;

    public AbstractHandler(BlokClaims plugin) {
        this.plugin = plugin;
    }

}
