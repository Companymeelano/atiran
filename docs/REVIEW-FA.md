# 📋 گزارش بازبینی پروژه «آتیران همراه» (AtiranHamrah)

> تاریخ بازبینی: ۱۴۰۵/۰۶/۱۵ — بازبینی کامل بسته `AtiranHamrah.zip`، اسکریپت `eeee.sql` و باینری `AtiranLocalServices.dll`

---

## ۱) شناخت پروژه

پروژه فعلی شامل این بخش‌هاست:

| بخش | ماهیت | نقش |
|---|---|---|
| `LocalServices.svc` + `AtiranLocalServices.dll` | سرویس WCF (WebHttp/REST) روی .NET Framework 4.5.2 | لایه سرویس بین اپ ویندوزتبلت و پایگاه داده |
| `DataAccess/SACModel.*` (EF6 EDMX) | Entity Framework 6 Database-First | نگاشت جداول پایگاه `sac` |
| `bin/DataAccess/*.cs` | کلاس‌های POCO (۴۶ فایل) | مدل داده: CUSTOMER، visitor، sailfact، inventory، getchk، Visit و ... |
| `eeee.sql` | اسکریپت ساخت دیتابیس `Atiran2` | فقط `CREATE DATABASE` (بدون جداول!) |
| `AtiranPics/Thumbs.db` | فایل کش ویندوز | بدون ارزش — نباید در مخزن باشد |

**دامنه کسب‌وکار:** سامانه فروش سیار (SFA) برای ویزیتورها — ثبت فاکتور/پیش‌فاکتور، پیش‌دریافت، چک، مسیر روزانه (Masir)، پیام‌ها، مدیریت مشتری و انبار.

### سطح API استخراج‌شده از باینری (UriTemplate های WCF)

```
GET  /Get/Anbars                       /Get/KaGroups            /Get/KalasByGroupId/{groupId}
GET  /Get/CustomerByShMo/{shMo}        /Get/CustActs/{minRdf}/{shMo}   /Get/CustActDetails/{act_rdf}
GET  /Get/CustomerChecks/{shMo}        /Get/CustomerEtebar/{shMo}      /Get/CustomerFactors/{shMo}
GET  /Get/NotPaidFactors/{shMo}        /Get/PishFactors[/{shfacfo}]    /Get/PreInvoiceItems
GET  /Get/Checks                       /Get/CheckSetInfo        /Get/CompanyInfo
GET  /Get/VisitorMasir[/{VisitorID}]   /Get/VisitorMessages     /Get/VisitorSalesGoals
GET  /Get/DeviceInfo/{visitorId}       /Get/GetPeriods          /Get/GetShop/{typeSelect}
GET  /Get/KalaByShKa/{shKa}            /Get/KalaAnbByShKa/{shKa}        /Get/MaxShKa /Get/MaxShMo
GET  /Get/CountKa /Get/CountMo         /Get/Filtered*           /Get/Listofvisitororders
GET  /Get/RegisterDeviceAppId          /Get/SaleLine/{sysid}    /Get/ReadMessage/{id}
POST /POST/LoginVisitor                /Post/LoginY             /Post/isAdmin
POST /POST/AddCustomer                 /POST/EditCustomer       /Post/EditCustomerProfile
POST /Post/AddOnlineOrder              /Post/AddPishDaryaft     /Post/AddVisit[+Test]
POST /Post/GetCustomerByLogin          /Post/ChangeCustomerPassword
POST /Post/SetCustomerLocation         /Post/SetDeviceLocation(s) /Post/GetMessageFilter
```

این همان قراردادی است که اپ اندروید جدید (`AtiranHamrahAndroid/`) برای اتصال واقعی پیاده‌سازی کرده است.

---

## ۲) یافته‌های بازبینی — به ترتیب شدت

### 🔴 بحرانی (امنیتی)

