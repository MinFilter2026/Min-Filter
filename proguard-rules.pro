# libmihomo is loaded through its public Java bridge API.
# Keep the bridge entry points available to the Android shrinker.
-keep class io.github.oviron.libmihomo.** { *; }
