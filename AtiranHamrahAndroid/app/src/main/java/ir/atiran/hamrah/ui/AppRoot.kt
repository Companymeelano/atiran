package ir.atiran.hamrah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ir.atiran.hamrah.HamrahApp
import ir.atiran.hamrah.ui.components.BottomDock
import ir.atiran.hamrah.ui.screens.about.AboutScreen
import ir.atiran.hamrah.ui.screens.checks.ChecksScreen
import ir.atiran.hamrah.ui.screens.customers.CustomerDetailScreen
import ir.atiran.hamrah.ui.screens.customers.CustomersScreen
import ir.atiran.hamrah.ui.screens.home.HomeScreen
import ir.atiran.hamrah.ui.screens.invoices.InvoiceDetailScreen
import ir.atiran.hamrah.ui.screens.invoices.InvoicesScreen
import ir.atiran.hamrah.ui.screens.invoices.NewInvoiceScreen
import ir.atiran.hamrah.ui.screens.login.LoginScreen
import ir.atiran.hamrah.ui.screens.messages.MessagesScreen
import ir.atiran.hamrah.ui.screens.more.MoreScreen
import ir.atiran.hamrah.ui.screens.prepayments.PrepaymentsScreen
import ir.atiran.hamrah.ui.screens.products.ProductsScreen
import ir.atiran.hamrah.ui.screens.reports.ReportsScreen
import ir.atiran.hamrah.ui.screens.settings.SettingsScreen
import ir.atiran.hamrah.ui.screens.splash.SplashScreen
import ir.atiran.hamrah.ui.screens.visits.VisitsScreen
import ir.atiran.hamrah.ui.theme.Space900

/** مسیرهایی که داک پایین در آن‌ها دیده می‌شود */
private val dockRoutes = setOf("home", "customers", "invoices", "products", "more")

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val app = context.applicationContext as HamrahApp
    val vm: MainViewModel = viewModel { MainViewModel(app.container) }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    // نمایش پیام‌های موقت
    LaunchedEffect(Unit) {
        vm.snackbar.collect { message ->
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                vm.consumeSnackbar()
            }
        }
    }

    // هدایت خودکار: پس از ورود به خانه، پس از خروج به صفحه ورود
    val prefsNow by vm.prefs.collectAsState()
    LaunchedEffect(prefsNow.loggedIn) {
        val route = navController.currentDestination?.route
        if (prefsNow.loggedIn && route == "login") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else if (!prefsNow.loggedIn && route != null && route != "splash" && route != "login") {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    val open: (String) -> Unit = { route ->
        navController.navigate(route) { launchSingleTop = true }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Space900)
    ) {
        androidx.compose.material3.Scaffold(
            containerColor = Space900,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (currentRoute in dockRoutes) {
                    BottomDock(
                        currentRoute = currentRoute,
                        onSelect = { route ->
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onFab = { open("new_invoice") }
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                composable("splash") {
                    SplashScreen(
                        booting = vm.booting.collectAsStateValue(),
                        loggedIn = vm.prefs.collectAsStateValue().loggedIn,
                        onDone = { loggedIn ->
                            navController.navigate(if (loggedIn) "home" else "login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }
                composable("login") {
                    LoginScreen(vm = vm)
                }
                composable("home") {
                    HomeScreen(vm = vm, onOpen = open)
                }
                composable("more") {
                    MoreScreen(vm = vm, onOpen = open)
                }
                composable("customers") {
                    CustomersScreen(vm = vm, onOpenCustomer = { open("customer/$it") })
                }
                composable(
                    route = "customer/{shmo}",
                    arguments = listOf(navArgument("shmo") { type = NavType.IntType })
                ) { entry ->
                    val shmo = entry.arguments?.getInt("shmo") ?: 0
                    CustomerDetailScreen(
                        vm = vm,
                        shmo = shmo,
                        onBack = { navController.popBackStack() },
                        onOpen = open
                    )
                }
                composable("invoices") {
                    InvoicesScreen(
                        vm = vm,
                        onOpenInvoice = { open("invoice/$it") },
                        onNewInvoice = { open("new_invoice") }
                    )
                }
                composable(
                    route = "invoice/{shfacfo}",
                    arguments = listOf(navArgument("shfacfo") { type = NavType.LongType })
                ) { entry ->
                    val shfacfo = entry.arguments?.getLong("shfacfo") ?: 0L
                    InvoiceDetailScreen(
                        vm = vm,
                        shfacfo = shfacfo,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "new_invoice?customer={shmo}",
                    arguments = listOf(navArgument("shmo") { type = NavType.IntType; defaultValue = -1 })
                ) { entry ->
                    val shmo = entry.arguments?.getInt("shmo") ?: -1
                    NewInvoiceScreen(
                        vm = vm,
                        presetShmo = shmo,
                        onClose = { navController.popBackStack() },
                        onOpenInvoice = { open("invoice/$it") }
                    )
                }
                composable("products") {
                    ProductsScreen(vm = vm)
                }
                composable("checks") {
                    ChecksScreen(vm = vm)
                }
                composable("visits") {
                    VisitsScreen(vm = vm, onOpenCustomer = { open("customer/$it") })
                }
                composable("messages") {
                    MessagesScreen(vm = vm)
                }
                composable("prepayments") {
                    PrepaymentsScreen(vm = vm)
                }
                composable("reports") {
                    ReportsScreen(vm = vm)
                }
                composable("settings") {
                    SettingsScreen(vm = vm, onOpenAbout = { open("about") })
                }
                composable("about") {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

/** کمکی برای خواندن StateFlow داخل Composable */
@Composable
private fun <T> kotlinx.coroutines.flow.StateFlow<T>.collectAsStateValue(): T =
    this.collectAsState().value
