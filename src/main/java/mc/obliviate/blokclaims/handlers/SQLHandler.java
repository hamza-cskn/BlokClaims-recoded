package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.debug.Debug;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLHandler {

    private Statement statement;
    private Connection connection;
    private final BlokClaims plugin;


    public SQLHandler(BlokClaims plugin) {
        this.plugin = plugin;
        connect();
    }

    public void connect() {
        String URL = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "database.db";
        statement = null;
        try {
            connection = DriverManager.getConnection(URL);
            Debug.log("Veritabanına başarıyla bağlanıldı.");
            statement = connection.createStatement();

        } catch (SQLException throwables) {
            Bukkit.getLogger().log(Level.SEVERE, "Veritabanına bağlanırken bir hata oluştu!");
            throwables.printStackTrace();
        }
    }

    private int stringToInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().severe("String to int operation has failed.");
            e.printStackTrace();
        }
        return 0;
    }

    private OfflinePlayer stringUUIDtoOfflinePlayer(String ownerUUID) {
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
        } catch (IllegalArgumentException e) {
            Debug.log("INVALID UUID: " + ownerUUID);
        }
        return null;
    }

    public void updateClaimData(boolean async) throws SQLException {

        HashMap<String, ClaimData> claimsInSQL = getClaimDataFromDB(async);

        for (ClaimData claimData : plugin.getDataHandler().getAllClaimDataList().values()) {

            //Owner = UUID of Owner
            String owner = claimData.getOwner().getUniqueId().toString();

            //
            HashMap<String, ClaimPermission> permissionStates = new HashMap<>();

            //MemberList = Member1, Member2, Member3...
            StringBuilder memberList = new StringBuilder();
            for (UUID member : claimData.getMemberList()) {
                String uuid = member.toString();
                memberList.insert(0, uuid + ",");

                //PermissionStates putting
                permissionStates.put(uuid, claimData.getPermissionState(uuid));
            }

            StringBuilder formattedPermissionState = new StringBuilder();
            //Format Permission States
            for (String key : permissionStates.keySet()) {
                StringBuilder format = new StringBuilder();
                ClaimPermission cps = permissionStates.get(key);
                if (cps == null) continue;
                for (String permission : cps.getPermissions()) {
                    format.insert(0, permission + ";");
                }
                formattedPermissionState.insert(0, key + ";" + format + ",");
            }

            //ChunkList = world1;x1;y1, world2;x2;y2
            StringBuilder chunkList = new StringBuilder();
            for (ChunkID chunkid : claimData.getChunkList()) {
                String formattedChunk = chunkid.toString().replace(",", ";");
                chunkList.insert(0, formattedChunk + ",");
            }

            //ClaimID = mainChunk's ID
            String claimID = claimData.toString();

            //mainBlockLocation = main block of claim
            Location mainBlockLocation = claimData.getMainBlock();
            String mainBlock = mainBlockLocation.getWorld().getName() + ";" + mainBlockLocation.getBlockX() + ";" + mainBlockLocation.getBlockY() + ";" + mainBlockLocation.getBlockZ();

            //name, material, world, x, y, z, yaw, pitch


            StringBuilder formattedHomeList = new StringBuilder();
            if (claimData.getHomeList() != null) {
                for (ClaimHome home : claimData.getHomeList()) {

                    String format = home.getName() +
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

            long claimTimer = claimData.getTime();

            //is it first save?
            if (claimsInSQL.containsKey(claimData.getClaimID().toString())) {
                //no, so update.
                Debug.log("Updating new row for: " + claimData.getClaimID(), true);
                statement.executeUpdate("UPDATE claims SET" +
                        " owner = '" + owner +
                        "', memberList = '" + memberList +
                        "', chunkList = '" + chunkList +
                        "', energy = '" + claimTimer +
                        "', mainBlock = '" + mainBlock +
                        "', permissions = '" + formattedPermissionState +
                        "', homeList = '" + formattedHomeList +
                        "' WHERE claimID = '" + claimID +
                        "'");
            } else {
                //yes, so add.
                Debug.log("Creating new row for: " + claimData.getClaimID(), true);
                statement.executeUpdate("INSERT INTO claims VALUES('" + owner +
                        "', '" + memberList +
                        "', '" + chunkList +
                        "', '" + claimID +
                        "', '" + claimTimer +
                        "', '" + mainBlock +
                        "', '" + formattedPermissionState +
                        "', '" + formattedHomeList +
                        "')");
            }


        }
    }

    private OfflinePlayer deserializeOwner(String rawOwner) {
        return stringUUIDtoOfflinePlayer(rawOwner);
    }

    private List<ChunkID> deserializeChunkList(String stringChunkList) {
        List<ChunkID> chunkList = new ArrayList<>();
        for (String rawChunk : stringChunkList.split(",")) {
            chunkList.add(new ChunkID(rawChunk.replace(";", ",")));
        }
        return chunkList;
    }

    private List<OfflinePlayer> deserializeMemberList(String stringMemberList) {
        List<OfflinePlayer> memberList = new ArrayList<>();
        for (String memberUUID : stringMemberList.split(",")) {
            memberList.add(stringUUIDtoOfflinePlayer(memberUUID));
        }
        return memberList;
    }

    private HashMap<String, ClaimPermission> deserializePermissions(String stringPermission, ChunkID id) {
        HashMap<String, ClaimPermission> permissionStates = new HashMap<>();
        for (String data : stringPermission.split(",")) {
            String[] datas = data.split(";");
            if (datas[0] == null || datas[0].equalsIgnoreCase("")) continue;
            List<String> permissions = new ArrayList<>(Arrays.asList(datas).subList(1, datas.length));
            permissionStates.put(datas[0], new ClaimPermission(plugin, datas[0], id, permissions));
        }
        return permissionStates;
    }

    private Location deserializeMainBlock(String mainBlock) {
        String[] mainBlockData = mainBlock.split(";");
        return new Location(Bukkit.getWorld(mainBlockData[0]), stringToInt(mainBlockData[1]), stringToInt(mainBlockData[2]), stringToInt(mainBlockData[3]));

    }

    private List<ClaimHome> deserializeClaimHome(String home) {
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


    public HashMap<String, ClaimData> getClaimDataFromDB(boolean async) throws SQLException {


        HashMap<String, ClaimData> resultClaimData = new HashMap<>();
        String sql = "SELECT * FROM claims";

        if (statement == null) {
            Bukkit.getLogger().severe("[DATA_BACKUP] SQL STATEMENT IS NULL!");
            return null;
        }
        if (statement.isClosed()) {
            Bukkit.getLogger().severe("[DATA_BACKUP] SQL STATEMENT IS CLOSED");
            return null;
        }

        int dataAmount = 0;
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            dataAmount++;
            ChunkID id = new ChunkID(rs.getString("claimID"));
            int energy = rs.getInt("energy");

            OfflinePlayer owner = deserializeOwner(rs.getString("owner"));
            List<ChunkID> chunkList = deserializeChunkList(rs.getString("chunkList"));
            List<OfflinePlayer> memberList = deserializeMemberList(rs.getString("memberList")); //TODO Use UUID
            HashMap<String, ClaimPermission> permissionStates = deserializePermissions(rs.getString("permissions"), id);
            Location mainBlock = deserializeMainBlock(rs.getString("mainBlock"));
            List<ClaimHome> homeList = deserializeClaimHome(rs.getString("homeList"));


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
            resultClaimData.put(id.toString(), cd[0]);


        }
        Debug.log("Toplam veri: " + dataAmount, true);
        return resultClaimData;

    }


    public boolean deleteClaimData(ClaimData cd) {

        Debug.log("Deleting new row for: " + cd.getClaimID().toString());
        try {
            String cmd = "DELETE FROM claims WHERE claimID = '" + cd.getClaimID() + "'";
            statement.executeUpdate(cmd);
            return true;
        } catch (SQLException throwables) {
            Bukkit.getLogger().severe("ClaimData silme operasyonunda bir problem ortaya çıktı.");
            throwables.printStackTrace();
        }
        return false;
    }

    public void saveSQLDatas(boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveSQLDatas_operation(true));
        else saveSQLDatas_operation(false);

    }
    private void saveSQLDatas_operation(boolean async) {
        Bukkit.getLogger().info("SQL database backup started (ASYNC: " + async + ")");
        if (connection != null && statement != null) {
            long aaa = System.nanoTime();

            try {
                updateClaimData(async);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            long bbb = System.nanoTime();
            Bukkit.getLogger().info("SQL database backup ended. (" + (double) (bbb - aaa) / 1000000.0 + "ms)");
        } else {
            Bukkit.getLogger().info("SQL Data saving started without connection or sql statement. blokClaims cancelled it.");
        }

    }



    //ALWAYS ASYNC
/*
 public void updateClaimLog(ChunkID claimID, ClaimLogState logState) throws SQLException {

        if (logState.contains()) {
            //no, so update.
            Debug.log("Updating new row for (LOG DB): " + claimData.getClaimID().getChunkID());
            BlokClaims.claimdataStatement.executeUpdate("UPDATE logs SET" +
                    " owner = '" + owner +
                    "', memberList = '" + memberList +
                    "', chunkList = '" + chunkList +
                    "', energy = '" + claimTimer +
                    "', mainBlock = '" + mainBlock +
                    "', permissions = '" + formattedPermissionState +
                    "', homeList = '" + formattedHomeList +
                    "' WHERE claimID = '" + claimID +
                    "'");
        } else {
            //yes, so add.
            Debug.log("Creating new row for (LOG DB): " + claimData.getClaimID().getChunkID());
            BlokClaims.claimdataStatement.executeUpdate("INSERT INTO logs VALUES('" + owner +
                    "', '" + memberList +
                    "', '" + chunkList +
                    "', '" + claimID +
                    "', '" + claimTimer +
                    "', '" + mainBlock +
                    "', '" + formattedPermissionState +
                    "', '" + formattedHomeList +
                    "')");
        }

    }

 */
}


