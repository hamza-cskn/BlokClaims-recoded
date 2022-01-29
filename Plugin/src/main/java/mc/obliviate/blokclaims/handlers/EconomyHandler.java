package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EconomyHandler {

    private final BlokClaims plugin;

    public EconomyHandler(BlokClaims plugin) {
        this.plugin = plugin;
    }

    public boolean has(OfflinePlayer player, double cost) {
        double money = plugin.getEconomy().getBalance(player);
        if (money > 1000) {
            return plugin.getEconomy().depositPlayer(player, 1000).type.equals(EconomyResponse.ResponseType.SUCCESS);
        } else {
            if (player.isOnline()) {
                ((Player) player).sendMessage(Message.getConfigMessage("you-can-not-afford").replace("{need}", (1000 - money) + ""));
            }
            return false;
        }
    }


}
