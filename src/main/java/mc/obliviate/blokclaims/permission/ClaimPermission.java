package mc.obliviate.blokclaims.permission;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimPermission {

    private final BlokClaims plugin;
    String uuid;
    ChunkID claimID;
    List<String> permissions = new ArrayList<>();

    public ClaimPermission(BlokClaims plugin, String uuid, ChunkID claimID, List<String> permissions) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.claimID = claimID;
        if (permissions != null) {
            this.permissions = permissions;
        }
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public void addPermission(String permission) {
        if (!isValidPermission(permission)) return;
        this.permissions.add(permission);

    }

    public void removePermission(String permission) {
        if (!isValidPermission(permission)) return;
        this.permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        //if (player.isOp()) return true;
        return (permissions.contains(permission) && isValidPermission(permission));
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    private boolean isValidPermission(String permission) {
        BlokClaims.CLAIM_PERMISSIONS.valueOf(permission);
        return true;
    }

    //public String getUuid() { return uuid; }


}
