package mc.obliviate.blokclaims.utils.timer;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.debug.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private final BlokClaims plugin;

    public Timer(BlokClaims plugin) {
        this.plugin = plugin;
        start();
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                    for (Claim data : plugin.getDataHandler().getAllClaimDataList().values()) {
                        Bukkit.getScheduler().runTask(plugin, () -> data.setEnergy(plugin, data.getEnergy().getAmount() - data.getEnergy().getEnergyDecreasement()));

                    }
                    Logger.debug("Timer checked.", true);


                });
            }
        }.runTaskTimer(plugin, 20, 1200);

        final long period = plugin.getConfigHandler().getConfig().getInt("backup-interval", 300) * 20L;
        if (period <= 0) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getSqlManager().save(true);

            }
        }.runTaskTimer(plugin, period, period);
    }


}
