package mc.obliviate.blokclaims.utils.timer;

import java.util.concurrent.TimeUnit;

public class TimerUtils {
    public static String getFormattedTime(long time) {
        return getFormattedTime(time, 0);
    }

    public static String getFormattedTime(long time, int mode) {

        long day;
        long hour;
        day = TimeUnit.MINUTES.toDays(time);
        time -= (day * 24 * 60);
        hour = TimeUnit.MINUTES.toHours(time);
        time -= (hour * 60);

        switch (mode) {
            case 1:
                return day + "gün " + hour + "sa " + time + "dk ";
            case 2:
                return day + "gün, " + hour + "saat, " + time + "dk ";
            default:
                return day + " gün " + hour + " saat " + time + " dakika ";
        }

    }

}
