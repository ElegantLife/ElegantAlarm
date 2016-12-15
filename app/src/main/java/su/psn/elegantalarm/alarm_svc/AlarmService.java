package su.psn.elegantalarm.alarm_svc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

/**
 * 闹钟的后台服务部分。
 * Created by A-SC on 2016/12/8.
 */

public class AlarmService extends Service {

    private static final String TAG = "AlarmService";

    public static final String PER_SHUT_DOWN = "android.intent.action.ACTION_PER_SHUTDOWN";
    public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";
    public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";

    public static final String START_ALARM_ACTION = "su.psn.elegantalarm.START_ALARM";
    public static final String STOP_ALARM_ACTION="su.psn.elegantalarm.STOP_ALARM";

    private Context mContext = null;
    private TelephonyManager mTelephonyManager;
    private int mInitialCallState;

    // private AlarmInstance alarmInstance;

    private final BroadcastReceiver mStopPlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public static void stopAlarm(Context context) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
