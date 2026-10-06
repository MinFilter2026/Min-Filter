package com.minfilter.app.ui
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
object Language {
 const val BN="bn"; const val EN="en"; const val AR="ar"; const val UR="ur"; const val HI="hi"
 fun current(c:Context)=c.getSharedPreferences("min_filter",0).getString("language",BN)?:BN
 fun set(c:Context,l:String)=c.getSharedPreferences("min_filter",0).edit().putString("language",l).apply()
 fun apply(c:Context){val l=current(c);Locale.setDefault(Locale(l));val x=Configuration(c.resources.configuration);x.setLocale(Locale(l));x.setLayoutDirection(Locale(l));c.resources.updateConfiguration(x,c.resources.displayMetrics)}
}
