adb connect 127.0.0.1:58526
adb shell rm /storage/emulated/0/Android/data/io.anuke.mindustry/files/mods/Disintegration.jar
adb push %1 /storage/emulated/0/Android/data/io.anuke.mindustry/files/mods/
adb shell am start -n io.anuke.mindustry/mindustry.android.AndroidLauncher
