package com.example.universalcalendar.extensions

import android.util.Log
import com.example.universalcalendar.common.Strings
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.math.sin

object DateUtils {

    private const val PI = Math.PI
    private const val TAG = "LunarCoreHelper"

    /**
     * In China, Vietnam and other East Asian countries,
     * we use the sexagenary cycle, also known as the Stems-and-Branches.
     * It appears as a means of recording days in the first Chinese written texts.
     * We have 10 Heavenly Stems and 12 Earthly Branches which make 60 Stem-Branch pairs.
     * From those pairs, we can "guess" which day is good, which day is not, based on some traditional rules.
     * For more info: https://en.wikipedia.org/wiki/Sexagenary_cycle
     *
     * Yin Yang name will be added later
     */
    // 10 Heavenly Stems
    // Vietnamese Heavenly Stems (or "Thiên Can")
    private val CAN = arrayOf(
        "Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh",
        "Tân", "Nhâm", "Quý"
    )

    // Chinese Heavenly Stems (or "天干")
    private val STEMS = arrayOf(
        "甲", "乙", "丙", "丁", "戊", "己", "庚",
        "辛", "壬", "癸"
    )

    // 12 Earthly Branches
    // Vietnamese Earthly Branches (or "Địa Chi")
    private val CHI = arrayOf(
        "Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ",
        "Mùi", "Thân", "Dậu", "Tuất", "Hợi"
    )

    private val CHIFORMONTH =
        arrayOf("Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi", "Tý", "Sửu")

    // Chinese Earthly Branches (or "地支")
    private val BRANCHES = arrayOf(
        "子", "丑", "寅", "卯", "辰", "巳", "午",
        "未", "申", "酉", "戌", "亥"
    )

    /**
     * Based on some traditional rules (again!), on each month,
     * we have 4 Chi(s)/Branches which is called "good", and 4 Chi(s)/Branches which called "bad".
     * The next 2-dimensional arrays are the lists of "good days" and "bad days".
     * We Vietnamese actually have other terms for those two. But we haven't found any English word for that.
     * So let's just call them "good days" and "bad days".
     * Reference will be added later.
     *
     * We use Chi/Branch with unaccented syllables to make it easy when comparing string.
     */
    private val goodDays = arrayOf(
        arrayOf("Tý", "sửu", "tỵ", "mui"),
        arrayOf("dần", "mão", "mùi", "dậu"),
        arrayOf("thìn", "tỵ", "dậu", "hợi"),
        arrayOf("ngọ", "mùi", "sửu", "dậu"),
        arrayOf("thân", "dậu", "sửu", "mão"),
        arrayOf("tuất", "hợi", "mão", "tỵ"),
        arrayOf("tý", "sửu", "tỵ", "mùi"),
        arrayOf("dần", "mão", "mùi", "dậu"),
        arrayOf("thìn", "tỵ", "dậu", "hợi"),
        arrayOf("ngọ", "mùi", "sửu", "dậu"),
        arrayOf("thân", "dậu", "sửu", "mão"),
        arrayOf("tuất", "hợi", "mão", "tỵ")
    )

    private val badDays = arrayOf(
        arrayOf("ngọ", "mão", "hợi", "dậu"),
        arrayOf("thân", "tỵ", "sửu", "hợi"),
        arrayOf("tuất", "mùi", "sửu", "hợi"),
        arrayOf("tý", "dậu", "tỵ", "mão"),
        arrayOf("dần", "hợi", "mùi", "tỵ"),
        arrayOf("thìn", "sửu", "dậu", "mùi"),
        arrayOf("ngọ", "mão", "hợi", "dậu"),
        arrayOf("thân", "tỵ", "sửu", "hợi"),
        arrayOf("tuất", "mùi", "sửu", "hợi"),
        arrayOf("tý", "dậu", "tỵ", "mão"),
        arrayOf("dần", "hợi", "mùi", "tỵ"),
        arrayOf("thìn", "sửu", "dậu", "mùi")
    )


