package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini REST API Models ---

data class Part(val text: String)
data class Content(val parts: List<Part>)

data class GenerationConfig(
    val temperature: Float? = 0.7f,
    val responseMimeType: String? = null
)

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null
)

data class Candidate(val content: Content?)
data class GeminiResponse(val candidates: List<Candidate>?)

// --- Retrofit API Service ---

interface GeminiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- API Client ---

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val service: GeminiService by lazy {
        retrofit.create(GeminiService::class.java)
    }

    /**
     * Helper to get API key safely. If not set, returns a placeholder.
     */
    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key == "MY_GEMINI_API_KEY" || key.isEmpty()) "" else key
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Ask the Gemini chatbot a question about Yemeni culture, experiences or travel.
     */
    suspend fun chatWithGuide(history: List<ChatMessageEntity>, newPrompt: String): String {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return generateLocalGuideFallback(newPrompt)
        }

        val systemInstruction = Content(
            parts = listOf(Part(text = "أنت 'مرشد تجربة الذكي'، خبير سياحي وثقافي متفوق لليمن ومساعد سياحي لمنصة 'تِجربة'. ساعد المهندسة رغد والمستخدمين في استكشاف ثقافة اليمن وعاداتها وتجاربها الفريدة بأسلوب ودود ومحفز وملم بالتفاصيل."))
        )

        // Map ChatMessageEntity history into Gemini Content objects
        val contents = history.map { msg ->
            val role = if (msg.sender == "USER") "user" else "model"
            // Note: Direct REST models have contents. Role mapping can be structured if needed,
            // but for simple chat, sending them together is excellent.
            Content(parts = listOf(Part(text = msg.text)))
        } + Content(parts = listOf(Part(text = newPrompt)))

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "عذراً، لم أستطع استيعاب الرد بالشكل المطلوب حالياً."
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API Error", e)
            generateLocalGuideFallback(newPrompt)
        }
    }

    /**
     * Request a structured travel itinerary in Arabic.
     */
    suspend fun generateTripPlan(
        startCity: String,
        durationDays: Int,
        budgetYer: Double,
        interests: List<String>
    ): String {
        val apiKey = getApiKey()
        val prompt = """
            أنشئ خطة رحلة سياحية مخصصة في اليمن تبدأ من مدينة ($startCity) وتستمر لمدة ($durationDays) أيام.
            الميزانية التقريبية للرحلة هي ($budgetYer) ريال يمني.
            الاهتمامات الرئيسية: ${interests.joinToString(", ")}.
            
            يرجى تزويدي بالخطة باللغة العربية، مقسمة يوماً بيوم مع ذكر الأنشطة الصباحية والمسائية، تقدير التكلفة التفصيلية لكل يوم، أسماء مرشدين محليين مقترحين، ونصائح هامة للأمان واللباس والمواصلات في هذه المناطق.
        """.trimIndent()

        if (apiKey.isEmpty()) {
            return generateLocalItineraryFallback(startCity, durationDays, budgetYer, interests)
        }

        val systemInstruction = Content(
            parts = listOf(Part(text = "أنت منظم رحلات ذكي لمنصة تِجربة في اليمن. أنشئ برامج سياحية متكاملة، أصيلة، وثرية تراعي العادات المحلية، والمواصلات الجبلية، وتقدم ترشيحات رائعة لزيارة المدرجات الزراعية والأسواق والمواقع الأثرية."))
        )

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.6f)
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "عذراً، لم نتمكن من توليد خطة مخصصة الآن."
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API Error", e)
            generateLocalItineraryFallback(startCity, durationDays, budgetYer, interests)
        }
    }

    // --- Elegant Fallbacks (when API key is empty or network fails) ---

    private fun generateLocalGuideFallback(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("بن") || p.contains("قهوة") || p.contains("خولاني") -> {
                "أهلاً بك! البن الخولاني اليمني هو أحد أفخر أنواع البن في العالم، ويُزرع في المدرجات الجبلية مثل حراز وبني مطر ورازح. تتميز حبة البن الخولاني بنكهتها الفريدة المعقدة الشبيهة بالشوكولاتة والتوابل بسبب التربة البركانية وارتفاع الجبال الشاهق (أكثر من 2000 متر عن سطح البحر). في تجربة قطف البن، نأخذك مباشرة إلى حقل العم علي في حراز لتجرب قطفها وإعداد القشر بنفسك!"
            }
            p.contains("سقطرى") || p.contains("دم الأخوين") || p.contains("دكسم") -> {
                "مرحباً بك في سقطرى! جزيرة سقطرى مصنفة عالمياً كأحد مواقع التراث الطبيعي لليونسكو نظراً لتنوعها البيولوجي الفريد. ثلث النباتات هناك لا توجد في أي مكان آخر في العالم، وعلى رأسها شجرة دم الأخوين (Dragon's Blood Tree) التي تتميز بشكلها الشبيه بالمظلة وعصارتها الحمراء ذات الخصائص الطبية المذهلة. ننصحك بتجربة التخييم في محمية دكسم الطبيعية لمشاهدتها عن قرب والاستمتاع بصفاء النجوم الدوعنية والسقطرية."
            }
            p.contains("سلتة") || p.contains("أكل") || p.contains("طعام") || p.contains("ملوج") -> {
                "المطبخ اليمني غني بالنكهات التاريخية! الطبق الأشهر هو 'السلتة' التي تُطهى في وعاء حجر بري ساخن يُسمى 'المدرة'. تتكون السلتة من مرق لحم، وخضروات، وخلطة 'السحاوق' الممزوجة بالحلبة المخفوقة التي تعلو الوعاء كرغوة كثيفة زكية الرائحة. تُقدم السلتة تغلي وتصدر فوراناً مهيباً، وتؤكل ساخنة مع خبز 'الملوج' الذي يخبز في تنور الحطب."
            }
            p.contains("عسل") || p.contains("دوعن") || p.contains("سدر") -> {
                "عسل السدر الملكي من وادي دوعن في حضرموت يُعد الذهب السائل لليمن وأغلى أنواع العسل عالمياً. يكتسب العسل جودته من جني النحل لرحيق أزهار أشجار السدر البرية في مواسم محددة، ويتميز بقوامه الكثيف ورائحته العطرية النفاذة ومفعوله الطبي والعلاجي القوي. في منصة 'تِجربة'، يمكنك مرافقة الشيخ عمر الدوعني مباشرة لزيارة المناحل!"
            }
            p.contains("أمان") || p.contains("نصيحة") || p.contains("سفر") -> {
                "نصائح السفر والتنقل في اليمن: نوصي دائماً بالتنسيق المسبق مع المرشدين المحليين في كل منطقة (مثل حراز أو سقطرى أو حضرموت). الطرق الجبلية خلابة للغاية ولكنها تحتاج إلى سيارة دفع رباعي 4x4 وسائق محلي متمرس بالمنعطفات الشاهقة. احرص على احترام العادات والتقاليد المحلية وارتداء اللباس المحتشم المناسب لثقافة القرى الجبلية، وستجد ترحيباً وكرماً يمنياً يغمر القلوب!"
            }
            else -> {
                "مرحباً بك في منصة 'تِجربة' التراثية! أنا مرشدك الذكي، يسعدني جداً مساعدتك في استكشاف عادات اليمن العريقة، بدءاً من أسرار البن الخولاني الأصيل والزراعة المدرجية في حراز، إلى أسرار صياغة الجنابي في باب اليمن، وسحر جزيرة سقطرى الأسطورية. اسألني عن أي تجربة أو تاريخ ثقافي وسأجيبك بكل حب وبهاء!"
            }
        }
    }

    private fun generateLocalItineraryFallback(
        startCity: String,
        durationDays: Int,
        budgetYer: Double,
        interests: List<String>
    ): String {
        return """
            📋 *خطة الرحلة الافتراضية المقترحة من منصة تِجربة (وضع غير متصل)*
            --------------------------------------------------------
            📍 نقطة الانطلاق: $startCity | ⏱ المدة: $durationDays أيام
            💰 الميزانية التقريبية: ${"%,.0f".format(budgetYer)} ريال يمني
            🌟 اهتماماتك المفضلة: ${interests.joinToString(", ")}

            *اليوم الأول: الاستكشاف الثقافي والانغماس المحلي*
            • صباحاً: التجمع والتحرك من وسط $startCity بالدفع الرباعي. التوجه نحو المدرجات الزراعية أو المعالم الأثرية المحيطة.
            • ظهراً: زيارة مجلس غداء محلي لتناول طبق "السلتة" الصنعانية الحارة مع خبز الملوج الطازج من التنور الطيني في ضيافة عائلة ريفية.
            • مساءً: جولة استكشافية في السوق التراثي القديم ومقابلة الحرفيين التقليديين (صاغة الفضة وصناع الجنابي التراثية).
            • التكلفة المتوقعة: 22,000 ريال يمني.
            • المرشد المقترح: المرشد الثقافي عبد الوهاب.

            *اليوم الثاني: عبق الأرض والأصالة*
            • صباحاً: الاستيقاظ مبكراً للاستمتاع بشروق الشمس الساحر فوق القمم الجبلية. مرافقة المزارعين لقطف البن التراثي من الحقول المعلقة.
            • ظهراً: ورشة عمل تفاعلية لتجفيف حبوب البن الخولاني، وتحميص القشر وإعداد قهوة القشر اليمنية التقليدية على الجمر مع تذوق حلويات الشعير البلدي.
            • مساءً: زيارة قلعة تاريخية قريبة والاستماع لقصص الحضارات السبئية والحميرية القديمة من كبار السن في القرية.
            • التكلفة المتوقعة: 18,000 ريال يمني.
            • المرشد المقترح: العم يحيى الحرازي.

            *اليوم الثالث: مغامرة سحر الطبيعة والوديان*
            • صباحاً: رحلة هايكنج خفيفة عبر ممرات القرى الحجرية القديمة والتقاط صور بانورامية خلابة للمنازل المعلقة فوق الضباب.
            • ظهراً: جلسة تذوق لعسل السدر الدوعني الملكي من مناحل جبلية مفتوحة وشرح طرق فرز شمع العسل التقليدية.
            • مساءً: حفل سمر شعبي بسيط على أنغام الموسيقى العود اليمنية وتبادل الهدايا والتقاط الصور التذكارية بزي التراث التقليدي قبل العودة لـ $startCity.
            • التكلفة المتوقعة: 25,000 ريال يمني.
            • المرشد المقترح: المنسق المحلي صالح السقطري.

            💡 *نصائح تِجربة الذهبية للسلامة والأصالة:*
            1. الطرق الجبلية في اليمن تحتاج إلى سيارات 4x4 ممتازة وسائقين محليين ذوي خبرة بالطرق الريفية.
            2. ننصح بارتداء ملابس مريحة ومناسبة للمشي الجبلي، ومحترمة للثقافة المجتمعية الريفية اليمنية.
            3. احتفظ دائماً بنقد كافٍ بالريال اليمني، فالتعاملات الريفية تتم نقداً بالكامل.
        """.trimIndent()
    }
}
