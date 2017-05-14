package models

import java.util.*

class SoundAllocator {

    val allSounds = mutableListOf("fart", "rooster", "ping", "bicycle_bell", "boom_x", "burp2_x",
                                    "cuckoo_clogstck2_x", "doorbell2", "honk2_x", "aoogah")

    fun allocateSound(): String {
        if(allSounds.isEmpty()) return "ping"
        synchronized(this) {
            val randomIndex = Random().nextInt(allSounds.size - 1)
            val sound = allSounds[randomIndex]
            allSounds.removeAt(randomIndex)
            return sound
        }
    }
}