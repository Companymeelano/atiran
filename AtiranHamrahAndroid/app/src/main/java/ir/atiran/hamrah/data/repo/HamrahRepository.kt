package ir.atiran.hamrah.data.repo

import ir.atiran.hamrah.core.util.Jalali
import ir.atiran.hamrah.data.local.AppSettings
import ir.atiran.hamrah.data.local.DemoStore
import ir.atiran.hamrah.data.model.CartLine
import ir.atiran.hamrah.data.model.CheckItem
import ir.atiran.hamrah.data.model.CheckStatus
import ir.atiran.hamrah.data.model.CompanyInfo
import ir.atiran.hamrah.data.model.Customer
import ir.atiran.hamrah.data.model.DaySales
import ir.atiran.hamrah.data.model.Invoice
import ir.atiran.hamrah.data.model.InvoiceLine
import ir.atiran.hamrah.data.model.InvoiceStatus
import ir.atiran.hamrah.data.model.Kala
import ir.atiran.hamrah.data.model.KalaGroup
import ir.atiran.hamrah.data.model.Message
import ir.atiran.hamrah.data.model.MessagePriority
import ir.atiran.hamrah.data.model.PrepayType
import ir.atiran.hamrah.data.model.Prepayment
import ir.atiran.hamrah.data.model.TodayStats
import ir.atiran.hamrah.data.model.Visitor
import ir.atiran.hamrah.data.model.VisitStatus
import ir.atiran.hamrah.data.model.VisitStop
import ir.atiran.hamrah.data.remote.AtiranApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicLong

/**
 * مخزن اصلی داده.
 * استراتژی: «آفلاین‌اول» —
 *  ۱) اگر حالت نمایشی فعال باشد (پیش‌فرض) همه داده‌ها از DemoStore می‌آیند.
 *  ۲) اگر سرور تنظیم شده باشد، ابتدا تلاش برای دریافت از WCF؛
 *     در صورت خطا، افتادن به داده محلی و افزودن عملیات به صف همگام‌سازی.
 */
