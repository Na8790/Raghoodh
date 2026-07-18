package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExperienceEntity::class,
        BookingEntity::class,
        UserProfileEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun experienceDao(): ExperienceDao
    abstract fun bookingDao(): BookingDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tajrubah_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            // 1. Populate Default User Profile for Engineer Raghad (Project Owner)
            val defaultProfile = UserProfileEntity(
                userId = "me",
                name = "م. رغد",
                email = "raghad@tajrubah.ye",
                walletBalanceYer = 185000.0,
                walletBalanceUsd = 350.0,
                points = 1250,
                badgeAr = "سفير ثقافي ذهبي",
                badgeEn = "Gold Cultural Ambassador",
                referralCode = "RAGHAD-TAJRUBAH"
            )
            db.userProfileDao().insertProfile(defaultProfile)

            // 2. Populate Authentic Yemeni Experiences
            val experiences = listOf(
                ExperienceEntity(
                    id = "exp_coffee_haraz",
                    titleAr = "قطف وإعداد البن الخولاني التراثي",
                    titleEn = "Harvesting & Brewing Khawlani Coffee",
                    category = "زراعة وثقافة",
                    locationAr = "حراز، محافظة صنعاء",
                    locationEn = "Haraz, Sana'a",
                    priceYer = 15000.0,
                    rating = 4.9,
                    durationAr = "يوم كامل",
                    durationEn = "Full Day",
                    imageResName = "img_banner_coffee",
                    descriptionAr = "شارك النحالين والمزارعين في قطف حبوب البن الخولاني الأصيل من المدرجات الجبلية التراثية المعلقة بمرتفعات حراز. ستتعلم طريقة التجفيف التقليدية تحت أشعة الشمس، ثم تحميص البن في المحماس وطحنه يدوياً وإعداد قهوة القشر اليمنية المعطرة بالهيل والزنجبيل على جمر الصنوبر مع العائلات المحلية.",
                    descriptionEn = "Join local farmers in picking authentic Khawlani coffee beans from the spectacular, terraced mountains of Haraz. Learn traditional sun-drying, roast beans in a stone clay pan, grind them manually, and brew aromatic Yemeni Qishr coffee seasoned with cardamom and ginger over charcoal with native hosts.",
                    providerNameAr = "العم علي الحرازي",
                    providerNameEn = "Uncle Ali Al-Harazi",
                    providerBioAr = "مزارع شغوف ورث جبل البن في حراز عن أجداده ويعمل في رعاية أشجار البن منذ 40 عاماً.",
                    providerBioEn = "A passionate farmer who inherited his terraced coffee farm from his ancestors, preserving heritage coffee for over 40 years.",
                    isFeatured = true
                ),
                ExperienceEntity(
                    id = "exp_socotra_dragon",
                    titleAr = "تخييم بين أشجار دم الأخوين الأسطورية",
                    titleEn = "Camping Under Dragon's Blood Trees",
                    category = "طبيعة ومغامرة",
                    locationAr = "هضبة دكسم، جزيرة سقطرى",
                    locationEn = "Dixam Plateau, Socotra",
                    priceYer = 30000.0,
                    rating = 5.0,
                    durationAr = "يومين (مبيت)",
                    durationEn = "2 Days (Overnight)",
                    imageResName = "img_banner_socotra",
                    descriptionAr = "رحلة تخييم بيئية فريدة في قلب هضبة دكسم بجزيرة سقطرى، حيث تحيط بك أشجار دم الأخوين النادرة كالمظلات السحرية. ستستمع إلى الحكايات الأسطورية للشجرة من سكان الجزيرة الأصليين (المهرة)، وتتعلم استخدام عصارتها العلاجية الحمراء وتناول وجبة عشاء تقليدية من الماعز المشوي والأرز البري تحت قبة من النجوم الساطعة.",
                    descriptionEn = "An eco-camping adventure in the heart of Dixam Plateau, Socotra Island, surrounded by the iconic Dragon's Blood Trees. Hear local legends, learn medical uses of the tree's red sap from native Mahri guides, and enjoy a traditional dinner of slow-roasted goat and wild rice under an untouched, starry canopy.",
                    providerNameAr = "سالم السقطري",
                    providerNameEn = "Salem Socotri",
                    providerBioAr = "مرشد بيئي سقطري أصيل وصديق للبيئة، يحفظ وديان وجبال سقطرى عن ظهر قلب.",
                    providerBioEn = "A native Socotri eco-guide and nature preservationist who knows every valley and cave of this magical island.",
                    isFeatured = true
                ),
                ExperienceEntity(
                    id = "exp_craft_janbiyah",
                    titleAr = "حرفة صياغة الجنبية الصنعانية",
                    titleEn = "Traditional Janbiyah Crafting Masterclass",
                    category = "حرف يدوية",
                    locationAr = "سوق الجنابي، باب اليمن",
                    locationEn = "Bab Al-Yaman, Old Sana'a",
                    priceYer = 12000.0,
                    rating = 4.8,
                    durationAr = "4 ساعات",
                    durationEn = "4 Hours",
                    imageResName = "img_app_logo", // Fallback to logo / custom icon if no other image is generated
                    descriptionAr = "ادخل إلى قلب سوق الجنابي التاريخي في صنعاء القديمة، واجلس بجوار معلم صياغة الجنبية الشهير. ستشهد صقل شفرات الصلب، والمشاركة في نحت وزخرفة المقبض المصنوع من خامات تقليدية، وتزيين الغمد المذهب بالخيوط الفضية في تجربة حية تعبق برائحة التاريخ.",
                    descriptionEn = "Enter the historic Janbiyah (traditional Yemeni dagger) market in Old Sana'a. Sit alongside a master craftsman to watch steel blades being forged, try your hand at polishing, and learn to embroider the gold-threaded scabbard in a living museum experience.",
                    providerNameAr = "الأستاذ يحيى الصائغ",
                    providerNameEn = "Master Yahya Al-Sayegh",
                    providerBioAr = "كبير صاغة التراث بباب اليمن، قضى حياته في صيانة هوية الرجل اليمني المتمثلة في جنببته.",
                    providerBioEn = "Master jeweler at Bab Al-Yaman, dedicated his life to crafting and preserving the iconic Yemeni Janbiyah.",
                    isFeatured = false
                ),
                ExperienceEntity(
                    id = "exp_culinary_salta",
                    titleAr = "أسرار طهي السلتة والمدرة الحجرية",
                    titleEn = "The Art of Yemeni Salta & Stone Baking",
                    category = "مأكولات شعبية",
                    locationAr = "صنعاء القديمة التراثية",
                    locationEn = "Old Sana'a",
                    priceYer = 8000.0,
                    rating = 4.95,
                    durationAr = "3 ساعات",
                    durationEn = "3 Hours",
                    imageResName = "img_banner_coffee", // fallback or placeholder
                    descriptionAr = "تعلم طريقة طهي السلتة - الطبق الوطني لليمن - في 'المدرة' وهي الوعاء المصنوع من الحجر البري الأسود المنحوت يدوياً. ستشرف على غلي المرق ومزج 'الحلبة' المخفوقة بالخلطة السرية وإعداد خبز 'الملوج' العملاق الحار المخبوز في تنور طيني تقليدي لتتناول وجبتك بمذاق لا يُنسى.",
                    descriptionEn = "Master cooking Salta - Yemen's national dish - inside a hand-carved black volcanic stone pot ('Madra'). Whip the savory fenugreek froth, compound traditional lamb broth, and bake giant bubbly 'Maloog' flatbread in a burning clay tannour.",
                    providerNameAr = "الحاجة أم أحمد",
                    providerNameEn = "Hajjah Um Ahmad",
                    providerBioAr = "طاهية شعبية تدير مجلساً عائلياً لإحياء المطبخ اليمني التقليدي بلمساته التاريخية الحقيقية.",
                    providerBioEn = "A traditional chef operating a cozy homestay dining room, keeping centuries-old culinary secrets alive.",
                    isFeatured = false
                ),
                ExperienceEntity(
                    id = "exp_honey_doan",
                    titleAr = "جني العسل الدوعني الملكي الفاخر",
                    titleEn = "Wadi Do'an Sidr Honey Harvesting",
                    category = "زراعة وثقافة",
                    locationAr = "وادي دوعن، حضرموت",
                    locationEn = "Wadi Do'an, Hadramout",
                    priceYer = 25000.0,
                    rating = 4.97,
                    durationAr = "يوم كامل",
                    durationEn = "Full Day",
                    imageResName = "img_banner_socotra", // fallback
                    descriptionAr = "انطلق في رحلة إلى وادي دوعن الشهير برحيق شجر السدر الجبلي. ارتدِ بدلة واقية واذهب رفقة كبار النحالين الحضارم لفرز شمع الخلايا واستخلاص أغلى وأجود أنواع العسل في العالم بطرق تقليدية لم تتغير منذ آلاف السنين وتذوق قرص العسل ساخناً من الخلية.",
                    descriptionEn = "Journey into Hadramout's Wadi Do'an, home to the most expensive honey in the world. Suit up and accompany traditional beekeepers to harvest sweet, medicinal Sidr honey from mountainside hives, tasting pure liquid gold fresh from the honeycomb.",
                    providerNameAr = "الشيخ عمر الدوعني",
                    providerNameEn = "Sheikh Omar Al-Do'ani",
                    providerBioAr = "نحال وباحث تراثي من حضرموت، ينتمي لعائلة دوعنية توارثت صناعة العسل الفاخر لثلاثة أجيال.",
                    providerBioEn = "A prominent Hadrami beekeeper from a dynasty that has produced elite royal Sidr honey for three generations.",
                    isFeatured = true
                ),
                ExperienceEntity(
                    id = "exp_marine_fishing",
                    titleAr = "صيد صيرة التقليدي ومخبازة عدن",
                    titleEn = "Traditional Fishing & Adeni Mikhbazah",
                    category = "بحر وأنشطة",
                    locationAr = "مرسى صيرة التاريخي، عدن",
                    locationEn = "Sira Port, Aden",
                    priceYer = 18000.0,
                    rating = 4.75,
                    durationAr = "6 ساعات",
                    durationEn = "6 Hours",
                    imageResName = "img_app_logo", // fallback
                    descriptionAr = "اركب قارب الصيد الخشبي التقليدي عند الفجر بجوار قلعة صيرة التاريخية في عدن. تعلم رمي الشباك والصيد بالخيط اليدوي البسيط، ثم عد محملاً بالأسماك الطازجة (مثل الثمد أو الزينوب) ليتم طهيها وتحميرها في المخبازة العدنية الشهيرة مع الصالونة الحارة وخبز الرشوش بالفرن التنوري الساخن.",
                    descriptionEn = "Board a hand-crafted wooden vessel at dawn near Aden's historic Sira Castle. Practice traditional hand-line fishing, then bring your fresh catch to a legendary 'Mikhbazah' tavern to bake it open-faced in volcanic clay ovens, paired with spicy Adeni sauce and giant buttery 'Rashoush' bread.",
                    providerNameAr = "الكابتن عوض العدني",
                    providerNameEn = "Captain Awadh Al-Adani",
                    providerBioAr = "بحار عدني مخضرم يعرف تيارات خليج عدن ومواقع تجمع الأسماك الممتازة منذ صباه.",
                    providerBioEn = "A veteran Adeni seafarer who has navigated the warm waters of the Arabian Sea since boyhood.",
                    isFeatured = false
                )
            )
            db.experienceDao().insertAll(experiences)
        }
    }
}
