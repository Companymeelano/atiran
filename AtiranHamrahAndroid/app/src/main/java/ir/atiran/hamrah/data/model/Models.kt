package ir.atiran.hamrah.data.model

/**
 * مدل‌های دامنه برنامه «آتیران همراه»
 * این مدل‌ها با ساختار جداول اصلی سامانه آتیران
 * (CUSTOMER، visitor، sailfact، inventory، getchk و ...) هم‌راستا هستند.
 */

/** ویزیتور / فروشنده سیار */
data class Visitor(
    val visRdf: Int,
    val name: String,
    val cell: String? = null,
    val regionName: String? = null,
    val man: Double = 0.0,               // مانده حساب ویزیتور
    val eteb: Double = 0.0,              // اعتبار
    val tedadFactorMojaz: Int = 0,       // تعداد فاکتور مجاز مانده
    val mablaghMojaz: Double = 0.0,      // مبلغ مجاز مانده برای فاکتور
    val isSupervisor: Boolean = false
)

/** مشتری (معادل جدول CUSTOMER) */
data class Customer(
    val shmo: Int,
    val name: String,
    val code: String? = null,
    val groupName: String? = null,
    val address: String? = null,
    val tel: String? = null,
    val cell: String? = null,
    val cred: Double = 0.0,              // اعتبار (Etebar)
    val man: Double = 0.0,               // مانده بدهی
    val blackList: Boolean = false,
    val justNaghdi: Boolean = false,     // فقط نقدی
    val visitDay: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val active: Boolean = true
)

/** گروه کالا */
data class KalaGroup(
    val id: Int,
    val name: String
)

/** کالا (معادل جدول inventory) */
data class Kala(
    val shka: Long,
    val name: String,
    val code: String,
    val groupName: String,
    val unit: String,                    // واحد شمارش اصلی (vahsanj)
    val mojooziPerVah: Int = 0,          // تعداد جزء در واحد
    val price: Double,                   // قیمت فروش (forosh1)
    val minPrice: Double = 0.0,          // حداقل قیمت مجاز
    val stockVah: Double = 0.0,          // موجودی انبار (mohvah)
    val reorderPoint: Int = 0,           // نقطه سفارش (reopoint)
    val anbarName: String? = null,
    val active: Boolean = true,
    val gift: Boolean = false
)

/** وضعیت فاکتور */
enum class InvoiceStatus(val label: String) {
    DRAFT("پیش‌نویس"),
    PENDING("در انتظار تأیید"),
    CONFIRMED("تأییدشده"),
    REJECTED("ردشده")
}

/** ردیف فاکتور (معادل subsailfact) */
data class InvoiceLine(
    val shka: Long,
    val name: String,
    val qty: Double,                     // TEDVAH
    val unitPrice: Double,               // VAHPRICE
    val lineSum: Double,                 // LINESUM
    val tafifLine: Double = 0.0,         // تخفیف ردیف
    val gift: Boolean = false
)

/** فاکتور / پیش‌فاکتور (معادل sailfact / sailfact_pish) */
data class Invoice(
    val shfacfo: Long,
    val date: String,                    // تاریخ شمسی
    val time: String? = null,
    val customerShmo: Int,
    val customerName: String,
    val sumAll: Double,                  // جمع کل اقلام
    val tafif: Double = 0.0,             // تخفیف
    val tax: Double = 0.0,               // مالیات
    val avarez: Double = 0.0,            // عوارض
    val total: Double,                   // مبلغ نهایی (all)
    val paid: Double = 0.0,              // مبلغ دریافتی (MabDaryaftFactor)
    val status: InvoiceStatus = InvoiceStatus.CONFIRMED,
    val isPre: Boolean = false,          // پیش‌فاکتور است؟
    val lines: List<InvoiceLine> = emptyList()
) {
    val remaining: Double get() = total - paid
    val isSettled: Boolean get() = remaining <= 0.0
}

/** وضعیت چک */
enum class CheckStatus(val label: String) {
    IN_POCKET("در جیب"),
    DEPOSITED("به بانک سپردهشده"),
    CASHED("وصول‌شده"),
    BOUNCED("برگشتی"),
    RETURNED("عودت‌شده")
}

/** چک دریافتی (معادل getchk) */
data class CheckItem(
    val rdf: Long,
    val customerShmo: Int,
    val customerName: String,
    val bankName: String,
    val shobe: String? = null,
    val checkNo: String,
    val amount: Double,
    val dueDate: String,                 // تاریخ سررسید شمسی
    val status: CheckStatus = CheckStatus.IN_POCKET
)

/** وضعیت توقف در مسیر */
enum class VisitStatus(val label: String) {
    PENDING("در انتظار"),
    DONE("انجام‌شده"),
    SKIPPED("حذف از مسیر")
}

/** یک توقف در مسیر روزانه ویزیتور */
data class VisitStop(
    val visitId: Long,
    val shmo: Int,
    val customerName: String,
    val address: String? = null,
    val order: Int,
    val window: String,                  // بازه زمانی پیشنهادی
    val status: VisitStatus = VisitStatus.PENDING,
    val note: String? = null
)

/** اولویت پیام */
enum class MessagePriority(val label: String) {
    NORMAL("عادی"),
    HIGH("مهم"),
    URGENT("فوری")
}

/** پیام سامانه (معادل VisitorMessages) */
data class Message(
    val id: Long,
    val title: String,
    val body: String,
    val date: String,
    val time: String? = null,
    val sender: String? = null,
    val priority: MessagePriority = MessagePriority.NORMAL,
    val read: Boolean = false
)

/** نوع پیش‌دریافت */
enum class PrepayType(val label: String) {
    CASH("نقدی"),
    POS("کارت‌خوان"),
    CHECK("چک")
}

/** پیش‌دریافت از مشتری (معادل PishDaryaft) */
data class Prepayment(
    val id: Long,
    val customerShmo: Int,
    val customerName: String,
    val amount: Double,
    val type: PrepayType = PrepayType.CASH,
    val date: String,
    val note: String? = null
)

/** اطلاعات شرکت */
data class CompanyInfo(
    val name: String,
    val modir: String? = null,
    val tell: String? = null,
    val address: String? = null
)

/** آمار روز ویزیتور */
data class TodayStats(
    val salesToday: Double = 0.0,
    val target: Double = 1.0,
    val invoiceCount: Int = 0,
    val visitsDone: Int = 0,
    val visitsTotal: Int = 0,
    val newCustomers: Int = 0,
    val collectionToday: Double = 0.0   // وصولی امروز
) {
    val avgBasket: Double get() = if (invoiceCount > 0) salesToday / invoiceCount else 0.0
    val progress: Float get() = if (target > 0) (salesToday / target).toFloat().coerceIn(0f, 1f) else 0f
    val visitProgress: Float get() = if (visitsTotal > 0) visitsDone.toFloat() / visitsTotal else 0f
}

/** فروش هفتگی برای نمودار گزارش‌ها */
data class DaySales(
    val dayName: String,
    val value: Double
)

/** عملیات در صف همگام‌سازی با سرور */
data class PendingOp(
    val id: Long,
    val kind: String,          // INVOICE / VISIT / PREPAY / CUSTOMER
    val title: String,
    val createdAt: String,
    val synced: Boolean = false
)

/** ردیف سبد سفارش در ثبت فاکتور جدید */
data class CartLine(
    val kala: Kala,
    val qty: Double
) {
    val lineSum: Double get() = qty * kala.price
}