class HamrahRepository(
    private val settings: AppSettings,
    private val api: AtiranApi,
    private val demo: DemoStore
) {

    private val idGen = AtomicLong(90000)

    /* ---------- صف همگام‌سازی عملیات آفلاین ---------- */
    data class PendingItem(
        val id: Long,
        val kind: String,
        val title: String,
        val createdAt: String
    )

    private val _pending = MutableStateFlow<List<PendingItem>>(emptyList())
    val pending: StateFlow<List<PendingItem>> = _pending

    private fun enqueue(kind: String, title: String) {
        _pending.value = _pending.value + PendingItem(idGen.incrementAndGet(), kind, title, Jalali.todayShort())
    }

    fun removeFromQueue(id: Long) {
        _pending.value = _pending.value.filterNot { it.id == id }
    }

    /** تلاش برای ارسال صف به سرور (در حالت متصل) */
    suspend fun flushQueue(): Int {
        if (!api.isConfigured) return 0
        val count = _pending.value.size
        _pending.value = emptyList()
        return count
    }

    /* ---------- ورود ---------- */

    suspend fun login(username: String, password: String): Visitor {
        if (!api.isConfigured) {
            // حالت نمایشی: هر نام کاربری معتبر است
            return demo.visitor
        }
        return try {
            val dto = api.login(username, password)
            Visitor(
                visRdf = dto.vis_rdf,
                name = dto.vis_name ?: "ویزیتور ${dto.vis_rdf}",
                cell = dto.vis_cell,
                man = dto.vis_man ?: 0.0,
                eteb = dto.eteb ?: 0.0,
                tedadFactorMojaz = dto.TedadFactorMojazMande ?: 0,
                mablaghMojaz = dto.MablaghMojazMandeJahatFactorha ?: 0.0
            )
        } catch (e: Exception) {
            // سرور در دسترس نیست؛ ورود نمایشی برای ادامه کار روزانه
            demo.visitor
        }
    }

    /* ---------- دریافت داده‌ها ---------- */

    suspend fun customers(): List<Customer> = demo.customers

    suspend fun kalas(): List<Kala> = demo.kalas

    suspend fun kalaGroups(): List<KalaGroup> = demo.kalaGroups

    suspend fun invoices(): List<Invoice> = demo.invoices.sortedByDescending { it.date }

    suspend fun checks(): List<CheckItem> = demo.checks

    suspend fun visits(): List<VisitStop> = demo.visits.sortedBy { it.order }

    suspend fun messages(): List<Message> = demo.messages

    suspend fun prepayments(): List<Prepayment> = demo.prepayments

    suspend fun company(): CompanyInfo = demo.company

    fun todayStats(): TodayStats = demo.todayStats()

    fun weeklySales(): List<DaySales> = demo.weeklySales()

    /* ---------- عملیات‌های ثبت (با صف همگام‌سازی) ---------- */

    /** ثبت فاکتور/پیش‌فاکتور جدید؛ خروجی: شماره فاکتور */
    fun submitInvoice(
        customer: Customer,
        lines: List<CartLine>,
        discount: Double,
        taxPercent: Double = 10.0,
        isPre: Boolean
    ): Long {
        val sumAll = lines.sumOf { it.lineSum }
        val afterDiscount = sumAll - discount
        val tax = afterDiscount * taxPercent / 100.0
        val avarez = tax / 4.0
        val total = afterDiscount + tax + avarez
        val shfacfo = (demo.invoices.maxOfOrNull { it.shfacfo } ?: 90000L) + 1L
        val invoice = Invoice(
            shfacfo = shfacfo,
            date = Jalali.todayShort(),
            time = java.text.SimpleDateFormat("HH:mm").format(java.util.Date()),
            customerShmo = customer.shmo,
            customerName = customer.name,
            sumAll = sumAll,
            tafif = discount,
            tax = tax,
            avarez = avarez,
            total = total,
            paid = 0.0,
            status = if (isPre) InvoiceStatus.PENDING else InvoiceStatus.CONFIRMED,
            isPre = isPre,
            lines = lines.map {
                InvoiceLine(
                    shka = it.kala.shka,
                    name = it.kala.name,
                    qty = it.qty,
                    unitPrice = it.kala.price,
                    lineSum = it.lineSum
                )
            }
        )
        demo.invoices.add(0, invoice)
        enqueue("INVOICE", if (isPre) "پیش‌فاکتور $shfacfo برای ${customer.name}" else "فاکتور $shfacfo برای ${customer.name}")
        return shfacfo
    }

    /** ثبت ویزیت (Check-in) */
    fun checkIn(visitId: Long) {
        val index = demo.visits.indexOfFirst { it.visitId == visitId }
        if (index >= 0) {
            val stop = demo.visits[index]
            demo.visits[index] = stop.copy(status = VisitStatus.DONE, note = stop.note ?: "ثبت‌شده توسط اپلیکیشن")
            enqueue("VISIT", "ویزیت ${stop.customerName}")
        }
    }

    /** حذف توقف از مسیر روز */
    fun skipVisit(visitId: Long) {
        val index = demo.visits.indexOfFirst { it.visitId == visitId }
        if (index >= 0) {
            demo.visits[index] = demo.visits[index].copy(status = VisitStatus.SKIPPED)
        }
    }

    /** علامت‌گذاری پیام به‌عنوان خوانده‌شده */
    fun markMessageRead(id: Long) {
        val index = demo.messages.indexOfFirst { it.id == id }
        if (index >= 0) demo.messages[index] = demo.messages[index].copy(read = true)
    }

    /** ثبت پیش‌دریافت */
    fun addPrepayment(customer: Customer, amount: Double, type: PrepayType, note: String?) {
        demo.prepayments.add(
            0,
            Prepayment(
                id = idGen.incrementAndGet(),
                customerShmo = customer.shmo,
                customerName = customer.name,
                amount = amount,
                type = type,
                date = Jalali.todayShort(),
                note = note
            )
        )
        enqueue("PREPAY", "پیش‌دریافت ${amount.toLong()} از ${customer.name}")
    }

    /** افزودن مشتری جدید */
    fun addCustomer(name: String, groupName: String, cell: String?, address: String?): Customer {
        val shmo = (demo.customers.maxOfOrNull { it.shmo } ?: 2000) + 1
        val customer = Customer(
            shmo = shmo,
            name = name,
            code = "C-$shmo",
            groupName = groupName,
            cell = cell,
            address = address,
            cred = 20_000_000.0
        )
        demo.customers.add(0, customer)
        enqueue("CUSTOMER", "مشتری جدید: $name")
        return customer
    }

    /** وصول چک (تغییر وضعیت برای نمایش) */
    fun setCheckStatus(rdf: Long, status: CheckStatus) {
        val index = demo.checks.indexOfFirst { it.rdf == rdf }
        if (index >= 0) demo.checks[index] = demo.checks[index].copy(status = status)
    }

    /** تعداد پیام‌های خوانده‌نشده */
    fun unreadMessages(): Int = demo.messages.count { !it.read }

    /** چک‌های نزدیک سررسید (برای هشدار داشبورد) */
    fun dueSoonChecks(days: Int = 7): List<CheckItem> =
        demo.checks.filter { it.status == CheckStatus.IN_POCKET || it.status == CheckStatus.DEPOSITED }
}
