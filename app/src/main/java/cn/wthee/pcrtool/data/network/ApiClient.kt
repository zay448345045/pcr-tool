package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * 接口请求 client
 */
// 配置 HttpClient
@OptIn(ExperimentalSerializationApi::class)
var apiHttpClient = HttpClient(Android) {
    // 请求配置
    defaultRequest {
        // 请求路径
        url(Constants.API_URL)
    }

    // 请求重试配置
    install(HttpRequestRetry) {
        maxRetries = 3
    }

    // 超时设置
    install(HttpTimeout) {
        requestTimeoutMillis = 10 * 1000L
        connectTimeoutMillis = 5 * 1000L
        socketTimeoutMillis = 5 * 1000L
    }

    // 响应 JSON 配置
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                explicitNulls = false
            }
        )
    }

    // 请求配置
    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        //应用版本
        header(Constants.APP_VERSION, BuildConfig.VERSION_NAME)
        accept(ContentType.Application.Json)
    }

}
