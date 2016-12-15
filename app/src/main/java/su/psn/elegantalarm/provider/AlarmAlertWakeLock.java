package su.psn.elegantalarm.provider;

import android.content.Context;
import android.os.PowerManager;

/**
 * AlarmAlertWakeLock的作用就是一个WakeLock的工具类，在工具类中我们可以创建WakeLock，可以释放WakeLock。
 * 而我们为什么需要WakeLock呢，在官方文档我们可以看到如下内容：
 * The Alarm Manager holds a CPU wake lock as long as the alarm receiver's onReceive() method is executing.
 * If your alarm receiver called Context.startService(),
 * it is possible that the phone will sleep before the requested service is launched.
 * To prevent this, your BroadcastReceiver and Service will need to implement a separate wake lock policy to
 * ensure that the phone continues running until the service becomes available.
 *
 * Created by A-SC on 2016/12/8.
 */

public class AlarmAlertWakeLock {

    // 电源管理的唤醒锁
    private static PowerManager.WakeLock sCpuWakeLock;

    // Create a new wake lock of PartialWakeLock to ensures CPU is running;
    private static PowerManager.WakeLock createPartialWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");
    }

    /**
     * acquire cpu's wake lock.
     *
     * @param context context
     */
    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        sCpuWakeLock = createPartialWakeLock(context);
        sCpuWakeLock.acquire();
    }

    /**
     * Release wake lock;
     * It is an important step to release wake lock.
     */
    public static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
