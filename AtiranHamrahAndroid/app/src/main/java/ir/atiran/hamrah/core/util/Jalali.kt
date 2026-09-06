package ir.atiran.hamrah.core.util

import java.util.Calendar

/**
 * تبدیل تاریخ میلادی به شمسی (جلالی) و ابزارهای تاریخ فارسی.
 * الگوریتم کلاسیک و تست‌شده تبدیل تقویم جلالی.
 */
object Jalali {

    val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val weekDays = listOf(
        "یکشنبه", "دوشنبه", "سه‌شنبه",
        "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"
    )

    /** تبدیل میلادی به جلالی؛ خروجی: (سال، ماه، روز) */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
                ((gy2 + 399) / 400) + gd + gDm[gm - 1]
        var jy = -1595 + 33 * (days / 12053)
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return Triple(jy, jm, jd)
    }

    /** تاریخ شمسی امروز به‌صورت (سال، ماه، روز) */
    fun today(): Triple<Int, Int, Int> {
        val cal = Calendar.getInstance()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /** نام روز هفته امروز */
    fun todayWeekDay(): String {
        val cal = Calendar.getInstance()
        return weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1]
    }

    /** رشته کامل تاریخ امروز، مثال: «شنبه ۱۵ شهریور ۱۴۰۵» */
    fun todayFull(): String {
        val (y, m, d) = today()
        return "${todayWeekDay()} ${d.toPersianDigits()} ${monthNames[m - 1]} ${y.toPersianDigits()}"
    }

    /** رشته کوتاه تاریخ، مثال: ۱۴۰۵/۰۶/۱۵ */
    fun todayShort(): String {
        val (y, m, d) = today()
        return format(y, m, d)
    }

    fun format(y: Int, m: Int, d: Int): String =
        "${y.toPersianDigits()}/${m.toString().padStart(2, '0').toPersianDigits()}/${d.toString().padStart(2, '0').toPersianDigits()}"

    /** امروز به‌صورت عددی ترتیبی از مبنا (برای مقایسه) */
    fun todayStamp(): Int {
        val (y, m, d) = today()
        return y * 10000 + m * 100 + d
    }
}
