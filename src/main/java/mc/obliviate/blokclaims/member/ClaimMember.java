package mc.obliviate.blokclaims.member;

import mc.obliviate.blokclaims.permission.ClaimPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ClaimMember {

	private final UUID uuid;
	private final ClaimPermission permissions;

	public ClaimMember(UUID uuid) {
		this(uuid, new ClaimPermission(uuid));
	}

	public ClaimMember(UUID uuid, ClaimPermission permissions) {
		this.uuid = uuid;
		this.permissions = permissions;
	}

	public UUID getUuid() {
		return uuid;
	}

	public ClaimPermission getPermissions() {
		return permissions;
	}

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}


}
