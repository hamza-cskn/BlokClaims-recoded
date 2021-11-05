package mc.obliviate.blokclaims.permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimPermission {

	private final UUID uuid;
	private final List<ClaimPermissionType> permissions = new ArrayList<>();

	public ClaimPermission(UUID uuid) {
		this(uuid, null);
	}

	public ClaimPermission(UUID uuid, List<ClaimPermissionType> permissions) {
		this.uuid = uuid;
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
