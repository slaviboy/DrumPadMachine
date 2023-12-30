package com.slaviboy.drumpadmachine.data.room.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.api.entities.ConfigApi
import com.slaviboy.drumpadmachine.data.entities.LessonState
import com.slaviboy.drumpadmachine.data.room.category.CategoryDao
import com.slaviboy.drumpadmachine.data.room.category.CategoryEntity
import com.slaviboy.drumpadmachine.data.room.config.ConfigDao
import com.slaviboy.drumpadmachine.data.room.config.ConfigEntity
import com.slaviboy.drumpadmachine.data.room.file.FileDao
import com.slaviboy.drumpadmachine.data.room.file.FileEntity
import com.slaviboy.drumpadmachine.data.room.filter.FilterDao
import com.slaviboy.drumpadmachine.data.room.filter.FilterEntity
import com.slaviboy.drumpadmachine.data.room.lesson.LessonDao
import com.slaviboy.drumpadmachine.data.room.lesson.LessonEntity
import com.slaviboy.drumpadmachine.data.room.pad.PadDao
import com.slaviboy.drumpadmachine.data.room.pad.PadEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetDao
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SaveDatabaseForegroundService : Service() {

    @Inject
    lateinit var configDao: ConfigDao

    @Inject
    lateinit var categoryDao: CategoryDao

    @Inject
    lateinit var presetDao: PresetDao

    @Inject
    lateinit var filterDao: FilterDao

    @Inject
    lateinit var fileDao: FileDao

    @Inject
    lateinit var lessonDao: LessonDao

    @Inject
    lateinit var padDao: PadDao

    @Inject
    lateinit var gson: Gson

    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        if (!isServiceRunning) {
            isServiceRunning = true
            val configVersion = intent?.getIntExtra("CONFIG_VERSION", 12) ?: 12
            doWork(startId, configVersion)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
        isServiceRunning = false
    }

    private fun doWork(startId: Int, configVersion: Int) {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val path = File(applicationContext.cacheDir, "config/presets/v$configVersion/")
                if (path.exists()) {
                    readConfigFile(path)?.let {
                        saveRoomDatabaseEntities(it)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    } else {
                        stopForeground(true)
                    }
                    stopSelfResult(startId)
                }
            } catch (e: Exception) {
                print(e)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_dev_foreground)
            .setContentTitle("Downloading audio presets")
            .setContentText("Running...")
            .setProgress(0, 0, true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun readConfigFile(path: File): ConfigApi? {
        val configFile = File(path, "config.json")
        if (!configFile.exists()) {
            return null
        }
        val bufferedReader = configFile.bufferedReader()
        val configText = bufferedReader.use { it.readText() }
        return gson.fromJson(configText, ConfigApi::class.java)
    }

    private suspend fun saveRoomDatabaseEntities(configApi: ConfigApi) {
        val configEntity = ConfigEntity()
        val categoryEntityList = mutableListOf<CategoryEntity>()
        val filterEntityList = mutableListOf<FilterEntity>()

        configApi.categoriesApi.forEach { categoryApi ->
            val categoryEntity = CategoryEntity(
                configId = configEntity.id,
                title = categoryApi.title
            )
            val filterEntity = FilterEntity(
                categoryId = categoryEntity.id,
                tags = categoryApi.filterApi.tags
            )
            categoryEntityList.add(categoryEntity)
            filterEntityList.add(filterEntity)
        }

        val presetEntityList = mutableListOf<PresetEntity>()
        val fileEntityList = mutableListOf<FileEntity>()
        val lessonEntityList = mutableListOf<LessonEntity>()
        val padEntityList = mutableListOf<PadEntity>()

        configApi.presetsApi.forEach {
            val (_, presetApi) = it
            val presetEntity = PresetEntity(
                configId = configEntity.id,
                presetId = presetApi.id.toLongOrDefault(0L),
                name = presetApi.name,
                author = presetApi.author,
                price = presetApi.price,
                orderBy = presetApi.orderBy,
                timestamp = presetApi.timestamp,
                deleted = presetApi.deleted,
                hasInfo = presetApi.hasInfo,
                tempo = presetApi.tempo,
                tags = presetApi.tags
            )
            presetEntityList.add(presetEntity)

            presetApi.files.forEach {
                val (_, fileApi) = it
                val fileEntity = FileEntity(
                    presetId = presetEntity.id,
                    looped = fileApi.looped,
                    filename = fileApi.filename,
                    choke = fileApi.choke,
                    color = fileApi.color,
                    stopOnRelease = fileApi.stopOnRelease
                )
                fileEntityList.add(fileEntity)
            }

            presetApi.beatSchool?.forEach {
                val (side, lessonApiList) = it
                lessonApiList.forEach {
                    val lessonEntity = LessonEntity(
                        presetId = presetEntity.id,
                        lessonId = it.id,
                        side = side,
                        version = it.version,
                        name = it.name,
                        orderBy = it.orderBy,
                        sequencerSize = it.sequencerSize,
                        rating = it.rating,
                        lastScore = 0,
                        bestScore = 0,
                        lessonState = LessonState.Unlock
                    )
                    lessonEntityList.add(lessonEntity)

                    val pads = it.pads.map {
                        val (padId, padApiArray) = it
                        padApiArray.map {
                            PadEntity(
                                lessonId = lessonEntity.id,
                                padId = padId.toIntOrNull() ?: -1,
                                start = it.start,
                                ambient = it.embient,
                                duration = it.duration
                            )
                        }.filter {
                            it.padId != -1
                        }
                    }.flatten()
                    padEntityList.addAll(pads)
                }
            }
        }

        configDao.upsertConfig(configEntity)
        categoryDao.upsertCategories(categoryEntityList)
        filterDao.upsertFilters(filterEntityList)
        fileDao.upsertFiles(fileEntityList)
        presetDao.upsertPresets(presetEntityList)
        lessonDao.upsertLessons(lessonEntityList)
        padDao.upsertPads(padEntityList)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ForegroundServiceChannel"

        var isServiceRunning: Boolean = false
    }
}