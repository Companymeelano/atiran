package ir.atiran.hamrah.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * کلاینت ارتباط با سرویس WCF «AtiranLocalServices».
 * مسیرها دقیقاً همان UriTemplate های سرویس موجود هستند:
 *   GET  /Get/Anbars , /Get/KaGroups , /Get/KalasByGroupId/{groupId} , ...
 *   POST /Post/LoginY , /Post/AddOnlineOrder , /Post/AddVisit , ...
 *
 * نکته: نام فیلدهای JSON همان نام پراپرتی‌های C# است
 * (SHMO، MONAME، shka، naka و ...) و در صورت تفاوت جزئی با سرور شما،
 * فقط @SerialName ها را مطابقت دهید (راهنمای docs/ANDROID-GUIDE-FA.md).
 */
class AtiranApi {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }

    var baseUrl: String = ""
        private set

    fun configure(url: String) {
        baseUrl = url.trim().trimEnd('/')
    }

    val isConfigured: Boolean get() = baseUrl.isNotEmpty()

    /* ---------- مدل‌های انتقال داده (DTO) ---------- */

    /** ساختار پاسخ عمومی سرویس آتیران */
    @Serializable
    data class AtiranResult<T>(
        val Success: Boolean = true,
        val Result: T? = null,
        val Errors: List<String> = emptyList()
    )

    @Serializable
    data class LoginRequest(val Username: String, val Password: String)

    @Serializable
    data class VisitorDto(
        val vis_rdf: Int = 0,
        val vis_name: String? = null,
        val vis_cell: String? = null,
        val vis_man: Double? = 0.0,
        val eteb: Double? = 0.0,
        val TedadFactorMojazMande: Int? = 0,
        val MablaghMojazMandeJahatFactorha: Double? = 0.0,
        val is_supervisor: String? = null
    )

    @Serializable
    data class CustomerDto(
        val SHMO: Int = 0,
        val MONAME: String? = null,
        val code: String? = null,
        val addre: String? = null,
        val tell1: String? = null,
        val cell: String? = null,
        val cred: Double? = 0.0,
        val man: Double? = 0.0,
        val black_list: Int? = 0,
        val just_naghdi: Int? = 0,
        val active: String? = null,
        val group_rdf: Int? = 0
    )

    @Serializable
    data class KalaDto(
        val shka: Long = 0,
        val naka: String? = null,
        val coka: String? = null,
        val group_rdf: Int = 0,
        val vahsanj: String? = null,
        val mohvah: Double? = 0.0,
        val reopoint: Int? = 0,
        val active: String? = null
    )

    @Serializable
    data class GroupDto(
        val group_rdf: Int = 0,
        val group_name: String? = null
    )

    @Serializable
    data class AnbarDto(
        val rdf_anbar: Int = 0,
        val name: String? = null
    )

    @Serializable
    data class CheckDto(
        val rdf: Long = 0,
        val shmo: Long = 0,
        val getchbank: String? = null,
        val shgetchk: String? = null,
        val getchkmab: Double? = 0.0,
        val sardate: String? = null,
        val chk_satus: Int? = 0,
        val getchkdis: String? = null
    )

    @Serializable
    data class MessageDto(
        val ID: Long = 0,
        val Title: String? = null,
        val Body: String? = null,
        val Date_ : String? = null,
        val Time_ : String? = null,
        val Sender: String? = null,
        val Priority: Int? = 0
    )

    @Serializable
    data class AddVisitRequest(
        val VisRdf: Int,
        val Shmo: Int,
        val Duration: Int,
        val Created: Long,
        val Description: String? = null,
        val SaveLat: Double? = null,
        val SaveLng: Double? = null
    )

    @Serializable
    data class OrderLineDto(
        val SHKA: Long,
        val TEDVAH: Double,
        val VAHPRICE: Double,
        val LINESUM: Double
    )

    @Serializable
    data class AddOrderRequest(
        val Shmo: Int,
        val VisRdf: Int,
        val Lines: List<OrderLineDto>,
        val Tafif: Double = 0.0,
        val IsPre: Boolean = false
    )

    class ApiException(message: String) : Exception(message)

    /* ---------- هسته درخواست ---------- */

    private suspend fun rawGet(path: String): String = withContext(Dispatchers.IO) {
        if (!isConfigured) throw ApiException("نشانی سرور تنظیم نشده است")
        val request = Request.Builder()
            .url("$baseUrl/$path")
            .header("Accept", "application/json")
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw ApiException("خطای سرور: ${response.code}")
            body
        }
    }

    private suspend fun rawPost(path: String, payload: String): String = withContext(Dispatchers.IO) {
        if (!isConfigured) throw ApiException("نشانی سرور تنظیم نشده است")
        val media = "application/json; charset=utf-8".toMediaType()
        val body: RequestBody = payload.toRequestBody(media)
        val request = Request.Builder()
            .url("$baseUrl/$path")
            .post(body)
            .header("Accept", "application/json")
            .build()
        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw ApiException("خطای سرور: ${response.code}")
            text
        }
    }

    /* ---------- فراخوانی سرویس‌ها (دقیقاً مطابق UriTemplate های WCF) ---------- */

    suspend fun login(username: String, password: String): VisitorDto {
        val payload = json.encodeToString(LoginRequest.serializer(), LoginRequest(username, password))
        val raw = rawPost("Post/LoginY", payload)
        val result = json.decodeFromString(AtiranResult.serializer(VisitorDto.serializer()), raw)
        return result.Result ?: throw ApiException(result.Errors.firstOrNull() ?: "ورود ناموفق بود")
    }

    suspend fun customers(): List<CustomerDto> {
        val raw = rawGet("Get/CustomerByShMo/0")
        return json.decodeFromString(raw)
    }

    suspend fun groups(): List<GroupDto> {
        val raw = rawGet("Get/KaGroups")
        return json.decodeFromString(raw)
    }

    suspend fun anbars(): List<AnbarDto> {
        val raw = rawGet("Get/Anbars")
        return json.decodeFromString(raw)
    }

    suspend fun kalasByGroup(groupId: Int): List<KalaDto> {
        val raw = rawGet("Get/KalasByGroupId/$groupId")
        return json.decodeFromString(raw)
    }

    suspend fun checks(): List<CheckDto> {
        val raw = rawGet("Get/Checks")
        return json.decodeFromString(raw)
    }

    suspend fun messages(): List<MessageDto> {
        val raw = rawGet("Get/VisitorMessages")
        return json.decodeFromString(raw)
    }

    suspend fun addVisit(request: AddVisitRequest): Boolean {
        val payload = json.encodeToString(AddVisitRequest.serializer(), request)
        val raw = rawPost("Post/AddVisit", payload)
        val result = json.decodeFromString(AtiranResult.serializer(Boolean.serializer()), raw)
        return result.Success
    }

    suspend fun addOnlineOrder(request: AddOrderRequest): Boolean {
        val payload = json.encodeToString(AddOrderRequest.serializer(), request)
        val raw = rawPost("Post/AddOnlineOrder", payload)
        val result = json.decodeFromString(AtiranResult.serializer(Boolean.serializer()), raw)
        return result.Success
    }

    suspend fun ping(): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext false
        return@withContext try {
            val request = Request.Builder().url("$baseUrl/Get/CompanyInfo").build()
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }
}
