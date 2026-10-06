package com.minfilter.app

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import com.minfilter.app.data.BlockRules
import com.minfilter.app.filter.MinFilterVpnService
import com.minfilter.app.admin.MinFilterDeviceAdminReceiver
import com.minfilter.app.protection.AdminLock
import com.minfilter.app.ui.ContentLibrary
import com.minfilter.app.ui.Language

class MainActivity : Activity() {
    private val bg = Color.rgb(10,12,38); private val panel = Color.rgb(22,25,65); private val panel2 = Color.rgb(29,33,82)
    private val purple = Color.rgb(116,70,255); private val cyan = Color.rgb(48,224,204); private val white = Color.WHITE
    private var protectionOn = false; private lateinit var root: LinearLayout
    private val lock by lazy { AdminLock(this) }

    private val tr = mapOf(
        "bn" to mapOf("home" to "হোম","filter" to "ফিল্টার","settings" to "সেটিংস","safe" to "নিরাপদ ইন্টারনেট • সুন্দর ভবিষ্যৎ","on" to "সুরক্ষা চালু","off" to "সুরক্ষা বন্ধ","start" to "এক ট্যাপে সুরক্ষা চালু করুন","active" to "আপনার ইন্টারনেট এখন ফিল্টার হচ্ছে","tap" to "ট্যাপ করে ফিল্টারিং শুরু করুন","content" to "কনটেন্ট সুরক্ষা","short" to "শর্ট ভিডিও ব্লক","web" to "ওয়েব / DNS","quran" to "আয়াত ও হাদিস","quotes" to "মনীষীদের কথা","marriage" to "বিবাহ ও পরিবার","language" to "ভাষা","pin" to "Protection PIN","trusted" to "Trusted / Whitelist","custom" to "Custom blocklist","about" to "সম্পর্কে","save" to "সংরক্ষণ","cancel" to "বাতিল","ok" to "ঠিক আছে","back" to "ফিরে যান","safeMode" to "নিরাপদ মোড","languagePick" to "ভাষা নির্বাচন","pinNeed" to "PIN দিন","pinNew" to "নতুন ৪–১২ সংখ্যার PIN দিন","wrongPin" to "ভুল PIN","pinOn" to "Protection PIN চালু হয়েছে","pinOff" to "Protection PIN বন্ধ হয়েছে","domains" to "ডোমেইন তালিকা","empty" to "কোনো ডোমেইন যোগ করা হয়নি","dns" to "ওয়েবসাইট / DNS ফিল্টার","dnsSub" to "ঝুঁকিপূর্ণ ডোমেইন ও নিরাপদ DNS ব্যবহার","family" to "Family DNS সুরক্ষা","familySub" to "অশালীন ও ক্ষতিকর ডোমেইনের বিরুদ্ধে অতিরিক্ত DNS স্তর","malware" to "Malware / Phishing DNS","malwareSub" to "ক্ষতিকর ডোমেইনের বিরুদ্ধে অতিরিক্ত DNS স্তর","youtube" to "YouTube সম্পূর্ণ ব্লক","instagram" to "Instagram সম্পূর্ণ ব্লক","shortSub" to "TikTok, Likee, SnackVideo ও Kwai-এর ডোমেইন","limitations" to "গুরুত্বপূর্ণ সীমাবদ্ধতা","limText" to "Min Filter domain/network স্তরে কাজ করে; এটি প্রতিটি encrypted ছবি, ভিডিও, অডিও বা লেখাকে নিশ্চিতভাবে শনাক্ত করার universal classifier নয়।","contentTitle" to "কনটেন্ট সুরক্ষা","contentSub" to "নেটওয়ার্ক ফিল্টার + ইতিবাচক কনটেন্ট","sources" to "রেফারেন্স","parent" to "প্যারেন্টাল কন্ট্রোল","aboutText" to "Min Filter — নিরাপদ ইন্টারনেট, সুন্দর ভবিষ্যৎ।\n\nএক ট্যাপে VPN protection, domain/DNS filtering, whitelist, custom blocklist এবং ইতিবাচক কনটেন্ট।","protected" to "Protection বন্ধ করতে PIN লাগবে","restart" to "ফিল্টার সেটিংস আপডেট হয়েছে","protectionLock" to "Protection Lock","lockChoose" to "কত দিনের জন্য Protection Lock?","lockActive" to "Protection Lock চালু","lockNone" to "Protection Lock বন্ধ","lockUntil" to "লক শেষ হবে","lockSet" to "Protection Lock চালু করুন","lockClear" to "Lock শেষ হয়েছে","deviceOwner" to "Device Owner / Uninstall protection","deviceOwnerYes" to "Device Owner সক্রিয় — uninstall block করা সম্ভব","deviceOwnerNo" to "সাধারণ ফোন — শক্ত uninstall block নেই","adminInfo" to "শক্ত uninstall protection-এর জন্য Android Device Owner দরকার। সাধারণ ইনস্টলে Android এই ক্ষমতা দেয় না।","lockExpired" to "Protection Lock-এর সময় শেষ হয়েছে","shortNote" to "Shorts/Reels-কে DNS স্তরে আলাদা করে নির্ভরযোগ্যভাবে ব্লক করা যায় না; পুরো ডোমেইন ব্লক হয়।","domainHint" to "প্রতি লাইনে একটি ডোমেইন লিখুন","invalidPin" to "৪–১২ সংখ্যার PIN দিন","changePin" to "PIN পরিবর্তন","disablePin" to "PIN বন্ধ করুন"),
        "en" to mapOf("home" to "Home","filter" to "Filter","settings" to "Settings","safe" to "SAFE INTERNET • BETTER FUTURE","on" to "Protection ON","off" to "Protection OFF","start" to "Start protection with one tap","active" to "Your internet is being filtered","tap" to "Tap to start filtering","content" to "Content Safety","short" to "Block short video","web" to "Web / DNS","quran" to "Qur'an & Hadith","quotes" to "Wise Words","marriage" to "Marriage & Family","language" to "Language","pin" to "Protection PIN","trusted" to "Trusted / Whitelist","custom" to "Custom blocklist","about" to "About","save" to "Save","cancel" to "Cancel","ok" to "OK","back" to "Back","safeMode" to "Safe mode","languagePick" to "Choose language","pinNeed" to "Enter PIN","pinNew" to "Create a 4–12 digit PIN","wrongPin" to "Wrong PIN","pinOn" to "Protection PIN enabled","pinOff" to "Protection PIN disabled","domains" to "Domain list","empty" to "No domains added","dns" to "Website / DNS filter","dnsSub" to "Risky domains and safer DNS","family" to "Family DNS protection","familySub" to "Extra DNS layer against adult and harmful domains","malware" to "Malware / Phishing DNS","malwareSub" to "Extra DNS layer against known harmful domains","youtube" to "Block YouTube completely","instagram" to "Block Instagram completely","shortSub" to "TikTok, Likee, SnackVideo and Kwai domains","limitations" to "Important limitations","limText" to "Min Filter works at the domain/network layer; it is not a universal classifier that can reliably inspect every encrypted image, video, audio or text item.","contentTitle" to "Content Safety","contentSub" to "Network filtering + positive content","sources" to "References","parent" to "Parental control","aboutText" to "Min Filter — Safe internet, better future.\n\nOne-tap VPN protection, domain/DNS filtering, whitelist, custom blocklist and positive content.","protected" to "PIN required to stop protection","restart" to "Filter settings updated","protectionLock" to "Protection Lock","lockChoose" to "How many days should Protection Lock last?","lockActive" to "Protection Lock is active","lockNone" to "Protection Lock is off","lockUntil" to "Lock ends","lockSet" to "Start Protection Lock","lockClear" to "Protection Lock expired","deviceOwner" to "Device Owner / Uninstall protection","deviceOwnerYes" to "Device Owner active — uninstall blocking is available","deviceOwnerNo" to "Normal phone — strong uninstall blocking is unavailable","adminInfo" to "Strong uninstall protection requires Android Device Owner. A normal install cannot grant itself this authority.","lockExpired" to "Protection Lock has expired","shortNote" to "Shorts/Reels cannot be reliably separated at DNS level; the whole domain is blocked.","domainHint" to "Enter one domain per line","invalidPin" to "Enter a 4–12 digit PIN","changePin" to "Change PIN","disablePin" to "Disable PIN"),
        "ar" to mapOf("home" to "الرئيسية","filter" to "التصفية","settings" to "الإعدادات","safe" to "إنترنت آمن • مستقبل أفضل","on" to "الحماية مفعلة","off" to "الحماية متوقفة","start" to "ابدأ الحماية بلمسة واحدة","active" to "يتم الآن تصفية الإنترنت","tap" to "اضغط لبدء التصفية","content" to "حماية المحتوى","short" to "حظر الفيديو القصير","web" to "الويب / DNS","quran" to "القرآن والحديث","quotes" to "كلمات نافعة","marriage" to "الزواج والأسرة","language" to "اللغة","pin" to "رمز حماية","trusted" to "القائمة الموثوقة","custom" to "قائمة الحظر المخصصة","about" to "حول التطبيق","save" to "حفظ","cancel" to "إلغاء","ok" to "حسنًا","back" to "رجوع","safeMode" to "الوضع الآمن","languagePick" to "اختيار اللغة","pinNeed" to "أدخل الرمز","pinNew" to "أنشئ رمزًا من 4–12 رقمًا","wrongPin" to "الرمز غير صحيح","pinOn" to "تم تفعيل رمز الحماية","pinOff" to "تم تعطيل رمز الحماية","domains" to "قائمة النطاقات","empty" to "لا توجد نطاقات","dns" to "تصفية الويب / DNS","dnsSub" to "نطاقات خطرة وDNS أكثر أمانًا","family" to "حماية Family DNS","familySub" to "طبقة DNS إضافية ضد المواقع الضارة وغير المناسبة","malware" to "حماية Malware / Phishing","malwareSub" to "طبقة DNS إضافية ضد النطاقات الضارة المعروفة","youtube" to "حظر YouTube بالكامل","instagram" to "حظر Instagram بالكامل","shortSub" to "نطاقات TikTok وLikee وSnackVideo وKwai","limitations" to "حدود مهمة","limText" to "يعمل Min Filter على مستوى النطاق والشبكة؛ وليس مصنفًا شاملًا يستطيع فحص كل صورة أو فيديو أو صوت أو نص مشفر بشكل موثوق.","contentTitle" to "حماية المحتوى","contentSub" to "تصفية الشبكة + محتوى نافع","sources" to "المراجع","parent" to "رقابة الوالدين","about" to "حول التطبيق","aboutText" to "Min Filter — إنترنت آمن، مستقبل أفضل.\n\nحماية VPN بلمسة واحدة، وتصفية DNS والنطاقات، والقائمة الموثوقة وقائمة الحظر والمحتوى النافع.","protected" to "يلزم رمز لإيقاف الحماية","restart" to "تم تحديث إعدادات التصفية","protectionLock" to "قفل الحماية","lockChoose" to "كم يومًا لقفل الحماية؟","lockActive" to "قفل الحماية مفعّل","lockNone" to "قفل الحماية متوقف","lockUntil" to "ينتهي القفل","lockSet" to "تفعيل قفل الحماية","lockClear" to "انتهى القفل","deviceOwner" to "مالك الجهاز / منع إلغاء التثبيت","deviceOwnerYes" to "مالك الجهاز مفعّل — يمكن منع إلغاء التثبيت","deviceOwnerNo" to "جهاز عادي — لا يوجد منع قوي لإلغاء التثبيت","adminInfo" to "منع إلغاء التثبيت القوي يحتاج إلى Android Device Owner. التثبيت العادي لا يمنح التطبيق هذه الصلاحية.","lockExpired" to "انتهت مدة قفل الحماية","shortNote" to "لا يمكن فصل Shorts/Reels بشكل موثوق على مستوى DNS؛ يتم حظر النطاق بالكامل.","domainHint" to "أدخل نطاقًا واحدًا في كل سطر","invalidPin" to "أدخل رمزًا من 4–12 رقمًا","changePin" to "تغيير الرمز","disablePin" to "تعطيل الرمز"),
        "ur" to mapOf("home" to "ہوم","filter" to "فلٹر","settings" to "ترتیبات","safe" to "محفوظ انٹرنیٹ • بہتر مستقبل","on" to "تحفظ فعال","off" to "تحفظ بند","start" to "ایک ٹچ سے تحفظ شروع کریں","active" to "آپ کا انٹرنیٹ فلٹر ہو رہا ہے","tap" to "فلٹرنگ شروع کرنے کے لیے دبائیں","content" to "مواد کی حفاظت","short" to "شارٹ ویڈیو بلاک","web" to "ویب / DNS","quran" to "قرآن و حدیث","quotes" to "دانائی کی باتیں","marriage" to "نکاح و خاندان","language" to "زبان","pin" to "Protection PIN","trusted" to "قابل اعتماد فہرست","custom" to "اپنی بلاک لسٹ","about" to "تعارف","save" to "محفوظ کریں","cancel" to "منسوخ","ok" to "ٹھیک ہے","back" to "واپس","safeMode" to "محفوظ موڈ","languagePick" to "زبان منتخب کریں","pinNeed" to "PIN درج کریں","pinNew" to "4–12 ہندسوں کا PIN بنائیں","wrongPin" to "غلط PIN","pinOn" to "Protection PIN فعال ہوگیا","pinOff" to "Protection PIN بند ہوگیا","domains" to "ڈومین فہرست","empty" to "کوئی ڈومین نہیں","dns" to "ویب سائٹ / DNS فلٹر","dnsSub" to "خطرناک ڈومین اور محفوظ DNS","family" to "Family DNS حفاظت","familySub" to "نامناسب اور نقصان دہ ڈومین کے خلاف اضافی DNS تہہ","malware" to "Malware / Phishing DNS","malwareSub" to "معلوم نقصان دہ ڈومین کے خلاف اضافی DNS تہہ","youtube" to "YouTube مکمل بلاک","instagram" to "Instagram مکمل بلاک","shortSub" to "TikTok، Likee، SnackVideo اور Kwai کے ڈومین","limitations" to "اہم حدود","limText" to "Min Filter ڈومین/نیٹ ورک سطح پر کام کرتا ہے؛ یہ ہر encrypted تصویر، ویڈیو، آڈیو یا متن کو یقینی طور پر شناخت کرنے والا universal classifier نہیں ہے۔","contentTitle" to "مواد کی حفاظت","contentSub" to "نیٹ ورک فلٹر + مثبت مواد","sources" to "حوالے","parent" to "والدین کا کنٹرول","aboutText" to "Min Filter — محفوظ انٹرنیٹ، بہتر مستقبل۔\n\nایک ٹچ VPN حفاظت، DNS/ڈومین فلٹرنگ، whitelist، custom blocklist اور مثبت مواد۔","protected" to "تحفظ بند کرنے کے لیے PIN ضروری ہے","restart" to "فلٹر سیٹنگز اپ ڈیٹ ہوگئیں","protectionLock" to "Protection Lock","lockChoose" to "Protection Lock کتنے دن کے لیے؟","lockActive" to "Protection Lock فعال ہے","lockNone" to "Protection Lock بند ہے","lockUntil" to "لاک ختم ہوگا","lockSet" to "Protection Lock شروع کریں","lockClear" to "Lock ختم ہوگیا","deviceOwner" to "Device Owner / Uninstall protection","deviceOwnerYes" to "Device Owner فعال — uninstall block ممکن ہے","deviceOwnerNo" to "عام فون — مضبوط uninstall block نہیں","adminInfo" to "مضبوط uninstall protection کے لیے Android Device Owner ضروری ہے۔ عام انسٹال میں یہ اختیار نہیں ملتا۔","lockExpired" to "Protection Lock کی مدت ختم ہوگئی","shortNote" to "DNS سطح پر Shorts/Reels کو قابلِ اعتماد طریقے سے الگ نہیں کیا جا سکتا؛ پورا ڈومین بلاک ہوگا۔","domainHint" to "ہر سطر میں ایک ڈومین لکھیں","invalidPin" to "4–12 ہندسوں کا PIN درج کریں","changePin" to "PIN تبدیل کریں","disablePin" to "PIN بند کریں"),
        "hi" to mapOf("home" to "होम","filter" to "फ़िल्टर","settings" to "सेटिंग्स","safe" to "सुरक्षित इंटरनेट • बेहतर भविष्य","on" to "सुरक्षा चालू","off" to "सुरक्षा बंद","start" to "एक टैप से सुरक्षा शुरू करें","active" to "आपका इंटरनेट फ़िल्टर हो रहा है","tap" to "फ़िल्टरिंग शुरू करने के लिए टैप करें","content" to "कंटेंट सुरक्षा","short" to "शॉर्ट वीडियो ब्लॉक","web" to "वेब / DNS","quran" to "कुरआन और हदीस","quotes" to "ज्ञान की बातें","marriage" to "विवाह और परिवार","language" to "भाषा","pin" to "Protection PIN","trusted" to "Trusted / Whitelist","custom" to "Custom blocklist","about" to "ऐप के बारे में","save" to "सहेजें","cancel" to "रद्द करें","ok" to "ठीक है","back" to "वापस","safeMode" to "सुरक्षित मोड","languagePick" to "भाषा चुनें","pinNeed" to "PIN दर्ज करें","pinNew" to "4–12 अंकों का PIN बनाएं","wrongPin" to "गलत PIN","pinOn" to "Protection PIN चालू है","pinOff" to "Protection PIN बंद है","domains" to "डोमेन सूची","empty" to "कोई डोमेन नहीं","dns" to "वेबसाइट / DNS फ़िल्टर","dnsSub" to "जोखिम वाले डोमेन और सुरक्षित DNS","family" to "Family DNS सुरक्षा","familySub" to "अनुचित और हानिकारक डोमेन के खिलाफ अतिरिक्त DNS स्तर","malware" to "Malware / Phishing DNS","malwareSub" to "ज्ञात हानिकारक डोमेन के खिलाफ अतिरिक्त DNS स्तर","youtube" to "YouTube पूरी तरह ब्लॉक","instagram" to "Instagram पूरी तरह ब्लॉक","shortSub" to "TikTok, Likee, SnackVideo और Kwai के डोमेन","limitations" to "महत्वपूर्ण सीमाएं","limText" to "Min Filter डोमेन/नेटवर्क स्तर पर काम करता है; यह हर encrypted image, video, audio या text को विश्वसनीय रूप से पहचानने वाला universal classifier नहीं है।","contentTitle" to "कंटेंट सुरक्षा","contentSub" to "नेटवर्क फ़िल्टर + सकारात्मक कंटेंट","sources" to "संदर्भ","parent" to "अभिभावक नियंत्रण","aboutText" to "Min Filter — सुरक्षित इंटरनेट, बेहतर भविष्य।\n\nएक टैप VPN सुरक्षा, DNS/डोमेन फ़िल्टरिंग, whitelist, custom blocklist और सकारात्मक कंटेंट।","protected" to "सुरक्षा बंद करने के लिए PIN चाहिए","restart" to "फ़िल्टर सेटिंग अपडेट हो गई","protectionLock" to "Protection Lock","lockChoose" to "Protection Lock कितने दिनों के लिए?","lockActive" to "Protection Lock चालू है","lockNone" to "Protection Lock बंद है","lockUntil" to "लॉक समाप्त होगा","lockSet" to "Protection Lock चालू करें","lockClear" to "Lock समाप्त हो गया","deviceOwner" to "Device Owner / Uninstall protection","deviceOwnerYes" to "Device Owner सक्रिय — uninstall block संभव है","deviceOwnerNo" to "सामान्य फोन — मजबूत uninstall block नहीं","adminInfo" to "मजबूत uninstall protection के लिए Android Device Owner चाहिए। सामान्य इंस्टॉल में यह अधिकार नहीं मिलता।","lockExpired" to "Protection Lock की अवधि समाप्त हो गई","shortNote" to "DNS स्तर पर Shorts/Reels को विश्वसनीय रूप से अलग नहीं किया जा सकता; पूरा डोमेन ब्लॉक होगा।","domainHint" to "हर पंक्ति में एक डोमेन लिखें","invalidPin" to "4–12 अंकों का PIN दर्ज करें","changePin" to "PIN बदलें","disablePin" to "PIN बंद करें"))

