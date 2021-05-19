package mc.obliviate.blokclaims.utils.gui.animation.color;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gradient {

    List<String> gradient;
    int amount;



    public Gradient(int amount, boolean appendReverse, Color... colors) {

        int colorAmount = amount/colors.length;

        int n1,n2,i = 0;
        List<String> fromTo = new ArrayList<>();


        while (i+1 < colors.length) {

            n1 = i;
            n2 = i+1;

            int red1 = colors[n1].getRed();
            int red2 = colors[n2].getRed();
            int green1 = colors[n1].getGreen();
            int green2 = colors[n2].getGreen();
            int blue1 = colors[n1].getBlue();
            int blue2 = colors[n2].getBlue();
            final double[] redG = linear(red1, red2, colorAmount);
            final double[] greenG = linear(green1, green2, colorAmount);
            final double[] blueG = linear(blue1, blue2, colorAmount);

            for (int times = 0; times < colorAmount; times++) {
                fromTo.add(ChatColor.of(new Color(
                        (int) Math.round(redG[times]),
                        (int) Math.round(greenG[times]),
                        (int) Math.round(blueG[times])))
                        + "");
            }
            i++;

        }

        List<String> resultList = new ArrayList<>(fromTo);
        if (appendReverse) {
            Collections.reverse(fromTo);
            resultList.addAll(fromTo);
        }

        this.amount = amount;
        this.gradient = resultList;

    }

    private double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    public List<String> getGradient() {
        return gradient;
    }

    public int getAmount() {
        return amount;
    }
}
