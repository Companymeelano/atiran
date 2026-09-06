package ir.atiran.hamrah.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.atiran.hamrah.AppContainer
import ir.atiran.hamrah.data.local.Prefs
import ir.atiran.hamrah.data.model.CartLine
import ir.atiran.hamrah.data.model.CheckItem
import ir.atiran.hamrah.data.model.CheckStatus
import ir.atiran.hamrah.data.model.CompanyInfo
import ir.atiran.hamrah.data.model.Customer
import ir.atiran.hamrah.data.model.DaySales
import ir.atiran.hamrah.data.model.Invoice
import ir.atiran.hamrah.data.model.Kala
import ir.atiran.hamrah.data.model.KalaGroup
import ir.atiran.hamrah.data.model.Message
import ir.atiran.hamrah.data.model.PrepayType
import ir.atiran.hamrah.data.model.TodayStats
import ir.atiran.hamrah.data.model.Visitor
import ir.atiran.hamrah.data.model.VisitStop
import ir.atiran.hamrah.data.repo.HamrahRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * مدل-ویو مرکزی برنامه؛ وضعیت همه بخش‌ها را نگه می‌دارد.
 */
class MainViewModel(private val container: AppContainer) : ViewModel() {

    private val repo: HamrahRepository = container.repository

    /* ---------- وضعیت‌ها ---------- */

    private val _booting = MutableStateFlow(true)
    val booting: StateFlow<Boolean> = _booting

    private val _visitor = MutableStateFlow<Visitor?>(null)
    val visitor: StateFlow<Visitor?> = _visitor

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers

    private val _kalas = MutableStateFlow<List<Kala>>(emptyList())
    val kalas: StateFlow<List<Kala>> = _kalas

    private val _kalaGroups = MutableStateFlow<List<KalaGroup>>(emptyList())
    val kalaGroups: StateFlow<List<KalaGroup>> = _kalaGroups

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    private val _checks = MutableStateFlow<List<CheckItem>>(emptyList())
    val checks: StateFlow<List<CheckItem>> = _checks

    private val _visits = MutableStateFlow<List<VisitStop>>(emptyList())
    val visits: StateFlow<List<VisitStop>> = _visits

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _prepayments = MutableStateFlow<List<ir.atiran.hamrah.data.model.Prepayment>>(emptyList())
    val prepayments: StateFlow<List<ir.atiran.hamrah.data.model.Prepayment>> = _prepayments

    private val _company = MutableStateFlow<CompanyInfo?>(null)
    val company: StateFlow<CompanyInfo?> = _company

    private val _stats = MutableStateFlow(TodayStats())
    val stats: StateFlow<TodayStats> = _stats

    private val _weekly = MutableStateFlow<List<DaySales>>(emptyList())
    val weekly: StateFlow<List<DaySales>> = _weekly

    private val _prefs = MutableStateFlow(Prefs())
    val prefs: StateFlow<Prefs> = _prefs

    private val _pending = MutableStateFlow<List<HamrahRepository.PendingItem>>(emptyList())
    val pending: StateFlow<List<HamrahRepository.PendingItem>> = _pending

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar

    /* ---------- سبد فاکتور جدید ---------- */

    private val _cart = MutableStateFlow<List<CartLine>>(emptyList())
    val cart: StateFlow<List<CartLine>> = _cart

    private val _cartCustomer = MutableStateFlow<Customer?>(null)
    val cartCustomer: StateFlow<Customer?> = _cartCustomer

    val cartSum: Double get() = _cart.value.sumOf { it.lineSum }

    /* ---------- راه‌اندازی ---------- */

    init {
        viewModelScope.launch {
            val p = container.settings.current()
            _prefs.value = p
            container.api.configure(p.serverUrl)
            if (p.loggedIn) {
                _visitor.value = container.demo.visitor
                loadAll()
            }
            delay(400) // مکث کوتاه برای نمایش اسپلش
            _booting.value = false
        }
        viewModelScope.launch {
            container.settings.flow.collect { _prefs.value = it }
        }
        viewModelScope.launch {
            repo.pending.collect { _pending.value = it }
        }
    }

    private fun loadAll() {
        viewModelScope.launch {
            _customers.value = repo.customers()
            _kalas.value = repo.kalas()
            _kalaGroups.value = repo.kalaGroups()
            _invoices.value = repo.invoices()
            _checks.value = repo.checks()
            _visits.value = repo.visits()
            _messages.value = repo.messages()
            _prepayments.value = repo.prepayments()
            _company.value = repo.company()
            _stats.value = repo.todayStats()
            _weekly.value = repo.weeklySales()
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }

    private fun toast(message: String) {
        _snackbar.value = message
    }

    /* ---------- ورود / خروج ---------- */

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val v = repo.login(username.trim(), password)
                _visitor.value = v
                container.settings.setLoggedIn(true, v.name, v.visRdf)
                loadAll()
                onResult(true)
            } catch (e: Exception) {
                toast("ورود ناموفق بود: ${e.message}")
                onResult(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            container.settings.setLoggedIn(false)
            _visitor.value = null
        }
    }

