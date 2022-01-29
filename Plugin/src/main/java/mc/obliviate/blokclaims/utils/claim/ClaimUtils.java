package mc.obliviate.blokclaims.utils.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.debug.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClaimUtils {

	private final ClaimManager claimManager;

	public ClaimUtils(ClaimManager claimManager) {
		this.claimManager = claimManager;
	}

	// GET CHUNK_ID
	public static ChunkID getChunkID(Chunk chunk) {
		if (chunk != null) {
			return new ChunkID(chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ());
		}
		Logger.debug("ChunkID(chunk) is null");
		return null;
	}

	public static ChunkID getChunkID(CompletableFuture<Chunk> chunk) {
		if (chunk != null) {
			try {
				return new ChunkID(chunk.get().getWorld().getName() + "," + chunk.get().getX() + "," + chunk.get().getZ());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		Logger.debug("ChunkID(async chunk) is null");
		return null;
	}

	public static ChunkID getChunkID(Location loc) {
		return getChunkID(loc.getChunk());
	}

	public static ChunkID getChunkID(World world, int x, int z, boolean isLocation) {
		int n = 1;
		if (isLocation) {
			n = 16;
		}
		if (world != null) {
			return new ChunkID(world.getName() + "," + x / n + "," + z / n);
		}
		Logger.debug("ChunkID(wxz) is null");
		return null;
	}

	public static boolean isClaimWorld(World world) {
		return BlokClaims.getWorldList().contains(world.getName());
	}

	//
	public void broadcastClaimMembers(Claim cd, String str) {
		for (final ClaimMember member : cd.getMembers().values()) {
			final Player p = Bukkit.getPlayer(member.getUuid());
			if (p == null) continue;
			p.sendMessage(str);
		}
	}

	public boolean checkPermission(final Player player, final ClaimPermissionType permissionType, final Location location) {
		final Claim cd = claimManager.getClaimData(location);
		if (cd == null) return true; //has all permission in unowned claims
		final ClaimPermission cps = cd.getMembers().get(player.getUniqueId()).getPermissions();

		return (cps != null && cps.hasPermission(permissionType));
	}

	public ClaimManager getClaimManager() {
		return claimManager;
	}
}
