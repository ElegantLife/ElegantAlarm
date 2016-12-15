package su.psn.elegantalarm.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

/**
 * Alarm的实体类
 * Created by HX-SC on 2016/12/14.
 */

public class Alarm implements Parcelable, ClockContract.AlarmsColumns {

    // 存db时的那个id
    public long id;

    // 闹钟是否启用
    public boolean enabled;

    // 小时
    public int hour;

    // 分钟
    public int minutes;

    // 一周七天的实例
    public DaysOfWeek daysOfWeek;

    // 是否震动
    public boolean vibrate;

    // 附加的标签
    public String label;

    // ringtone的uri
    public Uri alert;

    // 是否在使用过一次后就删除该闹钟
    public boolean deleteAfterUse;

    // for cursor to use  : ColumnIndex
    private static final int ID_INDEX = 0;
    private static final int ENABLED_INDEX = 1;
    private static final int HOUR_INDEX = 2;
    private static final int MINUTES_INDEX = 3;
    private static final int DAYS_OF_WEEK_INDEX = 4;
    private static final int VIBRATE_INDEX = 5;
    private static final int LABEL_INDEX = 6;
    private static final int RINGTONE_INDEX = 7;
    private static final int DELETE_AFTER_USE_INDEX = 8;
    private static final int COLUMN_COUNT = DELETE_AFTER_USE_INDEX + 1;

    public Alarm(int hour, int minutes) {
        this.id = INVALID_ID;
        this.enabled = false;
        this.hour = hour;
        this.minutes = minutes;
        this.daysOfWeek = new DaysOfWeek(0);
        this.vibrate = true;
        this.label = "";
        this.alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.deleteAfterUse = false;
    }

    /**
     * 通过Cursor来构建Alarm实例。
     *
     * @param cursor 从db得到的cursor
     */
    public Alarm(Cursor cursor) {
        id = cursor.getLong(ID_INDEX);
        enabled = cursor.getInt(ENABLED_INDEX) == 1;
        hour = cursor.getInt(HOUR_INDEX);
        minutes = cursor.getInt(MINUTES_INDEX);
        daysOfWeek = new DaysOfWeek(cursor.getInt(DAYS_OF_WEEK_INDEX));
        vibrate = cursor.getInt(VIBRATE_INDEX) == 1;
        label = cursor.getString(LABEL_INDEX);
        deleteAfterUse = cursor.getInt(DELETE_AFTER_USE_INDEX) == 1;

        // 如果ringtone这一列是空的话，那么就使用默认类型的alarm。
        if (cursor.isNull(RINGTONE_INDEX)) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        } else {
            alert = Uri.parse(cursor.getString(RINGTONE_INDEX));
        }
    }

    Alarm(Parcel p) {
        id = p.readLong();
        enabled = p.readInt() == 1;
        hour = p.readInt();
        minutes = p.readInt();
        daysOfWeek = new DaysOfWeek(p.readInt());
        vibrate = p.readInt() == 1;
        label = p.readString();
        alert = p.readParcelable(null);
        deleteAfterUse = p.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeLong(id);
        p.writeInt(enabled ? 1 : 0);
        p.writeInt(hour);
        p.writeInt(minutes);
        p.writeInt(daysOfWeek.getBitSet());
        p.writeInt(vibrate ? 1 : 0);
        p.writeString(label);
        p.writeParcelable(alert, flags);
        p.writeInt(deleteAfterUse ? 1 : 0);
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel p) {
            return new Alarm(p);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    public String getLabelOrDefault(Context context) {
        if (TextUtils.isEmpty(label)) {
            return "Alarm Label";
        }

        return label;
    }

    // 默认的排序顺序
    private static final String DEFAULT_SORT_ORDER =
            HOUR + "," + MINUTES + " ASC" + ", " + _ID + " DESC";

    // 需要在db table中查询的列
    private static final String[] QUERY_COLUMNS = {
            _ID,
            ENABLED,
            HOUR,
            MINUTES,
            DAYS_OF_WEEK,
            VIBRATE,
            LABEL,
            RINGTONE,
            DELETE_AFTER_USE
    };

    public static ContentValues createContentValues(Alarm alarm) {
        ContentValues values = new ContentValues(COLUMN_COUNT);
        if (alarm.id != INVALID_ID) {
            values.put(ClockContract.AlarmsColumns._ID, alarm.id);
        }

        values.put(ENABLED, alarm.enabled ? 1 : 0);
        values.put(HOUR, alarm.hour);
        values.put(MINUTES, alarm.minutes);
        values.put(DAYS_OF_WEEK, alarm.daysOfWeek.getBitSet());
        values.put(VIBRATE, alarm.vibrate ? 1 : 0);
        values.put(LABEL, alarm.label);
        values.put(DELETE_AFTER_USE, alarm.deleteAfterUse ? 1 : 0);
        if (alarm.alert == null) {
            values.putNull(RINGTONE);
        } else {
            values.put(RINGTONE, alarm.alert.toString());
        }

        return values;
    }

    public static Uri getUri(long alarmId) {
        return ContentUris.withAppendedId(CONTENT_URI, alarmId);
    }

    public static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }

    public static CursorLoader getAlarmsCursorLoader(Context context) {
        return new CursorLoader(context, ClockContract.AlarmsColumns.CONTENT_URI, QUERY_COLUMNS, null, null, DEFAULT_SORT_ORDER);
    }

    public static Alarm getAlarm(ContentResolver contentResolver, long alarmId) {
        Cursor cursor = contentResolver.query(getUri(alarmId), QUERY_COLUMNS, null, null, null);
        Alarm result = null;
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                result = new Alarm(cursor);
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}
