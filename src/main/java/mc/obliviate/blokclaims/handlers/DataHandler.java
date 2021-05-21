package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.HashMap;

public class DataHandler {



    /**                  Main Chunk, ClaimData */
    private final HashMap<ChunkID, ClaimData> allClaimDataList = new HashMap<>();

    /**Any Chunk, Main Chunk*/
    private final HashMap<ChunkID, ChunkID> allChunkList = new HashMap<>();

    public HashMap<ChunkID, ChunkID> getAllChunkList() {
        return allChunkList;
    }

    public HashMap<ChunkID, ClaimData> getAllClaimDataList() {
        return allClaimDataList;
    }




}
