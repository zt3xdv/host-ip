package com.zt3xdv.hostip.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vpn_prefs")

class VpnManager(private val context: Context) {
    
    companion object {
        private val AUTO_CONNECT_KEY = booleanPreferencesKey("auto_connect")
        private val AUTO_RECONNECT_KEY = booleanPreferencesKey("auto_reconnect")
    }
    
    val isVpnConnected = mutableStateOf(false)
    val isConnecting = mutableStateOf(false)
    val vpnError = mutableStateOf<String?>(null)
    
    val autoConnectPreference: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_CONNECT_KEY] ?: false
    }
    
    val autoReconnectPreference: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_RECONNECT_KEY] ?: false
    }
    
    suspend fun setAutoConnect(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_CONNECT_KEY] = enabled
        }
    }
    
    suspend fun setAutoReconnect(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_RECONNECT_KEY] = enabled
        }
    }
    
    fun connectVpn(gatewayIp: String) {
        if (isVpnConnected.value || isConnecting.value) return
        
        isConnecting.value = true
        vpnError.value = null
        
        try {
            // Prepare VPN first
            val intent = VpnService.prepare(context)
            if (intent != null) {
                // VPN needs permission, activity should handle this
                isConnecting.value = false
                return
            }
            
            // Start VPN service
            val serviceIntent = Intent(context, Socks5VpnService::class.java).apply {
                action = Socks5VpnService.ACTION_CONNECT
                putExtra(Socks5VpnService.EXTRA_GATEWAY_IP, gatewayIp)
            }
            
            context.startService(serviceIntent)
            isVpnConnected.value = true
            isConnecting.value = false
        } catch (e: Exception) {
            isConnecting.value = false
            vpnError.value = "Error connecting VPN: ${e.message}"
        }
    }
    
    fun disconnectVpn() {
        if (!isVpnConnected.value) return
        
        try {
            val serviceIntent = Intent(context, Socks5VpnService::class.java).apply {
                action = Socks5VpnService.ACTION_DISCONNECT
            }
            context.startService(serviceIntent)
            isVpnConnected.value = false
            vpnError.value = null
        } catch (e: Exception) {
            vpnError.value = "Error disconnecting VPN: ${e.message}"
        }
    }
    
    fun toggleVpn(gatewayIp: String) {
        if (isVpnConnected.value) {
            disconnectVpn()
        } else {
            connectVpn(gatewayIp)
        }
    }
}
