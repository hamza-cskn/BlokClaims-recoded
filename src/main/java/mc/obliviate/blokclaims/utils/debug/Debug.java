package mc.obliviate.blokclaims.utils.debug;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Debug {

    public static void log(String log, boolean deep) {
        if (!deep) {
            Bukkit.broadcastMessage("[BlockClaims DEBUG] " + log);

        }
    }

    public static void log(String log) {
        log(log, false);

    }

    public static void warn(String log) {
        Bukkit.getLogger().severe("[BlokClaims - Uyarı]" + log);
        for (OfflinePlayer op : Bukkit.getOperators()) {
            if (op.isOnline()) {
                Player oop = (Player) op;
                oop.sendMessage("[BlokClaims - Uyarı]" + log);
            }
        }
    }

}
