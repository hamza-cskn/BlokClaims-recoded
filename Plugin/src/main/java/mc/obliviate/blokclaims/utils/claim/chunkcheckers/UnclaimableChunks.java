package mc.obliviate.blokclaims.utils.claim.chunkcheckers;

import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.claim.ClaimManager;

import java.util.ArrayList;
import java.util.List;

public class UnclaimableChunks {

    private final ChunkID id;
    private final Claim cd;
    private final List<ChunkID> confirmedChunks = new ArrayList<>();
    private final ClaimManager cm;
    private boolean connected = false;

    public UnclaimableChunks(ClaimManager cm, Claim cd, ChunkID id) {
        this.cd = cd;
        this.id = id;
        this.cm = cm;

        if (cd.getChunkList().size() > 2) {
            List<ChunkID> chunks = new ArrayList<>();
            chunks.add(cd.getClaimID());
            checkConnection(chunks);
        } else {
            connected = true;
        }
        return;
    }

    private void checkConnection(List<ChunkID> chunks) {
        if (chunks.size() == 0) {
            //                                                   -1 because, you did simulation chunks without chunk you want delete
            connected = confirmedChunks.size() == cd.getChunkList().size() - 1;
            //if sizes is equal. that means connected.
            return;
        }

        ChunkID chunkScanningNear = chunks.get(0);

        int[] n = {1, 0, -1};
        for (int x : n) {
            for (int z : n) {

                if (!(x == 0 && z == 0) && (x == 0 || z == 0)) {


                    ChunkID chunkScanning = new ChunkID(cd.getMainBlock().getWorld().getName() + "," + (chunkScanningNear.getX() + x) + "," + (chunkScanningNear.getZ() + z));
                    if (!chunkScanning.equals(id)) {
                        chunks.remove(chunkScanningNear);

                        Claim cd_scanning = cm.getClaimData(chunkScanning);


                        //mark with checked
                        if (cd_scanning == cd) {
                            //if claimdata is same, add to chunks to search
                            //if its not confirmed before.
                            if (!confirmedChunks.contains(chunkScanning)) {
                                //mark with confirmed
                                confirmedChunks.add(chunkScanning);
                                //and add to chunks waiting to scan
                                chunks.add(chunkScanning);
                            }
                        }
                    }
                }
            }
        }


        checkConnection(chunks);
    }

    public boolean isConnected() {
        return connected;
    }

    public List<ChunkID> getConfirmedChunks() {
        return confirmedChunks;
    }
}
