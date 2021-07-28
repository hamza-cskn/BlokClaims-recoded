package mc.obliviate.blokclaims.utils.debug;

import mc.obliviate.blokclaims.utils.message.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.logging.Level;

public class Debug {


    public enum DebugType {
        RED,
        ORANGE,
        YELLOW,
        LIME,
        GREEN,
        BLUE,
        PURPLE,
        WHITE,
        GRAY,
        DARK_GRAY,
    }

    public static void log(String log, boolean deep) {
        log(log, deep, DebugType.GRAY);
    }
    public static void log(String log, boolean deep, DebugType debugType) {

        if (false && !deep) {
            String color = "";
            switch (debugType) {
                case RED:
                    color = ChatColor.of(new Color(255,0,0)).toString();
                    break;
                case ORANGE:
                    color = ChatColor.of(new Color(255, 135, 66)).toString();
                    break;
                case YELLOW:
                    color = ChatColor.of(new Color(255, 225, 56)).toString();
                    break;
                case LIME:
                    color = ChatColor.of(new Color(215, 255, 82)).toString();
                    break;
                case GREEN:
                    color = ChatColor.of(new Color(0,255,0)).toString();
                    break;
                case BLUE:
                    color = ChatColor.of(new Color(67, 172, 232)).toString();
                    break;
                case PURPLE:
                    color = ChatColor.of(new Color(150, 67, 232)).toString();
                    break;
                case WHITE:
                    color = ChatColor.of(new Color(255, 255, 255)).toString();
                    break;
                case DARK_GRAY:
                    color = ChatColor.of(new Color(181, 181, 181)).toString();
                    break;
            }
            String className = "Class: \n" + Thread.currentThread().getStackTrace()[2].getClassName();
            String methodName = "Method: " + Thread.currentThread().getStackTrace()[2].getMethodName();
            String line = "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber();


            TextComponent text = new TextComponent("§l[DEBUG] §r" + log);
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    className+ "\n\n" +
                            methodName +
                            "\n" +
                            line +
                            "\nFrom: " +
                            Thread.currentThread().getStackTrace()[3] + "\n"
            )));


            for (Player player : Bukkit.getOnlinePlayers())  {
                player.spigot().sendMessage(text);
            }
            Bukkit.getLogger().log(Level.INFO, color + "§l[DEBUG] §r" + color + log);

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
