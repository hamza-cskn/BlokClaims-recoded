package mc.obliviate.blokclaims.utils.gui.animation;

import mc.obliviate.blokclaims.utils.gui.animation.color.Gradient;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextAnimation {

    public String getGradientString(String string, int startDelay, Color... colors) {
        return getGradientString(string, startDelay, null, null, colors);
    }

    public String getGradientString(String string, int startDelay, List<Integer> ignoredSlots, Color... colors) {
        return getGradientString(string, startDelay, null, ignoredSlots, colors);
    }


    public String getGradientString(String string, int startDelay, List<String> ignoredChars, List<Integer> ignoredSlots, Color... colors) {
        return getGradientString(string, startDelay, ignoredChars, ignoredSlots, ChatColor.WHITE + "", colors); }
    public String getGradientString(String string, int startDelay, java.util.List<String> ignoredChars, List<Integer> ignoredSlots, String background, Color... colors) {
        StringBuilder result = new StringBuilder();
        if (ignoredChars == null) ignoredChars = new ArrayList<>();
        if (ignoredSlots == null) ignoredSlots = new ArrayList<>();

        List<String> gradientList = new Gradient(string.length()/2, true, colors).getGradient();
        int i = gradientList.size();
        while (i < string.length()) {
            gradientList.add(background);
            i++;
        }


        int k = 0;

        String[] chars = string.split("");

        //Loop all characters of the string
        while (k < string.length()) {

            //wait start delay to paint character
            if (startDelay > k) {result.append(background).append(ChatColor.BOLD + "").append(chars[k]);}
            //check index out of bounds exception
            else if (k < chars.length) {
                //if valid, paint character
                if (k < gradientList.size() && !ignoredChars.contains(chars[k]) && !ignoredSlots.contains(k)) {
                    result.append(gradientList.get(Math.min(k - startDelay, gradientList.size() - 1))).append(ChatColor.BOLD + "").append(chars[k]);
                }
                //reset colors
                else result.append(background).append(ChatColor.BOLD + "").append(chars[k]);

            }
            k++;


        }


        return result.toString();
    }


    public List<String> getFrames_gradientString(String string, Color... colors) {
        return getFrames_gradientString(string, ChatColor.WHITE.toString(), colors);
    }
    public List<String> getFrames_gradientString(String string, String background, Color... colors) {
        List<String> stringList = new ArrayList<>();
        int i = -((string.length()+((colors.length-2)*16))/2);
        while (i < string.length()) {
            stringList.add(getGradientString(string, i, null, null, background, colors));
            i++;
        }
        return stringList;
    }

    public List<String> getWaveAnimation(String string, List<String> gradient1) {
        return getWaveAnimation(string, gradient1, ChatColor.WHITE + "" + ChatColor.BOLD);
    }

    public List<String> getWaveAnimation(String string, List<String> gradient1, String background) {
        List<String> frames = new ArrayList<>();

        int waveLength = Math.max(string.length() * 2, 7);


        int i = -(waveLength - (gradient1.get(0) + ChatColor.BOLD).length());
        int k = 0;
        String color1, color2;
        while (i < string.length()) {

            color1 = gradient1.get(Math.min(k, gradient1.size() - 1)) + ChatColor.BOLD;
            color2 = ChatColor.WHITE + "" + ChatColor.BOLD;

            StringBuilder builder = new StringBuilder(string);

            builder.insert(Math.max(i, 0), color1);

            boolean condition = (i + waveLength < builder.length());
            if (i > 0) {
                builder.insert(0, color2);
                if (condition && i + waveLength + color2.length() < builder.length())
                    builder.insert(i + waveLength + color2.length(), color2);
            } else if (condition) builder.insert(i + waveLength, color2);


            frames.add(builder.toString());
            k++;
            i++;
        }
        return frames;
    }

}
