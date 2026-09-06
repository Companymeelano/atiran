package ir.atiran.hamrah.core.util

import java.text.DecimalFormat

/** ابزارهای قالب‌بندی فارسی: اعداد، مبلغ، درصدها */

private val persianDigits = charArrayOf(
    '۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'
)

/** تبدیل ارقام لاتین یک رشته به ارقام فارسی */
fun String.toPersianDigits(): String {
    val sb = StringBuilder(length)
    for (c in this) {
        sb.append(if (c in '0'..'9') persianDigits[c - '0'] else c)
    }
    return sb.toString()
}

/** تبدیل ارقام فارسی/عربی یک رشته به ارقام لاتین (برای شماره‌گیری و پردازش) */
fun String.toEnglishDigits(): String {
    val sb = StringBuilder(length)
    for (c in this) {
        sb.append(
            when {
                c in '۰'..'۹' -> ('0' + (c - '۰'))
                c in '٠'..'٩' -> ('0' + (c - '٠'))
                else -> c
            }
        )
    }
    return sb.toString()
}

/** تبدیل عدد صحیح به رشته با ارقام فارسی */
fun Int.toPersianDigits(): String = toString().toPersianDigits()

/** تبدیل عدد اعشاری به رشته با ارقام فارسی */
fun Double.toPersianDigits(): String = toString().toPersianDigits()

/** جداکننده هزارگان + ارقام فارسی؛ مثال: ۱۲٬۴۵۰٬۰۰۰ */
fun Double.grouped(): String {
    val df = DecimalFormat("#,###")
    val s = df.format(this.toLong())
    return s.replace(",", "٬").toPersianDigits()
}

fun Int.grouped(): String = toDouble().grouped()

/** مبلغ با واحد؛ مثال: ۱۲٬۴۵۰٬۰۰۰ تومان */
fun Double.money(unit: String = "تومان"): String = "${grouped()} $unit"

/** نمایش جمع فارسی؛ مثال: «۱۲ کالا» */
fun Int.countPersian(noun: String): String = "${toPersianDigits()} $noun"

/** نمایش نرخ به‌صورت درصد فارسی */
fun Double.percent(): String = "${DecimalFormat("##.#").format(this)}٪".toPersianDigits()

/** علامت جهت رشد/افتاه + مقدار رنگی */
fun Double.trend(): String =
    (if (this >= 0) "▲ +" else "▼ ").let { "$it${DecimalFormat("##.#").format(kotlin.math.abs(this))}٪".toPersianDigits() }

/** دو حرف اول نام برای آواتار */
fun String.initials(): String {
    val parts = trim().split(" ")
    return if (parts.size >= 2) (parts[0].take(1) + parts[1].take(1))
    else take(1)
}
