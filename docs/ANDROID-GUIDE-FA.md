# 📱 راهنمای اپ اندروید «آتیران همراه»

## معرفی

`AtiranHamrahAndroid/` یک اپ اندروید **بومی و کامل** (Kotlin + Jetpack Compose + Material 3) است که
نسخه موبایل سامانه فروش سیار شماست و با همان سرویس WCF موجود (`AtiranLocalServices`) کار می‌کند.

## پیش‌نیازها

| ابزار | نسخه |
|---|---|
| Android Studio | Ladybug (2024.1.1) یا جدیدتر |
| JDK | ۱۷ (همراه Android Studio نصب می‌شود) |
| Gradle | ۸.۹ (wrapper همراه پروژه — دانلود خودکار) |
| حداقل اندروید دستگاه | ۷.۰ (API 24) — پوشش ~۹۷٪ دستگاه‌های فعال |

## ساخت APK (سه گام)

```bash
cd AtiranHamrahAndroid

# ۱) دیباگ — برای تست سریع
./gradlew assembleDebug
# خروجی: app/build/outputs/apk/debug/app-debug.apk

# ۲) ریلیز امضاشده
./gradlew assembleRelease
# خروجی: app/build/outputs/apk/release/app-release-unsigned.apk

# ۳) ساخت بسته‌ی فروشگاهی
./gradlew bundleRelease
# خروجی: app/build/outputs/bundle/release/app-release.aab
```

در ویندوز از `gradlew.bat` استفاده کنید. در Android Studio هم کافی است پروژه را **Open** کنید و
دکمه ▶️ را بزنید.

### امضای نسخه ریلیز

فایل `keystore.properties` بسازید (کنار `app/`) و در `app/build.gradle.kts` بخش signingConfigs را
وفق دهید؛ یا از **Generate Signed App Bundle/APK** در منوی Build استفاده کنید.

## اجرای اولین بار

اپ به‌صورت پیش‌فرض در **حالت نمایشی (Demo)** اجرا می‌شود:
- داده‌های نمونه واقع‌گرایانه (۱۴ مشتری، ۱۸ کالا، ۱۰ فاکتور، ۸ چک، مسیر روز، پیام‌ها)
- همه عملیات (ثبت فاکتور، ویزیت، پیش‌دریافت، مشتری جدید) کار می‌کنند و به «صف همگام‌سازی» می‌روند
- ورود نمایشی: هر نام کاربری/رمزی (یا دکمه «ورود نمایشی»)

## اتصال به سرور واقعی

1. وارد **تنظیمات** اپ شوید
2. نشانی سرویس را وارد کنید، مثال:
   `http://192.168.1.10/AtiranLocalServices/LocalServices.svc`
3. «ذخیره و بررسی اتصال» را بزنید — وضعیت اتصال نمایش داده می‌شود

از این پس ابتدا تلاش برای دریافت از WCF انجام می‌شود و در صورت قطعی، داده محلی ادامه می‌دهد
(الگوی آفلاین‌اول) و عملیات‌ها هنگام اتصال بعدی ارسال می‌شوند (بخش گزارش‌ها ← صف همگام‌سازی).

> اپ روی شبکه محلی اجازه اتصال HTTP دارد (`network_security_config.xml`)؛ برای اینترنت عمومی حتماً HTTPS فعال کنید.

## هم‌سختی فیلدهای JSON با سرور

لایه شبکه در `data/remote/AtiranApi.kt` متمرکز است. DTO ها با همان نام پراپرتی‌های C# ساخته
شده‌اند (چون WCF با DataContractSerializer نام اعضا را همین‌طور می‌فرستد):

```kotlin
@Serializable data class CustomerDto(
    val SHMO: Int = 0, val MONAME: String? = null, val man: Double? = 0.0, ...
)
```

اگر سرویس شما نام متفاوتی می‌فرستد، فقط `@SerialName("نام‌درسرور")` بالای فیلد بگذارید.
پاسخ‌های عمومی به‌صورت `AtiranResult<T> { Success, Result, Errors }` مدل شده‌اند و JSON
با حالت `ignoreUnknownKeys + isLenient` خوانده می‌شود، پس فیلدهای اضافی سرور خطا نمی‌دهند.

مسیرهای پیاده‌سازی‌شده (دقیقاً همان UriTemplate های WCF):

| عملیات اپ | مسیر سرویس |
|---|---|
| ورود ویزیتور | `POST /Post/LoginY` |
| فهرست مشتریان | `GET /Get/CustomerByShMo/0` |
| گروه‌های کالا / انبارها | `GET /Get/KaGroups` ، `/Get/Anbars` |
| کالاهای گروه | `GET /Get/KalasByGroupId/{id}` |
| چک‌ها | `GET /Get/Checks` |
| پیام‌ها | `GET /Get/VisitorMessages` |
| ثبت ویزیت | `POST /Post/AddVisit` |
| ثبت سفارش آنلاین | `POST /Post/AddOnlineOrder` |
| بررسی سلامت | `GET /Get/CompanyInfo` |

## ساختار پروژه

```
AtiranHamrahAndroid/
├── app/src/main/java/ir/atiran/hamrah/
│   ├── HamrahApp.kt, MainActivity.kt      ← نقطه ورود
│   ├── core/util/                          ← تاریخ شمسی، قالب‌بندی فارسی
│   ├── data/                               ← مدل‌ها، تنظیمات، API، مخزن
│   ├── ui/theme/                           ← پالت، وزیرمتن، تم تیره/روشن
│   ├── ui/components/                      ← دکمه سه‌بعدی، کاشی منو، کارت شیشه‌ای، ...
│   └── ui/screens/                         ← ۱۶ صفحه
├── app/src/main/res/
│   ├── font/                               ← وزیرمتن (مجوز OFL)
│   ├── mipmap-*/                           ← آیکن سه‌بعدی همه چگالی‌ها
│   └── xml/                                ← امنیت شبکه، قوانین بکاپ
└── gradle/ + gradlew                       ← wrapper رسمی Gradle 8.9
```

## نکات طراحی (چند کلمه درباره خلاقیت‌ها!)

- **دکمه سه‌بعدی (AtiranButton3D):** با `drawBehind` لایه اکستروژن زیر وجه اصلی کشیده می‌شود و
  هنگام لمس عمقش کم می‌شود — حس فرو رفتن فیزیکی، بدون کتابخانه خارجی
- **کاشی منو (Tile3D):** گرادیانت + هاله نور داخلی + قاب براق + فنر فشردن (spring)
- **پس‌زمینه Aurora:** سه هاله رنگی که با `infiniteTransition` آرام شنا می‌کنند
- **گوی آماری و نمودار هفتگی:** تماماً با Canvas و انیمیشن — بدون هیچ کتابخانه نمودار
- **داک پایین:** شیشه‌ای معلق با دکمه شناور طلایی «فاکتور جدید» در مرکز
- **اعداد و تاریخ‌ها:** همه فارسی (تبدیل خودکار + تقویم جلالی داخل اپ)

## رفع اشکال

| مشکل | راه‌حل |
|---|---|
| `gradlew: permission denied` | `chmod +x gradlew` |
| خطای دانلود Gradle | پراکسی/فیلترشکن؛ یا Gradle را دستی از services.gradle.org نصب و `distributionUrl` را هماهنگ کنید |
| اتصال به سرور برقرار نشد | پینگ/فایروال ویندوز سرور (پورت ۸۰)، درستی URL، و فعال‌بودن IIS |
| فونت عوض شد | فقط وزیرمتن بسته‌بندی شده — بدون نیاز به اینترنت |
