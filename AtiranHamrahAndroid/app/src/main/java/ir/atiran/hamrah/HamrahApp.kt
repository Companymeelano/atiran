package ir.atiran.hamrah

import android.app.Application
import ir.atiran.hamrah.data.local.AppSettings
import ir.atiran.hamrah.data.local.DemoStore
import ir.atiran.hamrah.data.remote.AtiranApi
import ir.atiran.hamrah.data.repo.HamrahRepository

/**
 * کلاس Application برنامه «آتیران همراه»
 * وابستگی‌ها را به‌صورت دستی (بدون فریم‌ورک تزریق وابستگی) نگه می‌دارد
 * تا پروژه ساده، سبک و قابل‌فهم بماند.
 */
class HamrahApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/**
 * ظرف وابستگی‌ها (ServiceLocator سبک)
 */
class AppContainer(context: android.content.Context) {
    val settings: AppSettings = AppSettings(context)
    val api: AtiranApi = AtiranApi()
    val demo: DemoStore = DemoStore()
    val repository: HamrahRepository = HamrahRepository(settings, api, demo)
}
