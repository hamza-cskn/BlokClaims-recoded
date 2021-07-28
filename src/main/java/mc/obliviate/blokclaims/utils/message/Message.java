package mc.obliviate.blokclaims.utils.message;

import mc.obliviate.blokclaims.handlers.ConfigHandler;
import mc.obliviate.blokclaims.utils.debug.Debug;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;

public class Message {

    private static String prefix;

    public Message() {
        prefix = color(ConfigHandler.messages.getString("prefix"));
    }

    public static YamlConfiguration getMessageConfig() {
        return ConfigHandler.messages;
    }

    public static String getConfigMessage(String name) {
        return getConfigMessage(name, false);
    }

    public static String getConfigMessage(String name, boolean noPrefix) {
        String message = ConfigHandler.messages.getString(name);
        Debug.log("sending config message: " + name + ", " + message + "...", true);

        if (message != null) {
            if (message.startsWith("[NO_PREFIX]")) {
                noPrefix = true;
                message = message.replace("[NO_PREFIX]", "");
            }
            if (noPrefix || prefix == null) {
                return color(message);
            }

            return prefix + color(message);

        }
        return "";
    }


    public static String color(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        str = str.replace("<#orange>", ChatColor.of(new Color(255, 128, 70)).toString())
                .replace("<#red>", ChatColor.of(new Color(255, 69, 69)).toString())
                .replace("<#yellow>", ChatColor.of(new Color(255, 205, 70)).toString())
                .replace("<#green>", ChatColor.of(new Color(169, 255, 70)).toString())
                .replace("<#aqua>", ChatColor.of(new Color(99, 255, 174)).toString())
                .replace("<#blue>", ChatColor.of(new Color(72, 168, 252)).toString())
                .replace("<#purple>", ChatColor.of(new Color(176, 55, 205)).toString())
                .replace("<#pink>", ChatColor.of(new Color(255, 71, 210)).toString())
                .replace("<#dark_blue>", ChatColor.of(new Color(50, 67, 123)).toString())
                .replace("<#gray>", ChatColor.of(new Color(139, 166, 170)).toString());
        return str;
    }

}
