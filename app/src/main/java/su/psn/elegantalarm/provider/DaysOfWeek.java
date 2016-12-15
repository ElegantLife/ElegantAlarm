package su.psn.elegantalarm.provider;

import android.content.Context;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashSet;

import su.psn.elegantalarm.R;

/**
 * Created by A-SC on 2016/12/9.
 */

public class DaysOfWeek {

    // 7 days of one week.
    public static final int DAYS_IN_A_WEEK = 7;

    // 127 for binary expression = 1111111 and 1111111 likes an array as a week.
    public static final int ALL_DAYS_SET = 127;

    // 0000000 up to ALL_DAYS_SET
    public static final int NO_DAYS_SET = 0;

    // position for which bit need to set in binary week array.
    private int mBitSet;

    public DaysOfWeek(int bitSet) {
        this.mBitSet = bitSet;
    }

    public void setDaysOfWeek(boolean value, int... daysOfWeek) {
        for (int day : daysOfWeek) {
            setBit(convertBitIndexToDay(day), value);
        }
    }

    public void setBitSet(int bitSet) {
        this.mBitSet = bitSet;
    }

    public int getBitSet() {
        return mBitSet;
    }

    /**
     * Using bit operation to finish bit switching.
     *
     * @param bitIdx which bit need to set open or close
     * @param set    set value or cancel value
     */
    private void setBit(int bitIdx, boolean set) {
        if (set) {
            mBitSet |= (1 << bitIdx);
        } else {
            mBitSet &= ~(1 << bitIdx);
        }
    }

    /**
     * Convert bit to which day.
     * But I am puzzled about why +1 at last.
     *
     * @param bitIndex which bit in week binary
     * @return which day
     */
    private static int convertBitIndexToDay(int bitIndex) {
        return (bitIndex + 1) % DAYS_IN_A_WEEK + 1;
    }

    private static int convertDayToBitIndex(int day) {
        return (day + 5) % DAYS_IN_A_WEEK;
    }

    /**
     * Get which days are set to enabled.
     *
     * @return a hast set
     */
    public HashSet<Integer> getSetDays() {
        final HashSet<Integer> result = new HashSet<>();
        for (int bitIdx = 0; bitIdx < DAYS_IN_A_WEEK; bitIdx++) {
            if (isBitEnabled(bitIdx)) {
                result.add(convertBitIndexToDay(bitIdx));
            }
        }

        return result;
    }

    /**
     * Get days to next alarm.
     *
     * @param current the calendar you used in that day.
     * @return how many days to next alarm.
     */
    public int calculateDaysToNextAlarm(Calendar current) {
        if (!isRepeating()) return -1;

        int dayCount = 0;
        int currentDayBit = convertDayToBitIndex(current.get(Calendar.DAY_OF_WEEK));

        for (; dayCount < DAYS_IN_A_WEEK; dayCount++) {
            int nextAlarmBit = (currentDayBit + dayCount) % DAYS_IN_A_WEEK;
            if (isBitEnabled(nextAlarmBit)) break;
        }

        return dayCount;
    }

    /**
     * 利用位运算去查看某一个bit是否是启用的。
     *
     * @param bitIndex 启动位
     * @return 是否启动
     */
    private boolean isBitEnabled(int bitIndex) {
        return (mBitSet & (1 << bitIndex)) > 0;
    }

    /**
     * Check whether all bits are enabled or not.
     *
     * @return is a repeat alarm
     */
    public boolean isRepeating() {
        return mBitSet != NO_DAYS_SET;
    }

    /**
     * Clear all bits set.
     */
    public void clearAllDays() {
        mBitSet = NO_DAYS_SET;
    }

    private String toString(Context context, boolean showNever, boolean forAccessibility) {
        StringBuilder ret = new StringBuilder();

        if (mBitSet == NO_DAYS_SET) {
            return showNever ? context.getText(R.string.never).toString() : "";
        }

        if (mBitSet == ALL_DAYS_SET) {
            return context.getText(R.string.every_day).toString();
        }

        // count selected days
        int dayCount = 0;
        int bitSet = mBitSet;
        while (bitSet > 0) {
            if ((bitSet & 1) == 1) dayCount++;
            bitSet >>= 1;
        }

        DateFormatSymbols dfm = new DateFormatSymbols();
        String[] dayList = (forAccessibility || dayCount <= 1)
                ? dfm.getWeekdays() : dfm.getShortWeekdays();

        for (int bitIdx = 0; bitIdx < DAYS_IN_A_WEEK; bitIdx++) {
            if ((mBitSet & (1 << bitIdx)) != 0) {
                ret.append(dayList[convertBitIndexToDay(bitIdx)]);
                if (dayCount > 0) ret.append(context.getText(R.string.app_name));
            }
        }

        return ret.toString();
    }

//    public static void main(String[] args) {
//        DaysOfWeek daysOfWeek = new DaysOfWeek();
//        daysOfWeek.mBitSet = 0b0000110;
//        HashSet<Integer> integers = daysOfWeek.getSetDays();
//        for (int item : integers) {
//            System.out.println(item + "");
//        }
//    }
}
