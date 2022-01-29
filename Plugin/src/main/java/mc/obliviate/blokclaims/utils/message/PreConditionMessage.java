package mc.obliviate.blokclaims.utils.message;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class PreConditionMessage {

	public static boolean isArgNull(Object arg, CommandSender sender, String command) {
		return isNull(arg, sender, "usage." + command);
	}

	public static boolean isOffline(OfflinePlayer player, CommandSender sender) {
		if (isNull(player, sender, "target-is-not-online")) return true;
		if (!player.isOnline()) {
			MessageUtils.sendMessage(sender, "target-is-not-online");
			return true;
		}
		return false;
	}

	public static boolean isNull(Object object, CommandSender sender, String message) {
		if (object == null) {
			MessageUtils.sendMessage(sender, message);
			return true;
		}
		return false;
	}

}
