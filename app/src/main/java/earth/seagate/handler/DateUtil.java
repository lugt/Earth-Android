package earth.seagate.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by God on 2017/2/9.
 */
public class DateUtil {

    public static String formatLong(Long currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        Date dt = new Date(currentTime);
        return sdf.format(dt);
    }
}
