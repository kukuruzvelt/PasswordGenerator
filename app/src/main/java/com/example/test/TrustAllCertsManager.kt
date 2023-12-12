package com.example.test

import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class TrustAllCertsManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // Do nothing (accept all certificates)
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        // Do nothing (accept all certificates)
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}

object MyHttpClient {
    fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCertsManager = TrustAllCertsManager()

            // Create an SSL context with the trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustAllCertsManager), java.security.SecureRandom())

            // Create an OkHttpClient that uses the custom SSL context
            OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCertsManager)
                .hostnameVerifier { _, _ -> true } // Bypass hostname verification
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}