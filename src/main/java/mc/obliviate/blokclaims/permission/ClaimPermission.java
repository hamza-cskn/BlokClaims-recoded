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
    private final UUID uuid;
    private final ChunkID claimID;
    private final List<ClaimPermissionType> permissions = new ArrayList<>();

    public ClaimPermission(BlokClaims plugin, UUID uuid, ChunkID claimID, List<ClaimPermissionType> permissions) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.claimID = claimID;
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }

    public List<ClaimPermissionType> getPermissions() {
        return this.permissions;
    }

    public void addPermission(ClaimPermissionType permission) {
        this.permissions.add(permission);

    }

    public void removePermission(ClaimPermissionType permission) {
        this.permissions.remove(permission);
    }

    public boolean hasPermission(ClaimPermissionType permission) {
        //if (player.isOp()) return true;
        return (permissions.contains(permission));
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }


    //public String getUuid() { return uuid; }


}
