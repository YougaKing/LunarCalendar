package com.youga.lunarcalendar;

import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LunarSolarConverter {


    private static final long MIN_TIME_MILLIS, MAX_TIME_MILLIS;
    /**
     * 农历年数据表(1900-2100年)
     * 每个农历年用16进制来表示，解析时转为2进制
     * 二进码长度为16位,如解码为15位高位补0,(当解码为17位时,最高位1即所闰月位大月,反之为小月)
     * 前12位分别表示12个农历月份的大小月，1是大月，0是小月
     * 最后4位表示闰月，转为十进制后即为闰月值，例如0110，则为闰6月
     */

    private final static int[] LUNAR_INFO = {
            0x10, 0x4ae0, 0xa570, 0x54d5, 0xd260, 0xd950, 0x16554, 0x56a0, 0x9ad0, 0x55d2,
            0x4ae0, 0xa5b6, 0xa4d0, 0xd250, 0x1d255, 0xb540, 0xd6a0, 0xada2, 0x95b0, 0x14977,
            0x4970, 0xa4b0, 0xb4b5, 0x6a50, 0x6d40, 0x1ab54, 0x2b60, 0x9570, 0x52f2, 0x4970,
            0x6566, 0xd4a0, 0xea50, 0x16a95, 0x5ad0, 0x2b60, 0x186e3, 0x92e0, 0x1c8d7, 0xc950,
            0xd4a0, 0x1d8a6, 0xb550, 0x56a0, 0x1a5b4, 0x25d0, 0x92d0, 0xd2b2, 0xa950, 0xb557,
            0x6ca0, 0xb550, 0x15355, 0x4da0, 0xa5b0, 0x14573, 0x52b0, 0xa9a8, 0xe950, 0x6aa0,
            0xaea6, 0xab50, 0x4b60, 0xaae4, 0xa570, 0x5260, 0xf263, 0xd950, 0x5b57, 0x56a0,
            0x96d0, 0x4dd5, 0x4ad0, 0xa4d0, 0xd4d4, 0xd250, 0xd558, 0xb540, 0xb6a0, 0x195a6,
            0x95b0, 0x49b0, 0xa974, 0xa4b0, 0xb27a, 0x6a50, 0x6d40, 0xaf46, 0xab60, 0x9570,
            0x4af5, 0x4970, 0x64b0, 0x74a3, 0xea50, 0x6b58, 0x5ac0, 0xab60, 0x96d5, 0x92e0,
            0xc960, 0xd954, 0xd4a0, 0xda50, 0x7552, 0x56a0, 0xabb7, 0x25d0, 0x92d0, 0xcab5,
            0xa950, 0xb4a0, 0xbaa4, 0xad50, 0x55d9, 0x4ba0, 0xa5b0, 0x15176, 0x52b0, 0xa930,
            0x7954, 0x6aa0, 0xad50, 0x5b52, 0x4b60, 0xa6e6, 0xa4e0, 0xd260, 0xea65, 0xd530,
            0x5aa0, 0x76a3, 0x96d0, 0x26fb, 0x4ad0, 0xa4d0, 0x1d0b6, 0xd250, 0xd520, 0xdd45,
            0xb5a0, 0x56d0, 0x55b2, 0x49b0, 0xa577, 0xa4b0, 0xaa50, 0x1b255, 0x6d20, 0xada0,
            0x14b63, 0x9370, 0x49f8, 0x4970, 0x64b0, 0x168a6, 0xea50, 0x6aa0, 0x1a6c4, 0xaae0,
            0x92e0, 0xd2e3, 0xc960, 0xd557, 0xd4a0, 0xda50, 0x5d55, 0x56a0, 0xa6d0, 0x55d4,
            0x52d0, 0xa9b8, 0xa950, 0xb4a0, 0xb6a6, 0xad50, 0x55a0, 0xaba4, 0xa5b0, 0x52b0,
            0xb273, 0x6930, 0x7337, 0x6aa0, 0xad50, 0x14b55, 0x4b60, 0xa570, 0x54e4, 0xd160,
            0xe968, 0xd520, 0xdaa0, 0x16aa6, 0x56d0, 0x4ae0, 0xa9d4, 0xa2d0, 0xd150, 0xf252,
            0xd520
    };

    private final static String[] LUNAR_MONTH_ARRAYS = {"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"};
    private final static String[] LUNAR_DAY_ARRAYS = {"初", "十", "廿", "卅"};
    private final static String[] NUMBERS = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1901, 0, 1, 0, 0, 0);//公历1901-1-1 即农历1900-11-11
        MIN_TIME_MILLIS = calendar.getTimeInMillis();
        calendar.set(2100, 11, 31, 23, 59, 59);//公历2100-12-31 即农历2100-12-1
        MAX_TIME_MILLIS = calendar.getTimeInMillis();
    }

    /**
     * 根据时间毫秒值转换 农历
     *
     * @param timeInMillis 时间毫秒值
     * @return Lunar 农历对象
     */
    public static Lunar converterDate(long timeInMillis) {
        if (timeInMillis < MIN_TIME_MILLIS || timeInMillis > MAX_TIME_MILLIS) {
            throw new RuntimeException("日期超出农历计算范围,-->minDate:1900-1-1 maxDate 2100-12-31");
        }
        Lunar lunar = new Lunar();
        // 距离起始日期间隔的总天数 间隔天数和目标日期差一天
        long offset = (timeInMillis - MIN_TIME_MILLIS) / (24 * 60 * 60 * 1000);
        // 默认农历年为1900年，且由此开始推算农历年份
        int lunarYear = 1900;
        while (true) {
            int daysInLunarYear = getLunarYearDays(lunarYear);
            if (offset > daysInLunarYear) {
                offset -= daysInLunarYear;
                lunarYear++;
            } else {
                break;
            }
        }
        lunar.year = lunarYear;
        // 获取该农历年的闰月月份
        int leapMonth = getLunarLeapMonth(lunarYear);
        // 没有闰月则不是闰年
        lunar.leapMonth = leapMonth;
        // 默认农历月为正月，且由此开始推荐农历月
        int lunarMonth = lunarYear == 1900 ? 11 : 1;
        int daysInLunarMonth;
        // 递减每个农历月的总天数,确定农历月份,先计算非闰月后计算闰月
        while (true) {
            if (lunarMonth == leapMonth) { // 该农历年闰月的天数,先算正常月再算闰月 如果润一月 先减去一月再减去润一月
                daysInLunarMonth = getLunarDays(lunarYear, lunarMonth);
                if (offset > daysInLunarMonth) {//剩余天数>当月天数
                    offset -= daysInLunarMonth;//减去差额
                    if (offset > getLunarLeapDays(lunarYear)) {//剩余天数>闰月天数
                        offset -= getLunarLeapDays(lunarYear);//减去闰月天数
                        lunarMonth++;//月份+1
                    } else {
                        lunarMonth = lunarYear;//标记闰月为当前年
                        break;
                    }
                } else {
                    break;
                }
            } else { // 该农历年正常农历月份的天数
                daysInLunarMonth = getLunarDays(lunarYear, lunarMonth);
                if (offset > daysInLunarMonth) {//剩余天数>当月天数
                    offset -= daysInLunarMonth;//减去差额
                    lunarMonth++;//月份+1
                } else {
                    break;
                }
            }
        }

        lunar.month = lunarMonth;
        lunar.day = (lunarYear == 1900 && lunarMonth == 11) ? (int) Math.abs(-offset + -11) : (int) offset;
        return lunar;
    }

    /**
     * 获取某农历年的总天数
     *
     * @param lunarYear 农历年份
     * @return 该农历年的总天数
     */
    private static int getLunarYearDays(int lunarYear) {
        if (lunarYear == 1900) {
            return 48;//1900天只有48天的数据
        }
        // 按小月计算,农历年最少有12 * 29 = 348天
        int daysInLunarYear = 348;
        // 遍历前12位
        for (int i = 0x8000; i > 0x8; i >>= 1) {
            // 每个大月累加一天
            daysInLunarYear += ((LUNAR_INFO[lunarYear - 1900] & i) != 0) ? 1 : 0;
        }
        // 加上闰月天数
        daysInLunarYear += getLunarLeapDays(lunarYear);

        return daysInLunarYear;
    }

    /**
     * 获取某农历年闰月的总天数
     *
     * @param lunarYear 农历年份
     * @return 该农历年闰月的天数, 无闰月返回0 (闰月的天数等于所润月的天数)
     */
    private static int getLunarLeapDays(int lunarYear) {
        // 计算所闰月为大月还是小月 如果该年的二进制码为17位,最高位为1则该年闰月为大月,反之则为小月（2017/2055）
        // 若该年没有闰月,返回0
        return getLunarLeapMonth(lunarYear) > 0 ? ((LUNAR_INFO[lunarYear - 1900] & 0x10000) > 0 ? 30 : 29) : 0;
    }

    /**
     * 获取某农历年闰月月份
     *
     * @param lunarYear 农历年份
     * @return 该农历年闰月的月份, 四位二进制码即为闰月的月份, 0为不闰月
     */
    private static int getLunarLeapMonth(int lunarYear) {
        // 匹配后4位
        int leapMonth = LUNAR_INFO[lunarYear - 1900] & 0xf;
        leapMonth = (leapMonth == 0xf ? 0 : leapMonth);
        if (leapMonth > 12) {//闰月月份不能大于12 否则数据肯定是错误的
            throw new RuntimeException(lunarYear + "年数据错误,lunarYear:" + Integer.toBinaryString(LUNAR_INFO[lunarYear - 1900]));
        }
        return leapMonth;
    }

    /**
     * 获取某农历年某月的总天数
     *
     * @param lunarYear 农历年份
     * @return 该农历年某月的天数
     */
    private static int getLunarDays(int lunarYear, int month) {
        if (lunarYear == 1900 && month == 11) {
            return 18;//1900年11月18天
        }
        return (LUNAR_INFO[lunarYear - 1900] & (0x10000 >> month)) != 0 ? 30 : 29;
    }


    public static class Lunar {
        public int year;
        public int month;
        public int day;
        public int leapMonth;

        public String getMonth() {
            return month > LUNAR_MONTH_ARRAYS.length ? "润" + LUNAR_MONTH_ARRAYS[leapMonth - 1] : LUNAR_MONTH_ARRAYS[month - 1];
        }

        public String getDay() {
            int result = day / 10;
            return LUNAR_DAY_ARRAYS[(result == 1 && day % 10 == 0) ? result - 1 : result] +
                    (day % 10 == 0 ? NUMBERS[NUMBERS.length - 1] : NUMBERS[Math.abs(day - result * 10) - 1]);
        }

        @Override
        public String toString() {
            return "Lunar{" +
                    "年=" + year +
                    ", 月=" + month +
                    ", 日=" + day +
                    ", 闰月=" + leapMonth +
                    ", 月/日:" + getMonth() + "月" + getDay() +
                    '}';
        }
    }
}