    fun isGoodDay(chiDay: String, lunarMonth: Int): Boolean {
        val data = goodDays[lunarMonth - 1]
        val tmp = data.size
        for (i in 0 until tmp) {
            if (data[i].equals(chiDay, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun isBadDay(chiDay: String, lunarMonth: Int): Boolean {
        if (isGoodDay(chiDay, lunarMonth)) return false
        val data = badDays[lunarMonth - 1]
        val tmp = data.size
        for (aData in data) {
            if (aData.equals(chiDay, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    /**
     *
     * @param solarDay
     * @param solarMonth
     * @param solarYear
     * @return days between March 1st 1996 and the input date. Why March 1st 1996, see the "processDayLunar" method below.
     */
    private fun getDateDurationBetweenInputAndPivotDate(
        solarDay: Int,
        solarMonth: Int,
        solarYear: Int
    ): Int {
        val currentCalendar: GregorianCalendar = GregorianCalendar()
        currentCalendar[solarYear, solarMonth - 1, solarDay, 0, 0] = 0
        val checkCalendar: GregorianCalendar = GregorianCalendar()
        checkCalendar[1996, 2, 1, 0, 0] = 0
        return ((currentCalendar.timeInMillis / 1000L - checkCalendar
            .timeInMillis / 1000L) / (60 * 60 * 24)).toInt()
    }

    /**
     *
     * @param solarDay
     * @param solarMonth
     * @param solarYear
     * @return Can-Chi (or Stem-Branch) number of the input date.
     * We make March 1st 1996 a "pivot" date, and start counting Can-Chi from that date.
     * You can see we set iCan = 3, iChi = 9 means the "pivot" day is Đinh Dậu (or 丁酉, or Yin Fire Rooster).
     * So yeah, it's not a special date. Just a "pivot", a starting point to count. You can choose another day, which has another iCan and iChi.
     */
    private fun processDayLunar(solarDay: Int, solarMonth: Int, solarYear: Int): IntArray {
        var iCan = 3
        var iChi = 9
        val numDays = getDateDurationBetweenInputAndPivotDate(solarDay, solarMonth, solarYear)
        //        Log.d(TAG, "Test: " + iCan + "/" + iChi + "/" + numDays);
        if (numDays < 0) {
            iCan = (iCan + numDays % 10 + 10) % 10
            iChi = (iChi + numDays % 12 + 12) % 12
        } else if (numDays > 0) {
            iCan = (iCan + numDays % 10) % 10
            iChi = (iChi + numDays % 12) % 12
        }
        //        Log.d(TAG, "Test2: " + iCan + "/" + iChi + "/" + numDays);
        return intArrayOf(iCan, iChi)
    }

    fun getChiDayLunar(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        val tmp: IntArray =
            processDayLunar(solarDay, solarMonth, solarYear)
        return CHI[tmp[1]]
    }


    fun getCanChiDayLunar(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        return "Ngày ${CAN[(jdn(solarDay, solarMonth, solarYear) + 9) % 10]} ${
            CHI[(jdn(
                solarDay,
                solarMonth,
                solarYear
            ) + 1) % 12]
        }"
    }

    fun getCanYearLunar(solarYear: Int): String {
        return CAN[(solarYear + 6) % 10]
    }

    fun getChiYearLunar(solarYear: Int): String {
        return CHI[(solarYear + 8) % 12]
    }

    fun getCanChiForMonth(solarMonth: Int, solarYear: Int): String {
        val canChiMonth =  ((solarYear * 12) + solarMonth + 3) % 10
        return "Tháng ${CAN[canChiMonth]} ${CHIFORMONTH[solarMonth - 1]}"
    }

    /**
     *
     * @param s
     * @return words unaccented syllables, with an exception of "Đ/đ"
     */
    private fun unAccent(s: String): String {
        val temp: String = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("").replace("Đ", "D").replace("đ", "d")
    }

    /**
     *
     * @param canchi
     * @return Can-Chi with unaccented syllables, with an exception of "Tý" and "Tỵ" which has the same result when unaccented.
     */
    private fun getUnAccentCanChi(canchi: String): String {
        var rs = unAccent(canchi)
        if (canchi.equals("tý", ignoreCase = true)) {
            rs += "s"
        } else if (canchi.equals("tỵ", ignoreCase = true)) {
            rs += "j"
        }
        return rs.lowercase(Locale.getDefault())
    }

    /**
     *
     * @param chiDay
     * @param lunarMonth
     * @return the string showing the input day is good or not
     * We use Can-Chi string with unaccented syllables to compare.
     * And yeah, "rateDay" is not a really good name for this method!
     */
    fun rateDay(chiDay: String, lunarMonth: Int): String? {
        var chiDay = chiDay
        chiDay = getUnAccentCanChi(chiDay)
        return if (isGoodDay(chiDay, lunarMonth)) {
            "Good"
        } else if (isBadDay(chiDay, lunarMonth)) {
            "Bad"
        } else {
            "Normal"
        }
    }


    /**
     *
     * @param dd
     * @param mm
     * @param yy
     * @return the number of days since 1 January 4713 BC (Julian calendar)
     */
    private fun jdFromDate(dd: Int, mm: Int, yy: Int): Int {
        val a = (14 - mm) / 12
        val y = yy + 4800 - a
        val m = mm + 12 * a - 3
        var jd = (dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400
                - 32045)
        if (jd < 2299161) {
            jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083
        }
        // jd = jd - 1721425;
        return jd
    }

    /**
     * http://www.tondering.dk/claus/calendar.html Section: Is there a formula
     * for calculating the Julian day number?
     *
     * @param jd
     * - the number of days since 1 January 4713 BC (Julian calendar)
     * @return
     */
    private fun jdToDate(jd: Int): IntArray? {
        val a: Int
        val b: Int
        val c: Int
        if (jd > 2299160) { // After 5/10/1582, Gregorian calendar
            a = jd + 32044
            b = (4 * a + 3) / 146097
            c = a - b * 146097 / 4
        } else {
            b = 0
            c = jd + 32082
        }
        val d = (4 * c + 3) / 1461
        val e = c - 1461 * d / 4
        val m = (5 * e + 2) / 153
        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = b * 100 + d - 4800 + m / 10
        return intArrayOf(day, month, year)
    }

    fun jdn(i: Int, i2: Int, i3: Int): Int {
        val intValue = Integer.valueOf((14 - i2) / 12).toInt()
        val i4 = i3 + 4800 - intValue
        return i + Integer.valueOf(((i2 + intValue * 12 - 3) * 153 + 2) / 5)
            .toInt() + i4 * 365 + Integer.valueOf(i4 / 4).toInt() - Integer.valueOf(i4 / 100)
            .toInt() + Integer.valueOf(i4 / 400).toInt() - 32045
    }

    /**
     * Solar longitude in degrees Algorithm from: Astronomical Algorithms, by
     * Jean Meeus, 1998
     *
     * @param jdn
     * - number of days since noon UTC on 1 January 4713 BC
     * @return
     */
    private fun SunLongitude(jdn: Double): Double {
        // return CC2K.sunLongitude(jdn);
        return SunLongitudeAA98(jdn)
    }

    private fun SunLongitudeAA98(jdn: Double): Double {
        val T = (jdn - 2415021.0) / 36525 // Time in Julian centuries from
        // 2000-01-01 12:00:00 GMT
        val T2 = T * T
        val dr = PI / 180 // degree to radian
        val M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - (0.00000048
                * T * T2) // mean anomaly, degree
        val L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2 // mean
        // longitude,
        // degree
        var DL = ((1.914600 - 0.004817 * T - 0.000014 * T2)
                * Math.sin(dr * M))
        DL += (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + (0.000290
                * Math.sin(dr * 3 * M))
        var L = L0 + DL // true longitude, degree
        L -= 360 * INT(L / 360) // Normalize to (0, 360)
        return L
    }

    private fun NewMoon(k: Int): Double {
        // return CC2K.newMoonTime(k);
        return NewMoonAA98(k)
    }

    /**
     * Julian day number of the kth new moon after (or before) the New Moon of
     * 1900-01-01 13:51 GMT. Accuracy: 2 minutes Algorithm from: Astronomical
     * Algorithms, by Jean Meeus, 1998
     *
     * @param k
     * @return the Julian date number (number of days since noon UTC on 1
     * January 4713 BC) of the New Moon
     */
    private fun NewMoonAA98(k: Int): Double {
        val T = k / 1236.85 // Time in Julian centuries from 1900 January
        // 0.5
        val T2 = T * T
        val T3 = T2 * T
        val dr = PI / 180
        var Jd1 = (2415020.75933 + 29.53058868 * k + 0.0001178 * T2
                - 0.000000155 * T3)
        Jd1 += 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr) // Mean
        // new
        // moon
        val M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - (0.00000347
                * T3) // Sun's mean anomaly
        val Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + (0.00001236
                * T3) // Moon's mean anomaly
        val F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - (0.00000239
                * T3) // Moon's argument of latitude
        var C1 = (0.1734 - 0.000393 * T) * sin(M * dr) + 0.0021 * sin(2 * dr * M)
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr)
        C1 -= 0.0004 * sin(dr * 3 * Mpr)
        C1 = C1 + 0.0104 * sin(dr * 2 * F) - 0.0051 * sin(dr * (M + Mpr))
        C1 = C1 - 0.0074 * sin(dr * (M - Mpr)) + 0.0004 *sin(dr * (2 * F + M))
        C1 = C1 - 0.0004 * sin(dr * (2 * F - M)) - (0.0006
                * sin(dr * (2 * F + Mpr)))
        C1 += 0.0010 *
                sin(dr * (2 * F - Mpr)) + (0.0005
                * sin(dr * (2 * Mpr + M)))
        val deltat: Double = if (T < -11) {
            0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3
        } else {
            -0.000278 + 0.000265 * T + 0.000262 * T2
        }
        return Jd1 + C1 - deltat
    }

    private fun INT(d: Double): Int {
        return floor(d).toInt()
    }

    private fun getSunLongitude(dayNumber: Int, timeZone: Double): Double {
        return SunLongitude(dayNumber - 0.5 - timeZone / 24)
    }

    private fun getNewMoonDay(k: Int, timeZone: Double): Int {
        val jd = NewMoon(k)
        return INT(jd + 0.5 + timeZone / 24)
    }

    private fun getLunarMonth11(yy: Int, timeZone: Double): Int {
        val off = jdFromDate(31, 12, yy) - 2415021.076998695
        val k = INT(off / 29.530588853)
        var nm = getNewMoonDay(k, timeZone)
        val sunLong = INT(getSunLongitude(nm, timeZone) / 30)
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, timeZone)
        }
        return nm
    }

    private fun getLeapMonthOffset(a11: Int, timeZone: Double): Int {
        val k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853)
        var last = 0 // Month 11 contains point of sun longutide 3*PI/2 (December
        // solstice)
        var i = 1 // We start with the month following lunar month 11
        var arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30)
        //		int arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) );
        do {
            last = arc
            i++
            arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30)
            //			arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone));
        } while (arc != last && i < 14)
        return i - 1
    }

    /**
     *
     * @param solarDay
     * @param solarMonth
     * @param solarYear
     * @param timeZone
     * @return array of [lunarDay, lunarMonth, lunarYear, leapOrNot]
     */
    fun convertSolar2Lunar(
        solarDay: Int, solarMonth: Int, solarYear: Int,
        timeZone: Double
    ): IntArray {
        val lunarDay: Int
        var lunarMonth: Int
        var lunarYear: Int
        var lunarLeap: Int
        val dayNumber = jdFromDate(solarDay, solarMonth, solarYear)
        val k = INT((dayNumber - 2415021.076998695) / 29.530588853)
        var monthStart = getNewMoonDay(k + 1, timeZone)
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone)
        }
        var a11 = getLunarMonth11(solarYear, timeZone)
        var b11 = a11
        if (a11 >= monthStart) {
            lunarYear = solarYear
            a11 = getLunarMonth11(solarYear - 1, timeZone)
        } else {
            lunarYear = solarYear + 1
            b11 = getLunarMonth11(solarYear + 1, timeZone)
        }
        lunarDay = dayNumber - monthStart + 1
        val diff = INT(((monthStart - a11) / 29).toDouble())
        lunarLeap = 0
        lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapMonthDiff = getLeapMonthOffset(a11, timeZone)
            //            Log.d(TAG,"xxxxxxxxxxxx- leapMonthDeff = "+leapMonthDiff);
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10
                if (diff == leapMonthDiff) {
                    lunarLeap = 1
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1
        }
        return intArrayOf(lunarDay, lunarMonth, lunarYear, lunarLeap)
    }

    fun convertLunar2Solar(
        lunarDay: Int, lunarMonth: Int,
        lunarYear: Int, lunarLeap: Int, timeZone: Double
    ): IntArray? {
        val a11: Int
        val b11: Int
        if (lunarMonth < 11) {
            a11 = getLunarMonth11(lunarYear - 1, timeZone)
            b11 = getLunarMonth11(lunarYear, timeZone)
        } else {
            a11 = getLunarMonth11(lunarYear, timeZone)
            b11 = getLunarMonth11(lunarYear + 1, timeZone)
        }
        val k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853)
        var off = lunarMonth - 11
        if (off < 0) {
            off += 12
        }
        if (b11 - a11 > 365) {
            val leapOff = getLeapMonthOffset(a11, timeZone)
            var leapMonth = leapOff - 2
            //            Log.d(TAG,"xxxxxxxxxxxxxxxxxxxx-leapMonth ="+leapMonth);
            if (leapMonth < 0) {
                leapMonth += 12
            }
            if (lunarLeap != 0 && lunarMonth != leapMonth) {
                Log.d(TAG, "Invalid input!")
                return intArrayOf(0, 0, 0)
            } else if (lunarLeap != 0 || off >= leapOff) {
                off += 1
            }
        }
        val monthStart = getNewMoonDay(k + off, timeZone)
        //        Log.d(TAG,"xxxxxxxxx - monthStart ="+monthStart);
        return jdToDate(monthStart + lunarDay - 1)
    }

    fun getWeek(year: Int, month: Int, day: Int) : String {
        val calendarInstance = java.util.Calendar.getInstance()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("$year-$month-$day 00:00:00")
        calendarInstance.time = date
       return when(calendarInstance.get(7)) {
            1 -> "Chủ nhật"
            2 -> "Thứ hai"
            3 -> "Thứ ba"
            4 -> "Thứ tư"
            5 -> "Thứ năm"
            6 -> "Thứ sáu"
            7 -> "Thứ bảy"
            else -> Strings.EMPTY
        }
    }


}