package mc.obliviate.blokclaims.utils.energy;

import mc.obliviate.blokclaims.energy.Energy;

public class EnergyUtils {
    public static double getEnergyCost(int chunks, int seed) {
        double ro = 1;
        if (chunks >= 25) ro = 3;
        else if (chunks >= 20) ro = 2;
        else if (chunks >= 15) ro = 1.5;
        return (((((1440*((seed*1.5)/ ro )))/1440)+ chunks )*(chunks *0.3))*360;
    }

    public static long convertEnergyToTime(int chunks, Energy energy) {
        return energy.getAmount()/Math.max(energy.getEnergyDecreasement(), 1);
    }

}
