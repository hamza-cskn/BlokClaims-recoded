package mc.obliviate.blokclaims.commands;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands extends ListenerHandler {


    public PlayerCommands(BlokClaims plugin) {
        super(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        //PLAYER
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("blokClaim")) {

                if (args.length == 0) {
                    switch (args[0]) {
                        case "":
                    }

                } else if (args.length == 1) {

                } else if (args.length == 2) {

                } else if (args.length == 3) {

                }


            }
            //CONSOLE
        } else {
            Bukkit.getLogger().info("Konsol bu komutu kullanmamalı.");
        }
        return false;
    }
}
