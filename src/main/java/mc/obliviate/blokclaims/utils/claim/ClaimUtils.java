package mc.obliviate.blokclaims.utils.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.utils.debug.Debug;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClaimUtils {


    // GET CHUNK_ID
    public static ChunkID getChunkID(Chunk chunk) {
        if (chunk != null) {
            return new ChunkID(chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ());
        }
        Debug.log("ChunkID(chunk) is null");
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
        Debug.log("ChunkID(async chunk) is null");
        return null;
    }

    public static ChunkID getChunkID(Location loc) {
        if (loc != null) {
            return new ChunkID(loc.getWorld().getName() + "," + (int) loc.getX() / 16 + "," + (int) loc.getZ() / 16);
        }
        Debug.log("ChunkID(loc) is null");
        return null;
    }

    public static ChunkID getChunkID(World world, int x, int z, boolean isLocation) {
        int n = 1;
        if (isLocation) {
            n = 16;
        }
        if (world != null) {
            return new ChunkID(world.getName() + "," + x / n + "," + z / n);
        }
        Debug.log("ChunkID(wxz) is null");
        return null;
    }


    //
    public void broadcastClaimMembers(ClaimData cd, String str) {
        for (UUID uuid : cd.getMemberList()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            p.sendMessage(str);
        }
    }

    public static boolean isClaimWorld(World world) {
        return BlokClaims.getWorldList().contains(world.getName());
    }




}