    /* ---------- سبد فاکتور ---------- */

    fun setCartCustomer(customer: Customer?) {
        _cartCustomer.value = customer
    }

    fun addToCart(kala: Kala, qty: Double = 1.0) {
        val current = _cart.value
        val index = current.indexOfFirst { it.kala.shka == kala.shka }
        _cart.value = if (index >= 0) {
            current.mapIndexed { i, line -> if (i == index) line.copy(qty = line.qty + qty) else line }
        } else {
            current + CartLine(kala, qty)
        }
    }

    fun removeFromCart(kala: Kala) {
        _cart.value = _cart.value.filterNot { it.kala.shka == kala.shka }
    }

    fun setCartQty(kala: Kala, qty: Double) {
        if (qty <= 0.0) {
            removeFromCart(kala)
            return
        }
        _cart.value = _cart.value.map { if (it.kala.shka == kala.shka) it.copy(qty = qty) else it }
    }

    fun clearCart() {
        _cart.value = emptyList()
        _cartCustomer.value = null
    }

    fun submitCart(discount: Double, isPre: Boolean, onDone: (Long) -> Unit) {
        val customer = _cartCustomer.value ?: return
        val lines = _cart.value
        if (lines.isEmpty()) return
        val shfacfo = repo.submitInvoice(customer, lines, discount, isPre = isPre)
        clearCart()
        viewModelScope.launch {
            _invoices.value = repo.invoices()
            _stats.value = repo.todayStats()
        }
        toast(if (isPre) "پیش‌فاکتور $shfacfo با موفقیت ثبت شد" else "فاکتور $shfacfo با موفقیت ثبت شد")
        onDone(shfacfo)
    }

    /* ---------- عملیات‌ها ---------- */

    fun checkIn(visitId: Long) {
        repo.checkIn(visitId)
        viewModelScope.launch {
            _visits.value = repo.visits()
            _stats.value = repo.todayStats()
        }
        toast("ویزیت با موفقیت ثبت شد ✓")
    }

    fun skipVisit(visitId: Long) {
        repo.skipVisit(visitId)
        viewModelScope.launch { _visits.value = repo.visits() }
    }

    fun markMessageRead(id: Long) {
        repo.markMessageRead(id)
        viewModelScope.launch { _messages.value = repo.messages() }
    }

    fun addPrepayment(customer: Customer, amount: Double, type: PrepayType, note: String?) {
        repo.addPrepayment(customer, amount, type, note)
        viewModelScope.launch { _prepayments.value = repo.prepayments() }
        toast("پیش‌دریافت ثبت شد و به صف همگام‌سازی اضافه شد ✓")
    }

    fun addCustomer(name: String, groupName: String, cell: String?, address: String?) {
        repo.addCustomer(name, groupName, cell, address)
        viewModelScope.launch { _customers.value = repo.customers() }
        toast("مشتری جدید ثبت شد ✓")
    }

    fun setCheckStatus(rdf: Long, status: CheckStatus) {
        repo.setCheckStatus(rdf, status)
        viewModelScope.launch { _checks.value = repo.checks() }
    }

    fun unreadCount(): Int = _messages.value.count { !it.read }

    fun dueSoonChecks(): List<CheckItem> = _checks.value.filter {
        it.status == CheckStatus.IN_POCKET || it.status == CheckStatus.DEPOSITED
    }

    fun flushQueue() {
        viewModelScope.launch {
            val n = repo.flushQueue()
            _pending.value = repo.pending.value
            toast(if (n > 0) "$n عملیات با سرور همگام شد ✓" else "صف همگام‌سازی خالی است")
        }
    }

    /* ---------- تنظیمات ---------- */

    fun setServerUrl(url: String) {
        viewModelScope.launch {
            container.settings.setServerUrl(url)
            container.api.configure(url)
            val reachable = container.api.ping()
            toast(
                if (url.isBlank()) "نشانی سرور پاک شد؛ حالت نمایشی فعال است"
                else if (reachable) "اتصال به سرور برقرار شد ✓"
                else "سرور در دسترس نیست؛ داده نمایشی ادامه می‌دهد"
            )
        }
    }

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch { container.settings.setDarkTheme(value) }
    }

    fun setDemoMode(value: Boolean) {
        viewModelScope.launch { container.settings.setDemoMode(value) }
    }
}
