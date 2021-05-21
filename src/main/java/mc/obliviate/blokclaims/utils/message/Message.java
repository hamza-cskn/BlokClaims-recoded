package mc.obliviate.blokclaims.utils.message;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.handlers.ConfigHandler;
import mc.obliviate.blokclaims.utils.debug.Debug;
import org.bukkit.ChatColor;

import java.awt.*;

public class Message {

    private static String prefix;

    public Message() {
        prefix = color(ConfigHandler.messages.getString("prefix"));
    }

    public static String getConfigMessage(String name) {
        return getConfigMessage(name, false);
    }

    public static String getConfigMessage(String name, boolean doNotUsePrefix) {
        Debug.log("sending config message" + name, true);
        String message = ConfigHandler.messages.getString(name);

        if (message != null) {
            if (message.startsWith("[NO_PREFIX]")) {
                doNotUsePrefix = true;
                message = message.replace("[NO_PREFIX]", "");
            }
            if (doNotUsePrefix) {
                prefix = "";
            }
            if (prefix != null) {
                return color(prefix + message);
            }
        }
        return "";
    }



    public static String color(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        str = str.replace("<#orange>", net.md_5.bungee.api.ChatColor.of(new Color(255, 128, 70)).toString())
                .replace("<#red>", net.md_5.bungee.api.ChatColor.of(new Color(255, 69, 69)).toString())
                .replace("<#yellow>", net.md_5.bungee.api.ChatColor.of(new Color(255, 205, 70)).toString())
                .replace("<#green>", net.md_5.bungee.api.ChatColor.of(new Color(169, 255, 70)).toString())
                .replace("<#aqua>", net.md_5.bungee.api.ChatColor.of(new Color(99, 255, 174)).toString())
                .replace("<#blue>", net.md_5.bungee.api.ChatColor.of(new Color(72, 168, 252)).toString())
                .replace("<#purple>", net.md_5.bungee.api.ChatColor.of(new Color(176, 55, 205)).toString())
                .replace("<#pink>", net.md_5.bungee.api.ChatColor.of(new Color(255, 71, 210)).toString());
        return str;
    }

}
