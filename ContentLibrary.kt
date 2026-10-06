package com.minfilter.app.ui

data class SafeContent(val title:String,val body:String,val reference:String)
object ContentLibrary {
    fun items(lang:String): List<SafeContent> = when(lang) {
        Language.AR -> listOf(
            SafeContent("وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ","نجاحي لا يكون إلا بعون الله.","القرآن 11:88 — سورة هود"),
            SafeContent("خيركم من تعلم القرآن وعلمه","خير الناس من يتعلم القرآن ويعلّمه.","صحيح البخاري 5027"),
            SafeContent("الزواج والأسرة","استعد للحياة الأسرية الحلال بالعلم وحسن الخلق وتحمل المسؤولية.","Min Filter — محتوى نافع"),
            SafeContent("عادات نافعة","احفظ وقتك وبصرك، واجعل استخدام التقنية وسيلة للخير.","Min Filter — إرشادات الاستخدام الآمن")
        )
        Language.UR -> listOf(
            SafeContent("وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ","میری کامیابی صرف اللہ کی مدد سے ہے۔","قرآن 11:88 — سورۃ ہود"),
            SafeContent("خيركم من تعلم القرآن وعلمه","تم میں بہترین وہ ہے جو قرآن سیکھے اور سکھائے۔","صحیح بخاری 5027"),
            SafeContent("نکاح اور خاندان","حلال اور ذمہ دار خاندانی زندگی کے لیے علم، کردار اور ذمہ داری پیدا کریں۔","Min Filter — مثبت مواد"),
            SafeContent("اچھی عادات","اپنے وقت اور نگاہ کی حفاظت کریں اور ٹیکنالوجی کو خیر کے لیے استعمال کریں۔","Min Filter — محفوظ انٹرنیٹ رہنمائی")
        )
        Language.HI -> listOf(
            SafeContent("وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ","मेरी सफलता केवल अल्लाह की सहायता से है।","कुरआन 11:88 — सूरह हूद"),
            SafeContent("خيركم من تعلم القرآن وعلمه","तुममें सबसे उत्तम वह है जो कुरआन सीखता और सिखाता है।","सहीह अल-बुखारी 5027"),
            SafeContent("विवाह और परिवार","हलाल और जिम्मेदार पारिवारिक जीवन के लिए ज्ञान, चरित्र और जिम्मेदारी विकसित करें।","Min Filter — सकारात्मक सामग्री"),
            SafeContent("अच्छी आदतें","समय और दृष्टि की रक्षा करें और तकनीक को अच्छे काम में लगाएं।","Min Filter — सुरक्षित इंटरनेट मार्गदर्शन")
        )
        Language.EN -> listOf(
            SafeContent("وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ","My success is only through Allah's help.","Qur'an 11:88 — Surah Hud"),
            SafeContent("خيركم من تعلم القرآن وعلمه","The best among you are those who learn the Qur'an and teach it.","Sahih al-Bukhari 5027"),
            SafeContent("Marriage & family","Prepare for a lawful and responsible family life through knowledge, character, and responsibility.","Min Filter — positive content"),
            SafeContent("Good habits","Protect your time and attention, and use technology for beneficial purposes.","Min Filter — safe internet guidance")
        )
        else -> listOf(
            SafeContent("وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ","আমার সফলতা তো কেবল আল্লাহর সাহায্যেই।","কুরআন ১১:৮৮ — সূরা হূদ"),
            SafeContent("خيركم من تعلم القرآن وعلمه","তোমাদের মধ্যে উত্তম সে, যে কুরআন শেখে এবং শেখায়।","সহিহ আল-বুখারি, ৫০২৭"),
            SafeContent("বিবাহ ও পরিবার","হালাল ও দায়িত্বশীল পারিবারিক জীবনের জন্য জ্ঞান, চরিত্র ও দায়িত্ববোধ গড়ে তুলুন।","Min Filter — ইতিবাচক জীবনধারা বিভাগ"),
            SafeContent("ভালো অভ্যাস","চোখ, সময় ও মনকে ভালো কাজে ব্যস্ত রাখা নিরাপদ ও সুন্দর ডিজিটাল জীবনের একটি অংশ।","Min Filter — নিরাপদ ইন্টারনেট নির্দেশনা")
        )
    }
}
