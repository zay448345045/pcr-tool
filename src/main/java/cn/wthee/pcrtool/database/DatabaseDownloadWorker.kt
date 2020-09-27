package cn.wthee.pcrtool.database

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.service.DatabaseService
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class DatabaseDownloadWorker(
    @NonNull context: Context,
    @NonNull parameters: WorkerParameters?
) : CoroutineWorker(context, parameters!!) {

    //通知栏
    private val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "1"
    private val noticeId = 1
    private lateinit var notification: NotificationCompat.Builder

    //适配低版本数据库路径
    private val folderPath = FileUtil.getDatabaseDir()

    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_VERSION = "KEY_VERSION"
        const val KEY_VERSION_TYPE = "KEY_VERSION_TYPE"
        const val KEY_FROM_SETTING = "KEY_FROM_SETTING"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        //下载地址
        val inputUrl = inputData.getString(KEY_INPUT_URL) ?: return@coroutineScope Result.failure()
        //版本号
        val version = inputData.getString(KEY_VERSION) ?: return@coroutineScope Result.failure()
        val type = inputData.getInt(KEY_VERSION_TYPE, 1)
        val fromSetting = inputData.getInt(KEY_FROM_SETTING, -1)
        setForeground(createForegroundInfo())
        return@coroutineScope download(inputUrl, version, type, fromSetting)
    }


    private fun download(
        inputUrl: String,
        version: String,
        type: Int,
        fromSetting: Int = -1
    ): Result {
        try {
            //创建Retrofit服务
            val service = ApiHelper.createWithClient(
                DatabaseService::class.java, inputUrl,
                ApiHelper.downloadClientBuild(object : DownloadListener {
                    //下载进度
                    override fun onProgress(progress: Int, currSize: Long, totalSize: Long) {
                        //更新下载进度
                        notification.setProgress(100, progress, false)
                            .setContentTitle(
                                "${Constants.NOTICE_TITLE} $currSize  / $totalSize"
                            )
                        notificationManager.notify(noticeId, notification.build())
                    }

                    override fun onFinish() {
                        //下载完成
                        notification.setProgress(100, 100, false)
                            .setContentTitle("${Constants.NOTICE_TOAST_SUCCESS} ")
                        notificationManager.notify(noticeId, notification.build())
                    }
                })
            )

            //下载文件
            val response =
                service.getDb(if (type == 1) Constants.DATABASE_DOWNLOAD_File_Name else Constants.DATABASE_DOWNLOAD_File_Name_JP)
                    .execute()

            //保存
            //创建数据库文件夹
            val file = File(folderPath)
            if (!file.exists()) {
                file.mkdir()
            }
            //删除已有数据库文件
            //br压缩包路径
            val dbZipPath = FileUtil.getDatabaseZipPath(type)
            val db = File(dbZipPath)
            if (db.exists()) {
                FileUtil.deleteDir(folderPath, dbZipPath)
            }
            //写入文件
            val input = response.body()!!.byteStream()
            input.let { inputStream ->
                val out = FileOutputStream(db)
                val byte = ByteArray(1024 * 4)
                var line: Int
                while (inputStream.read(byte).also { line = it } > 0) {
                    out.write(byte, 0, line)
                }
                out.flush()
                out.close()
                inputStream.close()
                MainScope().launch {
                    //更新数据库
                    AppDatabase.getInstance().close()
                    AppDatabaseJP.getInstance().close()
                    UnzippedUtil.deCompress(db, true)
                    //更新数据库版本号
                    sp.edit {
                        putString(
                            if (type == 1)
                                Constants.SP_DATABASE_VERSION
                            else
                                Constants.SP_DATABASE_VERSION_JP,
                            version
                        )
                    }
                    //通知更新数据
                    if (fromSetting == 1) {
                        CharacterListFragment.handler.sendEmptyMessage(2)
                    } else {
                        ToastUtil.short(Constants.NOTICE_TOAST_SUCCESS)
                    }
                    CharacterListFragment.handler.sendEmptyMessage(1)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            MainScope().launch {
                ToastUtil.short(Constants.NOTICE_TOAST_NO_FILE)
            }
            return Result.failure()
        }
    }

    //前台通知
    private fun createForegroundInfo(): ForegroundInfo {
        val context: Context = applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(Constants.NOTICE_TITLE)
            .setTicker(Constants.NOTICE_TITLE)
            .setSmallIcon(R.drawable.ic_logo)
            .setOngoing(true)
            .setProgress(100, 0, true)
        return ForegroundInfo(noticeId, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelId, "数据更新", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }


}