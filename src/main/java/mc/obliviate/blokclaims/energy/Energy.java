package mc.obliviate.blokclaims.energy;

import mc.obliviate.blokclaims.ChunkID;

import java.util.Objects;

public class Energy {
    ChunkID chunkID;
    int chunks;
    long energy;

    public Energy(ChunkID chunkID, long energy) {
        this.chunkID = chunkID;
        this.energy = energy;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getEnergyDecreasement() { return Math.max(chunks,1);/*Every chunk removes 1 minute */ }
    public long getAmount() { return energy; }
    public void setEnergy(long energy) { this.energy = energy; }
    public void addEnergy(long energy) { this.energy += energy; }

    public void removeEnergy(long energy) {
        if (this.energy > energy) this.energy -= energy;
        else this.energy = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Energy)) return false;
        Energy energy = (Energy) o;
        return chunkID.equals(energy.chunkID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkID);
    }


}
