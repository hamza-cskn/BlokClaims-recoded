package mc.obliviate.blokclaims.handlers.database;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.debug.Logger;
import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SQLManager extends SQLHandler {

	private final BlokClaims plugin;
	private SQLTable claimDataTable;

	public SQLManager(final String filePath, BlokClaims plugin) {
		super(filePath);
		this.plugin = plugin;
	}

	public void init() {
		Bukkit.getLogger().info("[BlokClaims] Connecting to database...");
		connect("database");
	}

	@Override
	public void onConnect() {
		Bukkit.getLogger().info("[BlokClaims] Writting to cache...");
		createClaimDataTable();
		putClaimDatas();
		putChunkDatas();
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

		plugin.getLogger().info("Writting sql datas...");
		for (Claim claim : plugin.getDataHandler().getAllClaimDataList().values()) {
			//Owner = UUID of Owner
			final String owner = ClaimSerializer.serializeOwner(claim.getOwner());
			plugin.getLogger().info(owner);

			final String chunkList = ClaimSerializer.serializeChunkList(claim.getChunkList());

			final String formattedPermissionState = ClaimSerializer.serializePermissions(claim.getMembers());

			final String memberList = ClaimSerializer.serializeMemberList(claim.getMembers());

			//ClaimID = mainChunk's ID
			final String claimID = claim.getClaimID().toString();

			//mainBlockLocation = main block of claim
			final String mainBlock = ClaimSerializer.serializeMainBlock(claim.getMainBlock());

			//name, material, world, x, y, z, yaw, pitch
			final String formattedHomeList = ClaimSerializer.serializeClaimHomeList(claim.getHomeList());

			final long energy = claim.getEnergy().getAmount();

			final SQLUpdateColumn update = claimDataTable.createUpdate(claimID)
					.putData("claimID", claimID)
					.putData("owner", owner)
					.putData("energy", energy)
					.putData("mainBlock", mainBlock)
					.putData("permissions", formattedPermissionState)
					.putData("memberList", memberList)
					.putData("chunkList", chunkList)
					.putData("homeList", formattedHomeList);

			claimDataTable.insertOrUpdate(update);
		}


	}

	private void putClaimDatas() {
		try {
			plugin.getDataHandler().setAllClaimDataList(callClaimData(false));
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	private void putChunkDatas() {
		for (final Claim cd : plugin.getDataHandler().getAllClaimDataList().values()) {
			for (final ChunkID chunkID : cd.getChunkList()) {
				plugin.getDataHandler().getAllChunkList().put(chunkID, cd.getClaimID());
			}
		}
		Logger.debug("Claim data listesi: " + plugin.getDataHandler().getAllClaimDataList().size());
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
		if (isConnected()) {
			final long startTime = System.nanoTime();
			try {
				writeClaimData(async);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			final long endTime = System.nanoTime();
			Bukkit.getLogger().info("SQL database backup ended. (" + (double) (endTime - startTime) / 1000000d + "ms)");
		} else {
			Bukkit.getLogger().info("SQL Data saving started without connection or sql statement. BlokClaims cancelled it.");
		}
	}

	public HashMap<ChunkID, Claim> callClaimData(boolean async) throws SQLException {

		final HashMap<ChunkID, Claim> resultClaimData = new HashMap<>();
		final ResultSet rs = claimDataTable.selectAll();

		plugin.getLogger().info("Calling claim datas...");
		while (rs.next()) {
			final ChunkID id = new ChunkID(rs.getString("claimID"));
			final int energy = rs.getInt("energy");

			final UUID owner = ClaimSerializer.deserializeOwner(rs.getString("owner"));
			final List<ChunkID> chunkList = ClaimSerializer.deserializeChunkList(rs.getString("chunkList"));
			Logger.debug(chunkList + "", false, Logger.DebugType.ORANGE);
			final HashMap<UUID, ClaimPermission> permissionStates = ClaimSerializer.deserializePermissions(rs.getString("permissions"), id);
			final HashMap<UUID, ClaimMember> memberList = ClaimSerializer.deserializeMemberList(rs.getString("memberList"), permissionStates);

			final Location mainBlock = ClaimSerializer.deserializeMainBlock(rs.getString("mainBlock"));
			final List<ClaimHome> homeList = ClaimSerializer.deserializeClaimHomeList(rs.getString("homeList"));


			final Claim[] cd = new Claim[1];
			if (async) {
				new BukkitRunnable() {
					@Override
					public void run() {
						cd[0] = new Claim(plugin, owner, memberList, chunkList, mainBlock, id, energy, homeList);

					}
				}.runTask(plugin);
			} else {
				cd[0] = new Claim(plugin, owner, memberList, chunkList, mainBlock, id, energy, homeList);
			}
			resultClaimData.put(id, cd[0]);


		}


		return resultClaimData;
	}


}
