package mc.obliviate.blokclaims.utils.claim.chunkcheckers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.claim.ClaimManager;
import mc.obliviate.blokclaims.utils.debug.Logger;

public class ClaimableChunks {

    private final ClaimManager cm;

    private boolean connected = false;
    private boolean noConflict = true;

    private int x;
    private int z;

    private ClaimableState result;


    enum ClaimableState {
        CONFLICT_FOUND,
        CLAIMABLE,
        MAIN_CHUNK_CLAIMABLE,
        CLAIMED,
        UNKNOWN
    }


    public ClaimableChunks(BlokClaims plugin, String claimID, String chunkId) {
        this.cm = plugin.getClaimManager();
        checkClaimable(claimID, chunkId);
    }

    //Scans 4 chunks to find claimable statement.
    private void checkClaimable(String claimID, String chunkID) {


        final Claim claim = cm.getClaimData(chunkID);
        if (claim != null) {
            result = ClaimableState.CLAIMED;
            return;
        }

        //List<ClaimData> nearClaimDatas = scanNearChunks(chunk);

        String[] datas = chunkID.split(",");


        int cX = Integer.parseInt(datas[1]);
        int cZ = Integer.parseInt(datas[2]);
        int[] n = {1, 0, -1};

        int[] connectionXZ = new int[3];
        for (int x : n) {
            for (int z : n) {
                //kontrol edilen chunk kendisi değilse, (0, 0)
                if (!(x == 0 && z == 0)) {



                    final Claim cd = cm.getClaimData(new ChunkID((cX + x), (cZ + z), datas[0]));
                    //claim data is not empty
                    if (cd != null) {
                        //kontrol edilen chunk sahibi oyuncu ise.
                        if (cd.getClaimID().toString().equalsIgnoreCase(claimID)) {
                            //alt,üst,sağ,sol ise; doğrudan bağ var demektir.
                            if (x == 0 || z == 0) {
                                if (!connected) {
                                    connected = true;
                                    connectionXZ = new int[]{cX + x, cZ + z};
                                }
                            }
                        } else {
                            //etrafında alınmış claim chunku var demektir.
                            noConflict = false;
                        }
                    }
                }
            }
        }
        Logger.debug("connection: " + connected + ", noConflict: " + noConflict, true);
        if (!noConflict) {
            result = ClaimableState.CONFLICT_FOUND;
        } else if (connected) {
            result = ClaimableState.CLAIMABLE;
            x = connectionXZ[0];
            z = connectionXZ[1];
        } else {
            result = ClaimableState.MAIN_CHUNK_CLAIMABLE;
            x = cX;
            z = cZ;

        }
    }


    public boolean isConnected() {
        return connected;
    }

    public boolean isNoConflict() {
        return noConflict;
    }

    public ClaimableState getResult() {
        return result;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
    public boolean isClaimable() {
        return result.equals(ClaimableState.CLAIMABLE);
    }

}
