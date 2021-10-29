package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
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


    public void updateClaimData(boolean async) throws SQLException {


        //int createdRowAmount = 0;
        //int updatedRowAmount = 0;
        HashMap<ChunkID, ClaimData> claimsInSQL = getClaimDataFromDB(async);
        for (ClaimData claimData : plugin.getDataHandler().getAllClaimDataList().values()) {
            //Owner = UUID of Owner
            String owner = serializeOwner(claimData.getOwner());

            String chunkList = serializeChunkList(claimData.getChunkList());

            HashMap<UUID, ClaimPermission> permissionStates = new HashMap<>();
            for (UUID uuid : claimData.getMemberList()) {
                permissionStates.put(uuid, claimData.getPermissionState(uuid));
            }
            String formattedPermissionState = serializePermissions(permissionStates);

            String memberList = serializeMemberList(claimData.getMemberList());

            //ClaimID = mainChunk's ID
            String claimID = claimData.getClaimID().toString();

            //mainBlockLocation = main block of claim
            String mainBlock = serializeMainBlock(claimData.getMainBlock());

            //name, material, world, x, y, z, yaw, pitch

            String formattedHomeList = serializeClaimHomeList(claimData.getHomeList());

            long claimTimer = claimData.getEnergy().getAmount();


            String sql;
            //is it first save?
            if (claimsInSQL.containsKey(claimData.getClaimID())) {
                //no, so update.
                Debug.log("Updating new row for: " + claimData.getClaimID(), true);
                sql = "UPDATE claims SET" +
                        " owner = '" + owner +
                        "', memberList = '" + memberList +
                        "', chunkList = '" + chunkList +
                        "', energy = '" + claimTimer +
                        "', mainBlock = '" + mainBlock +
                        "', permissions = '" + formattedPermissionState +
                        "', homeList = '" + formattedHomeList +
                        "' WHERE claimID = '" + claimID +
                        "'";
                //updatedRowAmount++;
            } else {
                //yes, so add.
                Debug.log("Creating new row for: " + claimData.getClaimID(), true);
                sql = "INSERT INTO claims VALUES('" + owner +
                        "', '" + memberList +
                        "', '" + chunkList +
                        "', '" + claimID +
                        "', '" + claimTimer +
                        "', '" + mainBlock +
                        "', '" + formattedPermissionState +
                        "', '" + formattedHomeList +
                        "')";
                //createdRowAmount++;
            }
            statement.executeUpdate(sql);
            //Bukkit.broadcastMessage(sql);
        }
        /*
        Bukkit.getLogger().info(Message.color("&8&m------------&r <#red>[BlokClaims - Database Task Result] &8&m------------"));
        Bukkit.getLogger().info(Message.color("<#orange> data amount: <#yellow>" + claimsInSQL.size()));
        Bukkit.getLogger().info(Message.color("<#orange> is async:  <#yellow>" + !Bukkit.isPrimaryThread()));
        Bukkit.getLogger().info(Message.color("<#orange> created row:  <#yellow>" + createdRowAmount ));
        Bukkit.getLogger().info(Message.color("<#orange> updated row:  <#yellow>" + updatedRowAmount));
        Bukkit.getLogger().info(Message.color("<#green> SQL database update task has completed."));
        Bukkit.getLogger().info(Message.color("&8&m------------&r <#red>[BlokClaims - Database Task Result] &8&m------------"));
         */


    }

    public void createTableIfNotExits() {
        try {
            Debug.log("Veritabanına başarıyla bağlanıldı.");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS claims (" +
                    "owner TEXT, " +
                    "memberList TEXT, " +
                    "chunkList TEXT, " +
                    "claimID TEXT NOT NULL, " +
                    "energy INTEGER, " +
                    "mainBlock TEXT, " +
                    "permissions TEXT," +
                    "homeList TEXT" +
                    ")");
            //statement.executeUpdate("CREATE TABLE IF NOT EXISTS logs (" + "logID INTEGER, " + "logType TEXT, " + "logDate TEXT, " + "logData TEXT " + ")");

            putClaimDatas();
            putChunkDatas();

        } catch (SQLException e) {
            e.printStackTrace();
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

    public HashMap<ChunkID, ClaimData> getClaimDataFromDB(boolean async) throws SQLException {

        HashMap<ChunkID, ClaimData> resultClaimData = new HashMap<>();
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

            UUID owner = deserializeOwner(rs.getString("owner"));
            List<ChunkID> chunkList = deserializeChunkList(rs.getString("chunkList"));
            Debug.log(chunkList + "", false, Debug.DebugType.ORANGE);
            List<UUID> memberList = deserializeMemberList(rs.getString("memberList"));
            HashMap<UUID, ClaimPermission> permissionStates = deserializePermissions(rs.getString("permissions"), id);
            Location mainBlock = deserializeMainBlock(rs.getString("mainBlock"));
            List<ClaimHome> homeList = deserializeClaimHomeList(rs.getString("homeList"));


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

    public void save(boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveSQLDatabase(true));
        else saveSQLDatabase(false);
    }

    private void saveSQLDatabase(boolean async) {
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

    private OfflinePlayer stringUUIDtoOfflinePlayer(String ownerUUID) {
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("[BlokClaims - SQL Operation] INVALID UUID: " + ownerUUID);
        }
        return null;
    }

    /**
     * SERIALIZE & DESERIALIZE METHODS
     */

    private UUID deserializeOwner(String rawOwner) {
        return UUID.fromString(rawOwner);
    }

    private String serializeOwner(UUID owner) {
        return owner.toString();
    }


    private <T> void reverseList(List<T> list) {
        // base case: the list is empty, or only one element is left
        if (list == null || list.size() <= 1) {
            return;
        }

        // remove the first element
        T value = list.remove(0);

        // recur for remaining items
        reverseList(list);

        // insert the top element back after recurse for remaining items
        list.add(value);
    }

    private List<ChunkID> deserializeChunkList(String stringChunkList) {
        /**
         * flat;7;0,
         * flat;8;0,
         * flat;9;0,
         * flat;9;1,
         **/
        List<ChunkID> chunkList = new ArrayList<>();
        for (String rawChunk : stringChunkList.split(",")) {
            ChunkID id = new ChunkID(rawChunk.replace(";", ","));
            chunkList.add(id);
        }
        reverseList(chunkList);
        return chunkList;
    }

    private String serializeChunkList(List<ChunkID> chunkIdlist) {
        //ChunkList = world1;x1;y1, world2;x2;y2
        StringBuilder chunkList = new StringBuilder();
        for (ChunkID chunkid : chunkIdlist) {
            String formattedChunk = chunkid.toString().replace(",", ";");
            chunkList.insert(0, formattedChunk + ",");
        }
        return chunkList.toString();
    }

    private List<UUID> deserializeMemberList(String stringMemberList) {
        List<UUID> memberList = new ArrayList<>();
        for (String memberUUID : stringMemberList.split(",")) {
            memberList.add(UUID.fromString(memberUUID));
        }
        return memberList;
    }

    private String serializeMemberList(List<UUID> memberList) {
        //

        //MemberList = Member1, Member2, Member3...
        StringBuilder result = new StringBuilder();
        for (UUID member : memberList) {
            String uuid = member.toString();
            result.insert(0, uuid + ",");

            //PermissionStates putting

        }
        return result.toString();


    }

    private HashMap<UUID, ClaimPermission> deserializePermissions(String stringPermission, ChunkID id) {
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

            permissionStates.put(UUID.fromString(datas[0]), new ClaimPermission(plugin, UUID.fromString(datas[0]), id, permissions));
        }
        return permissionStates;
    }

    private String serializePermissions(HashMap<UUID, ClaimPermission> permissionStates) {

        StringBuilder formattedPermissionState = new StringBuilder();
        //Format Permission States
        for (UUID key : permissionStates.keySet()) {
            StringBuilder format = new StringBuilder();
            ClaimPermission cps = permissionStates.get(key);
            if (cps == null) continue;
            for (ClaimPermissionType permission : cps.getPermissions()) {
                format.insert(0, permission + ";");
            }
            formattedPermissionState.insert(0, key + ";" + format + ",");
        }

        return formattedPermissionState.toString();

    }

    private Location deserializeMainBlock(String mainBlock) {
        String[] mainBlockData = mainBlock.split(";");
        return new Location(Bukkit.getWorld(mainBlockData[0]), stringToInt(mainBlockData[1]), stringToInt(mainBlockData[2]), stringToInt(mainBlockData[3]));

    }

    private String serializeMainBlock(Location mainBlockLocation) {
        return mainBlockLocation.getWorld().getName() + ";" +
                mainBlockLocation.getBlockX() + ";" +
                mainBlockLocation.getBlockY() + ";" +
                mainBlockLocation.getBlockZ();
    }

    private List<ClaimHome> deserializeClaimHomeList(String home) {
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

    private String serializeClaimHomeList(List<ClaimHome> homeList) {
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


