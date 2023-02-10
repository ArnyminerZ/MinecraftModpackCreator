package system

import data.common.Manifest
import data.minecraft.MinecraftVersion
import java.util.concurrent.locks.ReentrantLock
import kotlinx.serialization.json.Json

abstract class ManifestProvider <T: Manifest<*>> {
    companion object {
        val json = Json { ignoreUnknownKeys = true; isLenient = true }
    }

    protected val lock = ReentrantLock()

    abstract suspend fun manifest(minecraftVersion: MinecraftVersion): T
}