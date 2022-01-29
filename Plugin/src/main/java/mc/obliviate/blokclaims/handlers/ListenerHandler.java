package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class ListenerHandler {

    private final BlokClaims plugin;
    private final ClaimUtils claimUtils;

    public ListenerHandler(BlokClaims plugin) {
        this.plugin = plugin;
        this.claimUtils = plugin.getClaimUtils();
    }

    public ClaimUtils getClaimUtils() {
        return claimUtils;
    }

    public boolean checkPermission(Player player, ClaimPermissionType type, Location loc) {
        return claimUtils.checkPermission(player, type, loc);
    }

    public BlokClaims getPlugin() {
        return plugin;
    }
}
