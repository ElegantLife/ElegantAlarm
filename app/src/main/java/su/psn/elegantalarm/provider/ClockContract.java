package su.psn.elegantalarm.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by SUCONG on 2016/12/9.
 */

public class ClockContract {
    // as a content provider to provide some data, so it needs a AUTHORITY.
    public static final String AUTHORITY = "su.psn.elegantalarm.myclock";

    private ClockContract() {
    }

    // columns are needed in alarm settings
    private interface AlarmSettingColumns extends BaseColumns {
        long INVALID_ID = -1;
        String VIBRATE = "vibrate";
        String LABEL = "label";
        String RINGTONE = "ringtone";
    }

    // columns for alarm
    protected interface AlarmsColumns extends AlarmSettingColumns {
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/alarms");
        Uri NO_RINGTONE_URI = Uri.EMPTY;
        String NO_RINGTONE = NO_RINGTONE_URI.toString();

        // 0 - 23
        String HOUR = "hour";

        // 0 - 59
        String MINUTES = "minutes";
        String DAYS_OF_WEEK = "daysofweek";
        String ENABLED = "enabled";
        String DELETE_AFTER_USE = "delete_after_use";
    }

    // columns
    protected interface InstanceColumns extends AlarmSettingColumns {
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/instances");
        int SILENT_STATE = 0;
        int LOW_NOTIFICATION_STATE = 1;
        int HIDE_NOTIFICATION_STATE = 2;
        int HIGH_NOTIFICATION_STATE  = 3;
        int SNOOZE_STATE = 4;
        int FIRED_STATE = 5;
        int MISSED_STATE = 6;
        int DISMISSED_STATE = 7;

        String YEAR = "year";
        String MONTH = "month";
        String DAY = "day";
        String HOUR = "hour";
        String MINUTES = "minutes";
        String ALARM_ID = "alarm_id";
        String ALARM_STATE = "alarm_state";
    }
}
