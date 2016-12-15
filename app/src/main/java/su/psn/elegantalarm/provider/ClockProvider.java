package su.psn.elegantalarm.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by SUCONG on 2016/12/9.
 */

public class ClockProvider extends ContentProvider {

    private static final String TAG = "ClockProvider";

    private ClockDatabaseHelper mOpenHelper;

    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    private static final int INSTANCES = 3;
    private static final int INSTANCES_ID = 4;

    private static final UriMatcher sUrlMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUrlMatcher.addURI(ClockContract.AUTHORITY, "alarms", ALARMS);
        sUrlMatcher.addURI(ClockContract.AUTHORITY, "alarms/#", ALARMS_ID);
        sUrlMatcher.addURI(ClockContract.AUTHORITY, "instances", INSTANCES);
        sUrlMatcher.addURI(ClockContract.AUTHORITY, "instances/#", INSTANCES_ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ClockDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // 创建查询构造器
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int match = sUrlMatcher.match(uri);
        switch (match) {
            case ALARMS:
                queryBuilder.setTables(ClockDatabaseHelper.ALARMS_TABLE_NAME);
                break;

            case ALARMS_ID:
                queryBuilder.setTables(ClockDatabaseHelper.ALARMS_TABLE_NAME);
                queryBuilder.appendWhere(ClockContract.AlarmsColumns._ID + "=");
                queryBuilder.appendWhere(uri.getLastPathSegment());
                break;

            case INSTANCES:
                queryBuilder.setTables(ClockDatabaseHelper.INSTANCE_TABLE_NAME);
                break;

            case INSTANCES_ID:
                queryBuilder.setTables(ClockDatabaseHelper.INSTANCE_TABLE_NAME);
                queryBuilder.appendWhere(ClockContract.InstanceColumns._ID + "=");
                queryBuilder.appendWhere(uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        // 查询的时候得到的是只读数据库
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor == null) {
            Log.i(TAG, "query failed");
        } else {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long rowId;

        // 打开数据库
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();

        // 得到新插入行的id
        int match = sUrlMatcher.match(uri);
        switch (match) {
            case ALARMS:
                rowId = database.insert(ClockDatabaseHelper.ALARMS_TABLE_NAME, ClockContract.AlarmsColumns.RINGTONE, values);
                break;

            case INSTANCES:
                rowId = database.insert(ClockDatabaseHelper.INSTANCE_TABLE_NAME, ClockContract.InstanceColumns.RINGTONE, values);
                break;

            default:
                throw new IllegalArgumentException("Cannot insert form URL" + uri);
        }

        // 将指定的id追加到uri的path上，同时返回这个uri
        Uri uriResult = ContentUris.withAppendedId(uri, rowId);

        // contentResolver需要去通知一下它的观察者，比如CursorAdapter,在数据更新之后第一时间得到更新。
        getContext().getContentResolver().notifyChange(uriResult, null);

        // 返回新插入条目的uri地址。
        return uriResult;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        int count;
        String primaryKey;
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        switch (sUrlMatcher.match(uri)) {
            case ALARMS_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = ClockContract.AlarmsColumns._ID + "=" + primaryKey;
                } else {
                    where = ClockContract.AlarmsColumns._ID + "=" + primaryKey + "AND (" + where + ")";
                }
                // 通过删除得到受影响的行数
                count = database.delete(ClockDatabaseHelper.ALARMS_TABLE_NAME, where, whereArgs);
                break;

            case INSTANCES_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = ClockContract.AlarmsColumns._ID + "=" + primaryKey;
                } else {
                    where = ClockContract.AlarmsColumns._ID + "=" + primaryKey + "AND (" + where + ")";
                }
                count = database.delete(ClockDatabaseHelper.INSTANCE_TABLE_NAME, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Cannot delete from URL:" + uri);
        }

        // 发出数据有变更的通知。
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        String id = uri.getLastPathSegment();
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        switch (sUrlMatcher.match(uri)) {
            case ALARMS_ID:
                count = database.update(ClockDatabaseHelper.ALARMS_TABLE_NAME, values, ClockContract.AlarmsColumns._ID + "=" + id, null);
                break;

            case INSTANCES_ID:
                count = database.update(ClockDatabaseHelper.INSTANCE_TABLE_NAME, values, ClockContract.InstanceColumns._ID + "=" + id, null);
                break;

            default:
                throw new IllegalArgumentException("Cannot update from URL:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUrlMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            case INSTANCES:
                return "vnd.android.cursor.dir/instances";
            case INSTANCES_ID:
                return "vnd.android.cursor.item/instances";
            default:
                throw new IllegalArgumentException("Unknown URL" + uri);
        }
    }
}
