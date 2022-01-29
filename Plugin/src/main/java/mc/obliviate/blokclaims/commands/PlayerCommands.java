package mc.obliviate.blokclaims.commands;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.blokclaims.utils.message.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands extends ListenerHandler implements CommandExecutor {


	public PlayerCommands(BlokClaims plugin) {
		super(plugin);
	}

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


		/**
		 * #Player Side
		 * claim create
		 * claim map
		 * claim list
		 * claim help
		 * claim invites
		 * claim setclaimstone
		 * claim accept [player]
		 * claim decline [player]
		 * claim invite <player>
		 * claim kick <player>
		 * claim sethome <home name>
		 *
		 *
		 * #Admin Side
		 * claims reload
		 * claims list
		 */

		//PLAYER
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("claim")) {

				if (args.length == 1) {
					switch (args[0]) {
						case "create":
							if (getPlugin().getEconomyHandler().has(player, getPlugin().getConfigHandler().getInt("prices.create-claim"))) {
								getPlugin().getClaimManager().createClaim(player, player.getLocation());
							}
							break;
						case "map":
							player.sendMessage(MessageUtils.parseColor("<#red>Bu özellik henüz komutlara eklenmedi."));
							break;
						case "list":

							player.sendMessage(MessageUtils.parseColor("<#red>Bu özellik henüz komutlara eklenmedi."));
							break;
						case "setclaimstone":

							player.sendMessage(MessageUtils.parseColor("<#red>Bu özellik henüz komutlara eklenmedi."));
							break;
						case "invites":

							player.sendMessage(MessageUtils.parseColor("<#red>Bu özellik henüz komutlara eklenmedi."));
							break;
						case "accept":

							player.sendMessage(MessageUtils.parseColor("<#red>Bu özellik henüz komutlara eklenmedi."));
							break;
						case "decline":

							player.sendMessage(MessageUtils.parseColor("<#red>Bu özellik henüz komutlara eklenmedi."));
							break;
						case "invite":
							MessageUtils.sendMessage(player, "usage.invite");
							break;
						case "kick":
							MessageUtils.sendMessage(player, "usage.kick");
							break;
						case "sethome":
							MessageUtils.sendMessage(player, "usage.sethome");
							break;
						default:
							MessageUtils.sendMessage(player, "usage.no-argument-found", new PlaceholderUtil().add("{argument}", args[0]));
					}

				} else if (args.length == 2) {


				} else if (args.length == 3) {

				} else if (args.length == 4) {

				}


			}
			//CONSOLE
		} else {
			Bukkit.getLogger().info("Konsol bu komutu kullanmamalı.");
		}
		return false;
	}
}