| # | مشکل | توضیح | اصلاح |
|---|---|---|---|
| ۱ | **افشای اعتبارنامه پایگاه داده** | `Web.config` شامل `user id=atiranhamrah;password=ahmad100` به‌صورت متن‌آشکار است؛ در بسته‌بندی/گیت تاریخچه می‌ماند | در `server-fixed/Web.config` حذف شد + راهنمای `aspnet_regiis -pe` اضافه شد |
| ۲ | **روشن‌بودن directoryBrowse** | هر کسی با بازکردن آدرس سرویس، فهرست کامل فایل‌ها (شامل DLL و Web.config) را می‌بیند | بسته شد |
| ۳ | **نشت جزئیات خطا** (`includeExceptionDetailInFaults=true`) | Stack Trace داخلی (نام جداول، رشته‌ها، مسیرها) به کلاینت می‌رسد | خاموش شد |
| ۴ | **متادیتای عمومی WCF** (`httpGetEnabled=true` + نقطه mex) | کل قرارداد سرویس برای مهاجم نقشه می‌شود | خاموش شد (در توسعه داخلی روشنش کنید) |
| ۵ | **نبود احراز هویت روی Endpoint ها** | هیچ Security Binding یا توکنی دیده نمی‌شود؛ هر کلاینتِ داخل شبکه می‌تواند `AddCustomer`/`AddOnlineOrder` صدا بزند | پیشنهاد: افزودن Basic Authentication در IIS + فیلتر API Key در سرویس (نقشه راه) |
| ۶ | **فقط HTTP (بدون TLS)** | رمزهای عبور ویزیتور/مشتری روی سیم به‌صورت خام منتقل می‌شوند | فعال‌سازی SSL در IIS الزامی است |

### 🟠 مهم (ساختاری/کیفی)

| # | مشکل | توضیح | اصلاح |
|---|---|---|---|
| ۷ | **کدهای منبع سرویس موجود نیست** | فایل `LocalServices.svc.cs` (منطق اصلی) در بسته نیست؛ فقط `bin/AtiranLocalServices.dll` (کامپایل‌شده) هست؛ بازتولید و نگهداری عملاً ناممکن است | توصیه: بازیابی سورس از کنترل نسخه اصلی؛ الگوریتم API از DLL استخراج و مستند شد |
| ۸ | **اختلاط خروجی build با سورس** | پوشه `bin/` شامل DLL، PDB، سورس‌های POCO و فایل‌های NuGet است؛ ریشه پروژه (`.csproj`/`.sln`، `LocalServices.svc.cs`) ارسال نشده | ساختار پیشنهادی در `REVIEW-FA.md` بخش ۴ |
| ۹ | **اسکریپت ناقص دیتابیس** | `eeee.sql` فقط دیتابیس خالی می‌سازد (COMPATIBILITY_LEVEL = 120 = SQL Server 2014) و فاقد تمام جداول/رویه‌هاست | برای استقرار آزمایشی، اسکریپت کامل (Schema + Hamrah schema) لازم است |
| ۱۰ | **`Debug="true"` در .svc** | در عملیاتی ممنوع | `false` شد |
| ۱۱ | **`maxItemsInObjectGraph=2147483647` و نبود سقف پیام** | سطح حمله DoS | سقف ۱ میلیون آیتم و ۲MB پیام |
| ۱۲ | **Application Insights 1.2.3 (۲۰۱۶)** | بسیار قدیمی؛ کتابخانه Microsoft.AI در NuGet فعلی پشتیبانی نمی‌شود | به‌روزرسانی یا حذف در بازنویسی |
| ۱۳ | **فایل‌های زائد** | `Thumbs.db` (کش ویندوز) و DLL های تکراری در بسته | حذف از مخزن |
| ۱۴ | **فونت/رابط راست‌به‌چپ برای تبلت** | اپ فعلی ویندوزی است؛ نسخه موبایل به‌کل وجود ندارد | ✅ اپ اندروید بومی ساخته شد |

### 🟡 بهبود (فرسودگی فناوری)

- .NET Framework 4.5.2 (۲۰۱۴) — پایان پشتیبانی طولانی از ۲۰۲۲ گذشته
- WCF WebHttp — فریم‌ورکِ مرده؛ جایگزین: ASP.NET Core Web API / gRPC
- EF6 Database-First با EDMX — سنگین و بدون پشتیبانی NET Core.؛ جایگزین: EF Core
- Newtonsoft.Json 10.0.3 — قدیمی (حفره‌های شناخته‌شده در deserialization)
- SQL Server با COMPATIBILITY_LEVEL 120

---

