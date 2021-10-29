package mc.obliviate.blokclaims.handlers.database;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLManager extends SQLHandler {

	private final BlokClaims plugin;
	private SQLTable claimDataTable;

	public SQLManager(final String filePath, BlokClaims plugin) {
		super(filePath, true);
		this.plugin = plugin;
	}

	public void init() {
		Bukkit.getLogger().info("[BlokClaims] Connecting to database...");
		connect("database");
	}

	@Override
	public void onConnect() {
		Bukkit.getLogger().info("[BlokClaims] Connected...");
		createClaimDataTable();
	}

	public void createClaimDataTable() {
		claimDataTable = new SQLTable("claims", "claimID");
		claimDataTable = claimDataTable
				.addField("claimID", DataType.TEXT, true, true, true)
				.addField("owner", DataType.TEXT)
				.addField("energy", DataType.INTEGER)
				.addField("mainBlock", DataType.TEXT)
				.addField("permissions", DataType.TEXT)
				.addField("memberList", DataType.TEXT)
				.addField("chunkList", DataType.TEXT)
				.addField("homeList", DataType.TEXT);

		claimDataTable.create();
	}

	public void writeClaimData(final boolean async) throws SQLException {

		final HashMap<ChunkID, ClaimData> claimsInSQL = getClaimDataFromDB(async);
		for (ClaimData claimData : plugin.getDataHandler().getAllClaimDataList().values()) {
			//Owner = UUID of Owner
			final String owner = ClaimSerializer.serializeOwner(claimData.getOwner());

			final String chunkList = ClaimSerializer.serializeChunkList(claimData.getChunkList());

			final HashMap<UUID, ClaimPermission> permissionStates = new HashMap<>();
			for (final UUID uuid : claimData.getMemberList()) {
				permissionStates.put(uuid, claimData.getPermissionState(uuid));
			}
			final String formattedPermissionState = ClaimSerializer.serializePermissions(permissionStates);

			final String memberList = ClaimSerializer.serializeMemberList(claimData.getMemberList());

			//ClaimID = mainChunk's ID
			final String claimID = claimData.getClaimID().toString();

			//mainBlockLocation = main block of claim
			final String mainBlock = ClaimSerializer.serializeMainBlock(claimData.getMainBlock());

			//name, material, world, x, y, z, yaw, pitch
			final String formattedHomeList = ClaimSerializer.serializeClaimHomeList(claimData.getHomeList());

			long energy = claimData.getEnergy().getAmount();

			claimDataTable.createUpdate(claimID)
					.putData("owner", owner)
					.putData("memberList", memberList)
					.putData("chunkList", chunkList)
					.putData("claimTimer", energy)
					.putData("mainBlock", mainBlock)
					.putData("permissionState", formattedPermissionState)
					.putData("homeList", formattedHomeList);
		}


	}

	private void putClaimDatas() {
		try {
			plugin.getDataHandler().setAllClaimDataList(getClaimDataFromDB(false));
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	private void putChunkDatas() {
		for (ClaimData cd : plugin.getDataHandler().getAllClaimDataList().values()) {
			for (ChunkID chunkID : cd.getChunkList()) {
				plugin.getDataHandler().getAllChunkList().put(chunkID, cd.getClaimID());
			}
		}
		Debug.log("Claim data listesi: " + plugin.getDataHandler().getAllClaimDataList().size());
	}

	public void deleteClaimData(final ChunkID chunkID) {
		claimDataTable.delete(chunkID);
	}

	public void save(boolean async) {
		if (async) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveSQLDatabase(true));
		else saveSQLDatabase(false);
	}

	private void saveSQLDatabase(boolean async) {
		Bukkit.getLogger().info("SQL database backup started (ASYNC: " + async + ")");
		if (getConnection() != null) {
			long aaa = System.nanoTime();
			try {
				writeClaimData(async);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			long bbb = System.nanoTime();
			Bukkit.getLogger().info("SQL database backup ended. (" + (double) (bbb - aaa) / 1000000.0 + "ms)");
		} else {
			Bukkit.getLogger().info("SQL Data saving started without connection or sql statement. blokClaims cancelled it.");
		}
	}

	public HashMap<ChunkID, ClaimData> getClaimDataFromDB(boolean async) throws SQLException {

		final HashMap<ChunkID, ClaimData> resultClaimData = new HashMap<>();
		final ResultSet rs = claimDataTable.selectAll();

		while (rs.next()) {
			ChunkID id = new ChunkID(rs.getString("claimID"));
			int energy = rs.getInt("energy");

			UUID owner = ClaimSerializer.deserializeOwner(rs.getString("owner"));
			List<ChunkID> chunkList = ClaimSerializer.deserializeChunkList(rs.getString("chunkList"));
			Debug.log(chunkList + "", false, Debug.DebugType.ORANGE);
			List<UUID> memberList = ClaimSerializer.deserializeMemberList(rs.getString("memberList"));
			HashMap<UUID, ClaimPermission> permissionStates = ClaimSerializer.deserializePermissions(rs.getString("permissions"), id);
			Location mainBlock = ClaimSerializer.deserializeMainBlock(rs.getString("mainBlock"));
			List<ClaimHome> homeList = ClaimSerializer.deserializeClaimHomeList(rs.getString("homeList"));


			final ClaimData[] cd = new ClaimData[1];
			if (async) {
				new BukkitRunnable() {
					@Override
					public void run() {
						cd[0] = new ClaimData(plugin, owner, memberList, chunkList, mainBlock, id, energy, permissionStates, homeList);

					}
				}.runTask(plugin);
			} else {
				cd[0] = new ClaimData(plugin, owner, memberList, chunkList, mainBlock, id, energy, permissionStates, homeList);
			}
			resultClaimData.put(id, cd[0]);


		}


		return resultClaimData;
	}


}
