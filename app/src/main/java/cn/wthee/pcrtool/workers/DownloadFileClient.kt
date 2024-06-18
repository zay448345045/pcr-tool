package cn.wthee.pcrtool.workers

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header

/**
 * 文件下载
 */
object DownloadFileClient {

    // 配置 HttpClient
    var client = HttpClient(Android) {

        // 超时设置
        install(HttpTimeout) {
            requestTimeoutMillis = 30 * 1000L
            connectTimeoutMillis = 5 * 1000L
            socketTimeoutMillis = 5 * 1000L
        }

        install(DefaultRequest) {
            // 应用版本
            header(Constants.APP_VERSION, BuildConfig.VERSION_NAME)
        }

        // 请求重试配置
        install(HttpRequestRetry) {
            maxRetries = 3
        }
    }
}
