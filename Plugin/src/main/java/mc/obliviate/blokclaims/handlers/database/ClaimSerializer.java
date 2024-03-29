package mc.obliviate.blokclaims.handlers.database;

import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class ClaimSerializer {

	/**
	 * SERIALIZE & DESERIALIZE METHODS
	 */

	protected static int stringToInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			Bukkit.getLogger().severe("String to int operation has failed!");
			e.printStackTrace();
		}
		return 0;
	}

	protected static UUID deserializeOwner(String rawOwner) {
		return UUID.fromString(rawOwner);
	}

	protected static String serializeOwner(UUID owner) {
		return owner.toString();
	}


	protected static List<ChunkID> deserializeChunkList(String stringChunkList) {
		/**
		 * flat;7;0,
		 * flat;8;0,
		 * flat;9;0,
		 * flat;9;1,
		 **/
		final List<ChunkID> chunkList = new ArrayList<>();
		for (final String rawChunk : stringChunkList.split(",")) {
			final ChunkID id = new ChunkID(rawChunk.replace(";", ","));
			chunkList.add(id);
		}
		Collections.reverse(chunkList);
		return chunkList;
	}

	protected static String serializeChunkList(List<ChunkID> chunkIdlist) {
		//ChunkList = world1;x1;y1, world2;x2;y2
		final StringBuilder chunkList = new StringBuilder();
		for (final ChunkID chunkid : chunkIdlist) {
			final String formattedChunk = chunkid.toString().replace(",", ";");
			Bukkit.getLogger().info(formattedChunk);
			chunkList.insert(0, formattedChunk + ",");
		}
		return chunkList.toString();
	}

	protected static HashMap<UUID, ClaimMember> deserializeMemberList(String stringMemberList, HashMap<UUID, ClaimPermission> permissionState) {
		final HashMap<UUID, ClaimMember> memberList = new HashMap<>();
		for (final String memberUUID : stringMemberList.split(",")) {
			final UUID uuid = UUID.fromString(memberUUID);
			final ClaimMember member = new ClaimMember(uuid, permissionState.get(uuid));
			memberList.put(uuid, member);

		}
		return memberList;
	}

	protected static String serializeMemberList(HashMap<UUID, ClaimMember> memberList) {
		//MemberList = Member1, Member2, Member3...
		StringBuilder result = new StringBuilder();
		for (ClaimMember member : memberList.values()) {
			String uuid = member.getUuid().toString();
			result.insert(0, uuid + ",");

			//PermissionStates putting

		}
		return result.toString();


	}

	protected static HashMap<UUID, ClaimPermission> deserializePermissions(String stringPermission, ChunkID id) {
		HashMap<UUID, ClaimPermission> permissionStates = new HashMap<>();
		for (final String data : stringPermission.split(",")) {
			final String[] datas = data.split(";");
			if (datas[0] == null || datas[0].equalsIgnoreCase("")) continue;
			final List<ClaimPermissionType> permissions = new ArrayList<>();

			boolean first = true;
			for (final String perm : datas) {
				if (first) {
					first = false;
					continue;
				}
				permissions.add(ClaimPermissionType.valueOf(perm));
			}

			permissionStates.put(UUID.fromString(datas[0]), new ClaimPermission(UUID.fromString(datas[0]), permissions));
		}
		return permissionStates;
	}

	protected static String serializePermissions(HashMap<UUID, ClaimMember> members) {

		final StringBuilder formattedPermissionState = new StringBuilder();
		//Format Permission States
		for (final ClaimMember member : members.values()) {
			final StringBuilder format = new StringBuilder();
			final ClaimPermission cps = member.getPermissions();

			if (cps == null) continue;

			for (final ClaimPermissionType permission : cps.getPermissions()) {
				format.insert(0, permission + ";");
			}

			formattedPermissionState.insert(0, member.getUuid() + ";" + format + ",");
		}

		return formattedPermissionState.toString();

	}

	protected static Location deserializeMainBlock(String mainBlock) {
		String[] mainBlockData = mainBlock.split(";");
		return new Location(Bukkit.getWorld(mainBlockData[0]), stringToInt(mainBlockData[1]), stringToInt(mainBlockData[2]), stringToInt(mainBlockData[3]));
	}

	protected static String serializeMainBlock(Location mainBlockLocation) {
		return mainBlockLocation.getWorld().getName() + ";" +
				mainBlockLocation.getBlockX() + ";" +
				mainBlockLocation.getBlockY() + ";" +
				mainBlockLocation.getBlockZ();
	}

	protected static List<ClaimHome> deserializeClaimHomeList(String home) {
		List<ClaimHome> homeList = new ArrayList<>();
		for (String rawHome : home.split(",")) {
			String[] split = rawHome.split(";");
			if (split.length != 8) {
				if (split.length != 1) {
					Bukkit.getLogger().warning("A Home location couldn't resolved: " + Objects.requireNonNull(split[0]));
				}
				continue;
			}
			//name, material, world, x, y, z, yaw, pitch
			Location hloc = new Location(Bukkit.getWorld(split[2]), Double.parseDouble(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5]), Float.parseFloat(split[6]), Float.parseFloat(split[7]));

			//Location hloc = new Location(Bukkit.getWorld("world"),1,2,3);
			homeList.add(new ClaimHome(hloc, Material.getMaterial(split[1]), split[0]));
		}
		return homeList;
	}

	protected static String serializeClaimHomeList(List<ClaimHome> homeList) {
		StringBuilder formattedHomeList = new StringBuilder();
		if (homeList != null) {
			for (ClaimHome home : homeList) {
				String format =
						home.getName() +
								";" + home.getIcon().toString() +
								";" + home.getLoc().getWorld().getName() +
								";" + home.getLoc().getX() +
								";" + home.getLoc().getY() +
								";" + home.getLoc().getZ() +
								";" + home.getLoc().getYaw() +
								";" + home.getLoc().getPitch();

				formattedHomeList.insert(0, format + ",");
			}
		}
		return formattedHomeList.toString();

	}

}
