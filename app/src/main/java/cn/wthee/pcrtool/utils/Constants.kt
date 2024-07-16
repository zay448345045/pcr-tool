package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication.Companion.URL_DOMAIN
import cn.wthee.pcrtool.R

/**
 * 常量
 */
object Constants {

    //tp属性变化等级，260级后，tp回复将转化为攻击力
    const val TP_LIMIT_LEVEL = 260

    //属性变化等级，300级后，回避等属性转换为其他属性
    const val OTHER_LIMIT_LEVEL = 300

    //本地数据库版本，需强制更新数据库文件时，需更改该版本号
    const val SQLITE_VERSION = 383

    //图片缓存
    const val COIL_DIR = "coil_image_cache"

    //视频缓存
    const val VIDEO_DIR = "video_cache"

    const val SERVER_DOMAIN = "wthee.xyz"
    const val SERVER_IP = "96.45.190.76"

    //数据库资源地址
    var DATABASE_URL = "https://$URL_DOMAIN/db/"

    //接口正式地址
    var API_URL = "https://$URL_DOMAIN/pcr/api/v1/"

    //Spine 预览地址
    var PREVIEW_URL = "https://$URL_DOMAIN/spine/index.html"
    var PREVIEW_UNIT_URL = "$PREVIEW_URL?unitId="
    var PREVIEW_ENEMY_URL = "$PREVIEW_URL?enemyId="

    //数据库shm
    const val DATABASE_SHM = "-shm"

    //数据库wal
    const val DATABASE_WAL = "-wal"

    //数据库br
    const val DATABASE_BR = ".br"

    //国服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_CN = "redive_cn.db.br"
    const val DATABASE_NAME_CN = "redive_cn.db"

    //台服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_TW = "redive_tw.db.br"
    const val DATABASE_NAME_TW = "redive_tw.db"

    //日服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_JP = "redive_jp.db.br"
    const val DATABASE_NAME_JP = "redive_jp.db"

    //安装包名称
    const val DOWNLOAD_APK_NAME = "pcrtool.apk"

    //进度
    const val KEY_PROGRESS = "progress"

    //其它数据库
    const val DATABASE_NEWS = "news.db"
    const val DATABASE_TWEET = "tweet.db"
    const val DATABASE_COMIC = "comic.db"
    const val DATABASE_PVP = "pvp.db"
    const val DATABASE_MOCK_GACHA = "mock_gacha.db"

    const val RANK_UPPER = "RANK"

    val ATTR = arrayListOf(
        R.string.attr_hp,
        R.string.attr_life_steal,
        R.string.attr_atk,
        R.string.attr_magic_str,
        R.string.attr_def,
        R.string.attr_magic_def,
        R.string.attr_physical_critical,
        R.string.attr_magic_critical,
        R.string.attr_physical_penetrate,
        R.string.attr_magic_penetrate,
        R.string.attr_accuracy,
        R.string.attr_dodge,
        R.string.attr_wave_hp_recovery,
        R.string.attr_hp_recovery_rate,
        R.string.attr_wave_energy_recovery,
        R.string.attr_energy_recovery_rate,
        R.string.attr_energy_reduce_rate,
    )

    const val UNKNOWN = "?"


    // 异常
    const val EXCEPTION_API = "api exception: "
    const val EXCEPTION_DOWNLOAD_DB = "db download exception: "
    const val EXCEPTION_DOWNLOAD_WORK_DB = "db download work exception: "
    const val EXCEPTION_DOWNLOAD_FILE = "file download exception: "
    const val EXCEPTION_SAVE_DB = "db file save exception: "
    const val EXCEPTION_FILE_SAVE = "file save exception: "
    const val EXCEPTION_LOAD_ATTR = "character attr exception: "
    const val EXCEPTION_UNIT_NULL = "character info exception: "
    const val EXCEPTION_SKILL = "skill exception: "
    const val EXCEPTION_PVP_SERVICE = "pvp search exception: "
    const val EXCEPTION_DATA_CHANGE = "db change exception: "

    //任务
    const val DOWNLOAD_DB_WORK = "updateDatabase"
    const val DOWNLOAD_APK_WORK = "updateApk"
    const val DOWNLOAD_FILE_WORK = "downloadFile"

    //应用版本
    const val APP_VERSION = "app-version"
}