    private fun s(k: String): String = tr[Language.current(this)]?.get(k) ?: tr[Language.EN]!!.getValue(k)

    override fun onCreate(state: Bundle?) { super.onCreate(state); Language.apply(this); lock.isTimedLocked(); applyDeviceOwnerUninstallPolicy(); protectionOn = MinFilterVpnService.isRunning(); showHome() }
    override fun onResume() {
        super.onResume()
        val locked = lock.isTimedLocked()
        applyDeviceOwnerUninstallPolicy()
        protectionOn = MinFilterVpnService.isRunning()
        if (!locked && !lock.isTimedLocked()) showHome()
    }
    private fun bgDrawable(color:Int, radius:Float=24f, stroke:Int?=null)=GradientDrawable().apply{setColor(color);cornerRadius=radius;stroke?.let{setStroke(2,it)}}
    private fun tv(t:String,size:Float,color:Int=white)=TextView(this).apply{ text=t;textSize=size;setTextColor(color);includeFontPadding=true }
    private fun base()=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,16,18,12);setBackgroundColor(bg)}
    private fun button(t:String,action:()->Unit)=tv(t,15f).apply{gravity=Gravity.CENTER;background=bgDrawable(purple,60f);setPadding(18,16,18,16);setOnClickListener{action()}}
    private fun header(title:String,back:Boolean=true)=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL; if(back){val b=tv("‹",34f);b.setOnClickListener{showHome()};addView(b,LinearLayout.LayoutParams(48,60))};addView(tv("◈  $title",21f),LinearLayout.LayoutParams(0,60,1f));val g=tv("⚙",22f);g.setOnClickListener{showSettings()};addView(g,LinearLayout.LayoutParams(44,60))}

    private fun showHome(){
        root=base();root.addView(header("Min Filter",false));val scroll=ScrollView(this);val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER_HORIZONTAL}
        val hero=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER_HORIZONTAL;background=bgDrawable(panel2,30f);setPadding(18,20,18,22)}
        val badge=tv(s("safe"),11f,Color.rgb(190,200,255));badge.gravity=Gravity.CENTER;hero.addView(badge,LinearLayout.LayoutParams(-1,36))
        val p=tv("⏻",62f,if(protectionOn)cyan:purple);p.gravity=Gravity.CENTER;p.background=bgDrawable(Color.TRANSPARENT,100f,if(protectionOn)cyan else purple);p.setOnClickListener{connect()};hero.addView(p,LinearLayout.LayoutParams(160,160).apply{gravity=Gravity.CENTER})
        val st=tv(if(protectionOn)s("on") else s("off"),22f,if(protectionOn)cyan else white);st.gravity=Gravity.CENTER;hero.addView(st,LinearLayout.LayoutParams(-1,42))
        val sub=tv(if(protectionOn)s("active") else s("tap"),14f,Color.LTGRAY);sub.gravity=Gravity.CENTER;hero.addView(sub,LinearLayout.LayoutParams(-1,38));hero.addView(button(if(protectionOn)s("on") else s("start")){connect()},LinearLayout.LayoutParams(-1,58).apply{topMargin=10});c.addView(hero,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=14})
        val cards=listOf(s("content") to {showContentSafety()},s("short") to {showShort()},s("web") to {showFilter()},s("quran") to {showPositive()},s("quotes") to {showPositive()},s("marriage") to {showPositive()});cards.chunked(2).forEach{rowItems->val row=LinearLayout(this);rowItems.forEach{(t,a)->val v=tv(t,14f);v.gravity=Gravity.CENTER;v.background=bgDrawable(panel,22f,Color.rgb(56,61,125));v.setOnClickListener{a()};row.addView(v,LinearLayout.LayoutParams(0,82,1f).apply{setMargins(4,4,4,4)})};c.addView(row)}
        val info=tv("🔒  ${s("limitations")}\n${s("limText")}",13f,Color.LTGRAY);info.background=bgDrawable(panel,22f);info.setPadding(18,16,18,16);c.addView(info,LinearLayout.LayoutParams(-1,-2).apply{topMargin=8});scroll.addView(c);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));root.addView(nav());setContentView(root)
    }
    private fun nav()=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;background=bgDrawable(Color.rgb(17,19,51),22f);listOf("⌂\n${s("home")}" to {showHome()},"◈\n${s("filter")}" to {showFilter()},"📖\n${s("quran")}" to {showPositive()},"⚙\n${s("settings")}" to {showSettings()}).forEach{(t,a)->val v=tv(t,10f,Color.LTGRAY);v.gravity=Gravity.CENTER;v.setPadding(2,7,2,7);v.setOnClickListener{a()};addView(v,LinearLayout.LayoutParams(0,58,1f))}}

    private fun showFilter(){
        if(lock.hasPin()) pinDialog(s("pinNeed"),false){if(lock.verify(it)) showFilterPage() else toast(s("wrongPin"))}
        else showFilterPage()
    }
    private fun showFilterPage()=page(s("filter")){
        addToggle(s("dns"),s("dnsSub"),pref("dns_filter",true),"dns_filter")
        addToggle(s("family"),s("familySub"),pref("family_dns",true),"family_dns")
        addToggle(s("malware"),s("malwareSub"),pref("malware_dns",true),"malware_dns")
        addToggle(s("short"),s("shortSub"),pref("short_video_filter",true),"short_video_filter")
        addToggle(s("youtube"),"",pref("block_youtube",false),"block_youtube")
        addToggle(s("instagram"),"",pref("block_instagram",false),"block_instagram")
    }
    private fun showShort(){
        if(lock.hasPin()) pinDialog(s("pinNeed"),false){if(lock.verify(it)) showShortPage() else toast(s("wrongPin"))}
        else showShortPage()
    }
    private fun showShortPage()=page(s("short")){
        addToggle(s("short"),s("shortSub"),pref("short_video_filter",true),"short_video_filter")
        addToggle(s("youtube"),s("shortNote"),pref("block_youtube",false),"block_youtube")
        addToggle(s("instagram"),s("shortNote"),pref("block_instagram",false),"block_instagram")
    }
    private fun showContentSafety()=page(s("contentTitle")){addRow(s("contentTitle"),s("contentSub")){showFilter()};addRow(s("limitations"),s("limText")){};addRow(s("short"),s("shortSub")){showShort()};addRow(s("quran"),s("sources")){showPositive()}}
    private fun showPositive()=page(s("quran")){ContentLibrary.items(Language.current(this)).forEach{item->val box=tv("${item.title}\n\n${item.body}\n\n${item.reference}",15f);box.background=bgDrawable(panel2,22f);box.setPadding(18,18,18,18);addView(box,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=10})}}

    private fun showSettings(){
        if(lock.isTimedLocked()){ showLockedPage(); return }
        if(lock.hasPin()) pinDialog(s("pinNeed"),false){if(lock.verify(it))showSettingsPage() else toast(s("wrongPin"))} else showSettingsPage()
    }
    private fun showSettingsPage()=page(s("settings")){
        addRow("🌐 ${s("language")}","বাংলা • العربية • English • اردو • हिन्दी"){languageDialog()}
        addRow("🔐 ${s("pin")}",if(lock.hasPin())s("protected") else s("pinNew")){pinSettingsDialog()}
        addRow("⏱️ ${s("protectionLock")}",if(lock.isTimedLocked()) lockSummary() else s("lockNone")){protectionLockDialog()}
        addRow("🛡️ ${s("deviceOwner")}",if(lock.isDeviceOwner())s("deviceOwnerYes") else s("deviceOwnerNo")){deviceOwnerDialog()}
        addRow("✓ ${s("trusted")}",s("empty")){domainList("trusted_domains",s("trusted"))}
        addRow("＋ ${s("custom")}",s("empty")){domainList("custom_block_domains",s("custom"))}
        addRow("ℹ ${s("about")}",s("aboutText")){aboutDialog()}
    }
    private fun showLockedPage(){
        page(s("protectionLock")){
            addRow("🔒 ${s("lockActive")}","${s("lockUntil")}: ${formatRemaining(lock.remainingMillis())}"){ }
            addRow(s("pin"),s("protected")){ }
            addRow(s("deviceOwner"),if(lock.isDeviceOwner())s("deviceOwnerYes") else s("deviceOwnerNo")){deviceOwnerDialog()}
        }
    }
    private fun lockSummary():String = "${lock.lockDays()} days • ${formatRemaining(lock.remainingMillis())}"
    private fun formatRemaining(ms:Long):String{
        val total=ms/1000; val d=total/86400; val h=(total%86400)/3600; val m=(total%3600)/60
        return "${d}d ${h}h ${m}m"
    }
    private fun protectionLockDialog(){
        if(lock.isTimedLocked()){showLockedPage();return}
        if(!lock.hasPin()) pinDialog(s("pinNew"),false){lock.setPin(it); chooseLockDuration()}
        else pinDialog(s("pinNeed"),false){if(lock.verify(it)) chooseLockDuration() else toast(s("wrongPin"))}
    }
    private fun chooseLockDuration(){
        val days=AdminLock.LOCK_DAYS
        AlertDialog.Builder(this).setTitle(s("lockChoose"))
            .setItems(days.map{ "$it days" }.toTypedArray()){_,which->
                lock.startTimedLock(days[which]); applyDeviceOwnerUninstallPolicy(); toast("${days[which]} days"); showLockedPage()
            }.show()
    }
    private fun deviceOwnerDialog(){
        val text=s("adminInfo")+"\n\n"+(if(lock.isDeviceOwner())s("deviceOwnerYes") else s("deviceOwnerNo"))
        AlertDialog.Builder(this).setTitle(s("deviceOwner")).setMessage(text).setPositiveButton(s("ok"),null).show()
    }
    private fun applyDeviceOwnerUninstallPolicy(){ lock.syncDeviceOwnerUninstallPolicy(lock.isTimedLocked()) }
    private fun page(title:String,body:LinearLayout.()->Unit){root=base();root.addView(header(title));val scroll=ScrollView(this);val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(0,6,0,16);body()};scroll.addView(c);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));root.addView(nav());setContentView(root)}
    private fun LinearLayout.addToggle(title:String,sub:String,checked:Boolean,key:String){val row=LinearLayout(this@MainActivity).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;background=bgDrawable(panel,20f);setPadding(16,8,10,8)};val txt=LinearLayout(this@MainActivity).apply{orientation=LinearLayout.VERTICAL};txt.addView(tv(title,15f));if(sub.isNotEmpty())txt.addView(tv(sub,12f,Color.LTGRAY));row.addView(txt,LinearLayout.LayoutParams(0,70,1f));val sw=Switch(this@MainActivity).apply{isChecked=checked;setOnCheckedChangeListener{_,v->if(lock.isTimedLocked()){isChecked=!v;toast(s("protected"));return@setOnCheckedChangeListener};getSharedPreferences("min_filter",0).edit().putBoolean(key,v).apply();if(protectionOn)restartFilterService()}};row.addView(sw,LinearLayout.LayoutParams(60,60));addView(row,LinearLayout.LayoutParams(-1,80).apply{bottomMargin=10})}
    private fun LinearLayout.addRow(title:String,sub:String,action:()->Unit){val r=LinearLayout(this@MainActivity).apply{orientation=LinearLayout.VERTICAL;background=bgDrawable(panel,20f);setPadding(16,13,16,13);setOnClickListener{action()}};r.addView(tv(title,15f));r.addView(tv(sub,12f,Color.LTGRAY));addView(r,LinearLayout.LayoutParams(-1,82).apply{bottomMargin=10})}
    private fun pref(k:String,d:Boolean)=getSharedPreferences("min_filter",0).getBoolean(k,d)
    private fun restartFilterService(){startService(Intent(this,MinFilterVpnService::class.java).setAction(MinFilterVpnService.ACTION_STOP));window.decorView.postDelayed({startFilterService();toast(s("restart"))},350)}
    private fun languageDialog(){val names=arrayOf("বাংলা","العربية","English","اردو","हिन्दी");val ids=arrayOf(Language.BN,Language.AR,Language.EN,Language.UR,Language.HI);val cur=ids.indexOf(Language.current(this));AlertDialog.Builder(this).setTitle(s("languagePick")).setSingleChoiceItems(names,cur){d,w->Language.set(this,ids[w]);d.dismiss();recreate()}.show()}
    private fun permissionDialog()=aboutDialog()
    private fun domainList(key:String,title:String){if(lock.hasPin()){pinDialog(s("pinNeed"),false){if(lock.verify(it))domainListUnlocked(key,title)else toast(s("wrongPin"))}}else domainListUnlocked(key,title)}
    private fun domainListUnlocked(key:String,title:String){val input=EditText(this).apply{setText(getSharedPreferences("min_filter",0).getString(key,"")?:"");minLines=6;gravity=Gravity.TOP;hint="example.com"};AlertDialog.Builder(this).setTitle(title).setMessage(s("domainHint")).setView(input).setNegativeButton(s("cancel"),null).setPositiveButton(s("save")){_,_->val ds=BlockRules.parseDomainList(input.text.toString());getSharedPreferences("min_filter",0).edit().putString(key,ds.joinToString("\n")).apply();if(protectionOn)restartFilterService()}.show()}
    private fun aboutDialog()=AlertDialog.Builder(this).setTitle("Min Filter").setMessage(s("aboutText")+"\n\n"+s("limText")).setPositiveButton(s("ok"),null).show()
    private fun pinSettingsDialog(){if(!lock.hasPin())pinDialog(s("pinNew"),false){lock.setPin(it);toast(s("pinOn"))}else pinDialog(s("pinNeed"),false){if(!lock.verify(it)){toast(s("wrongPin"));return@pinDialog};AlertDialog.Builder(this).setTitle(s("pin")).setItems(arrayOf(s("changePin"),s("disablePin"))){_,w->if(w==0)pinDialog(s("pinNew"),false){lock.setPin(it)}else{lock.clearPin();toast(s("pinOff"))}}.show()}}
    private fun pinDialog(title:String,unused:Boolean,onVerified:(String)->Unit){val input=EditText(this).apply{inputType=InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD};AlertDialog.Builder(this).setTitle(title).setView(input).setNegativeButton(s("cancel"),null).setPositiveButton(s("ok")){_,_->val p=input.text.toString();if(p.length !in 4..12||!p.all(Char::isDigit)){toast(s("invalidPin"));return@setPositiveButton};onVerified(p)}.show()}
    private fun toast(t:String)=Toast.makeText(this,t,Toast.LENGTH_SHORT).show()
    private fun connect(){
        if(protectionOn){
            if(lock.isTimedLocked()){toast(s("protected"));return}
            if(lock.hasPin()) pinDialog(s("pinNeed"),false){if(lock.verify(it))stopProtection()else toast(s("wrongPin"))} else stopProtection()
            return
        }
        val p=VpnService.prepare(this)
        if(p!=null)startActivityForResult(p,10)else startFilterService()
    }
    private fun stopProtection(){
        if(lock.isTimedLocked()){toast(s("protected"));return}
        startService(Intent(this,MinFilterVpnService::class.java).setAction(MinFilterVpnService.ACTION_STOP));protectionOn=false;showHome()
    }
    private fun startFilterService(){protectionOn=true;val i=Intent(this,MinFilterVpnService::class.java);if(Build.VERSION.SDK_INT>=26)startForegroundService(i)else startService(i);showHome()}
    override fun onResume(){
        super.onResume()
        requestNotificationPermissionIfNeeded()
        lock.syncDeviceOwnerUninstallPolicy()
        protectionOn = MinFilterVpnService.isRunning()
        if (::root.isInitialized && !isFinishing) showHome()
    }

    private fun requestNotificationPermissionIfNeeded(){
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 44)
        }
    }

    override fun onActivityResult(r:Int,c:Int,d:Intent?){super.onActivityResult(r,c,d);if(r==10&&c==RESULT_OK)startFilterService()}
}
