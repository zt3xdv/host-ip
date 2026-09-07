package com.zt3xdv.hostip

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application) {
  val wifiIp = mutableStateOf("...")
  val gatewayIp = mutableStateOf("...")
  val publicIp = mutableStateOf("...")
  init { refresh() }
  fun refresh() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val ctx = getApplication<Application>().applicationContext
        val wm = ctx.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipInt = wm.connectionInfo?.ipAddress ?: 0
        val dhcp = wm.dhcpInfo?.gateway ?: 0
        wifiIp.value = formatIp(ipInt)
        gatewayIp.value = formatIp(dhcp)
      } catch (e: Exception) {
        wifiIp.value = "unknown"
        gatewayIp.value = "unknown"
      }
      try {
        val ip = URL("https://api.ipify.org").readText().trim()
        publicIp.value = if (ip.isEmpty()) "unknown" else ip
      } catch (e: Exception) {
        publicIp.value = "unknown"
      }
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
}
