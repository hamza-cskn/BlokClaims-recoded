package mc.obliviate.blokclaims.commands.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.message.PreConditionMessage;
import me.despical.commandframework.Command;
import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.Completer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ClaimCMD {

	private final BlokClaims plugin;

	public ClaimCMD(BlokClaims plugin) {
		this.plugin = plugin;
	}

	@Command(name = "claim",
			aliases = {"claims"},
			usage = "/claim help",
			senderType = Command.SenderType.BOTH
	)
	public void claim(final CommandArguments arguments) {

	}

	@Completer(name = "claim", aliases = {"claims"})
	public List<String> claimCompleter(final CommandArguments arguments) {
		return Arrays.asList("map","invite","decline","accept");
	}

	@Command(name = "claim.invite", senderType = Command.SenderType.PLAYER)
	public void claimInvite(final CommandArguments arguments) {
		final String targetName = arguments.getArgument(1);
		if (PreConditionMessage.isArgNull(targetName, arguments.getSender(), "invite")) return;

		final Player target = Bukkit.getPlayerExact(targetName);
		if (PreConditionMessage.isOffline(target, arguments.getSender())) return;



	}




}