## ۳) تحلیل مدل داده (استخراج‌شده از POCO ها)

هسته‌های اصلی و فیلدهای کلیدی که در اپ اندروید مدل شده‌اند:

```
CUSTOMER  (SHMO🔑, MONAME, code, addre, tell1, cell, cred=اعتبار, man=مانده,
           black_list, just_naghdi, group_rdf, vis_rdf, shomare_masir, Lat/Lng,
           Username/Password, ...)
visitor   (vis_rdf🔑, vis_name, vis_man, eteb, TedadFactorMojazMande,
           MablaghMojazMandeJahatFactorha, is_supervisor, ...)
sailfact  (shfacfo🔑, date, shmo→CUSTOMER, vis_rdf, sumlineall, tafif, tax,
           avarez, all, MabDaryaftFactor, tasvieh, Status, ...) + subsailfact
          (SHKA→inventory, TEDVAH, VAHPRICE, LINESUM, TafifLine, Gift, ...)
inventory (shka🔑, naka, coka, group_rdf→kagroup, vahsanj, mohvah=موجودی,
           reopoint=نقطه سفارش, ...)  + forosh_price (forosh1..5, Min/MaxPrice)
getchk    (rdf🔑, shmo, getchbank, shgetchk, getchkmab, sardate=سررسید,
           chk_satus, back, ...)
Visit     (VisitID🔑, VisRdf, Shmo, Duration, Created/Sent, Lat/Lng, SignatureImage)
MasirDay  (ID, Date_, VisRdf, CityRdf, RegionRdf, MasirRdf)
PishDaryaft + PishDaryaftPos/GetCheck/MultiFactor، cust_act، DeviceMessage(s)، Company، anbar
```

این ساختارها عیناً در `AtiranHamrahAndroid/.../data/model/Models.kt` با همان نام‌ها بازتولید شده‌اند.

---

## ۴) اقدامات انجام‌شده در همین بازبینی

1. **`server-fixed/`** — نسخه امن `Web.config` و `LocalServices.svc` (جدول بخش ۲ اعمال شد)
2. **`AtiranHamrahAndroid/`** — اپ اندروید کامل و آماده ساخت (Kotlin + Jetpack Compose) با:
   - منوی خلاقانه با کاشی‌های سه‌بعدی + دکمه‌های سه‌بعدی اختصاصی
   - آیکن سه‌بعدی (همه چگالی‌ها + Adaptive + Monochrome)
   - فونت فارسی وزیرمتن بسته‌بندی‌شده، تاریخ شمسی، اعداد فارسی
   - اتصال واقعی به سرویس WCF فعلی + حالت نمایشی آفلاین با صف همگام‌سازی
   - ۱۶ صفحه: اسپلش، ورود، داشبورد، منوی کامل، مشتریان (فهرست/پرونده)، فاکتورها (فهرست/جزئیات/ثبت)، کالا و انبار، چک‌ها، مسیر و ویزیت، پیام‌ها، پیش‌دریافت، گزارش‌ها، تنظیمات، درباره
3. **`preview/index.html`** — پیش‌نمایش تعاملی رابط کاربری در مرورگر (همان طراحی اپ اندروید)
4. مستندات فارسی کامل (`docs/`)

## ۵) نقشه راه پیشنهادی آینده

**مرحله ۱ (فوری):** اعمال `server-fixed`، فعال‌سازی HTTPS و احراز هویت در IIS، رمزنگاری connection string.

**مرحله ۲ (کوتاه‌مدت):** استقرار اپ اندروید بین ویزیتورها؛ تست هم‌سختی JSON بین DTO های اپ و `AtiranResult<T>` سرور (راهنمای `ANDROID-GUIDE-FA.md`).

**مرحله ۳ (میان‌مدت):** مهاجرت سرویس به **ASP.NET Core 8 Web API** با همان مسیرها (سپس فقط baseUrl اپ عوض می‌شود)، EF Core، JWT به‌جای رمز خام، و Swashbuckle برای مستندسازی.

**مرحله ۴ (بلندمدت):** پنل مدیریت تحت وب، گزارش‌های BI، اعلان فوری (FCM)، و ثبت موقعیت مکانی خودکار ویزیتور (فیلدهای Lat/Lng در Visit از قبل دیده شده است).
