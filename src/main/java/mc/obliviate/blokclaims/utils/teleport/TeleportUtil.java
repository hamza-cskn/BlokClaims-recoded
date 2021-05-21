package mc.obliviate.blokclaims.utils.teleport;

import com.destroystokyo.paper.Title;
import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeleportUtil {

    private final BlokClaims plugin;

    public TeleportUtil(BlokClaims plugin) {
        this.plugin = plugin;
    }

    private final List<String> teleportState = new ArrayList<>();


    public interface Callback {
        void confirm(boolean isTeleported);
    }

    public void teleportHome(Player player, ClaimHome home, int teleportTime) {
        teleportHome(player, home, teleportTime, event -> {});
    }

    public void teleportHome(Player player, ClaimHome home, int teleportTime, Callback callback) {
        if (player == null || home == null) {
            callback.confirm(false);
            return;
        }
        if (teleportState.contains(player.getName())) {
            callback.confirm(false);
            return;
        }
        Location location = home.getLoc();

        teleportState.add(player.getName());

        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();

        int z = player.getLocation().getBlockZ();
        new BukkitRunnable() {
            int counter = 0;

            public void run() {
                if (x != player.getLocation().getBlockX() || y != player.getLocation().getBlockY() || z != player.getLocation().getBlockZ()) {
                    String title = Message.getConfigMessage("home.teleport-cancelled.title")
                            .replace("&", "§");
                    String subtitle = Message.getConfigMessage("home.teleport-cancelled.subtitle")
                            .replace("&", "§");
                    player.sendTitle(new Title(title, subtitle, 0, 20, 20));
                    teleportState.remove(player.getName());
                    callback.confirm(false);
                    cancel();
                    return;
                } if (counter == teleportTime) {
                    String title = Message.getConfigMessage("home.teleported.title")
                            .replace("{home}", home.getName())
                            .replace("&", "§");
                    String subtitle = Message.getConfigMessage("home.teleported.subtitle")
                            .replace("{home}", home.getName())
                            .replace("&", "§");
                    player.sendTitle(new Title(title, subtitle, 0, 20, 20));
                    player.teleport(location);
                    teleportState.remove(player.getName());
                    callback.confirm(true);
                    cancel();
                    return;
                } else {
                    String title = Message.getConfigMessage("home.teleporting.title")
                            .replace("{time}", teleportTime - counter + "")
                            .replace("{home}", home.getName())
                            .replace("&", "§");
                    String subtitle = Message.getConfigMessage("home.teleporting.subtitle")
                            .replace("{time}", teleportTime - counter + "")
                            .replace("{home}", home.getName())
                            .replace("&", "§");
                    player.sendTitle(new Title(title, subtitle, 0, 20, 0));
                }
                counter++;
            }
        }.runTaskTimer(plugin, 0, 20);

    }
}
