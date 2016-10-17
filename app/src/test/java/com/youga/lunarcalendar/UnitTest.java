package com.youga.lunarcalendar;

import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {


    @org.junit.Test
    public void test() {
        Calendar calendar = Calendar.getInstance();
//        calendar.set(2100, 11, 31);

        System.out.println("DATE:" + calendar.get(Calendar.DATE));
        System.out.println(LunarSolarConverter.converterDate(calendar.getTimeInMillis()));

    }

}