package com.zt3xdv.hostip

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val wifiIp = mutableStateOf("Loading...")
    val gatewayIp = mutableStateOf("Loading...")
    val publicIp = mutableStateOf("Loading...")
    val errorMessage = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false)

    init {
        refresh()
    }
    
    fun clearError() {
        errorMessage.value = null
    }
    
    fun setError(message: String) {
        errorMessage.value = message
    }

    fun refresh() {
        if (isLoading.value) return

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val localResult = withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()
                        .applicationContext

                    val wifiManager = context.getSystemService(
                        Context.WIFI_SERVICE
                    ) as? WifiManager
                        ?: throw Exception("Could not get WifiManager")

                    val wifiInfo = wifiManager.connectionInfo
                        ?: throw Exception("Could not get Wifi info")

                    val dhcpInfo = wifiManager.dhcpInfo

                    val ip = wifiInfo.ipAddress
                    val gateway = dhcpInfo?.gateway ?: 0

                    LocalNetworkResult(
                        wifiIp = if (ip != 0) {
                            formatIp(ip)
                        } else {
                            "No connected"
                        },
                        gatewayIp = if (gateway != 0) {
                            formatIp(gateway)
                        } else {
                            "No available"
                        },
                        error = null
                    )
                } catch (exception: Exception) {
                    LocalNetworkResult(
                        wifiIp = "Error",
                        gatewayIp = "Error",
                        error = "Error getting Wifi data: ${
                            exception.message ?: exception.javaClass.simpleName
                        }"
                    )
                }
            }

            wifiIp.value = localResult.wifiIp
            gatewayIp.value = localResult.gatewayIp

            localResult.error?.let { error ->
                errorMessage.value = error
            }

            val publicIpResult = withContext(Dispatchers.IO) {
                try {
                    val result = URL("https://api.ipify.org")
                        .readText()
                        .trim()

                    if (result.isEmpty()) {
                        throw Exception("La respuesta estaba vacía")
                    }

                    result
                } catch (exception: Exception) {
                    "Error"
                }
            }

            publicIp.value = publicIpResult

            if (publicIpResult == "Error") {
                val publicError = "Error getting public ip. " +
                        "Check your internet connection."

                errorMessage.value = listOfNotNull(
                    errorMessage.value,
                    publicError
                ).joinToString("\n")
            }

            isLoading.value = false
        }
    }

    private fun formatIp(ip: Int): String {
        return listOf(
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        ).joinToString(".")
    }

    private data class LocalNetworkResult(
        val wifiIp: String,
        val gatewayIp: String,
        val error: String?
    )
}
