package com.zt3xdv.hostip.vpn

import android.content.Intent
import android.net.VpnService
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.system.OsConstants
import kotlinx.coroutines.*
import java.io.IOException
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.Socket

class Socks5VpnService : VpnService() {
    
    companion object {
        private const val TAG = "Socks5VpnService"
        const val ACTION_CONNECT = "com.zt3xdv.hostip.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.zt3xdv.hostip.vpn.DISCONNECT"
        const val EXTRA_GATEWAY_IP = "gateway_ip"
        const val SOCKS_PORT = 1080
    }
    
    private var vpnThread: Thread? = null
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var scope: CoroutineScope? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_CONNECT -> {
                val gatewayIp = intent.getStringExtra(EXTRA_GATEWAY_IP) ?: ""
                if (gatewayIp.isNotEmpty()) {
                    startVpnConnection(gatewayIp)
                    START_STICKY
                } else {
                    stopSelf()
                    START_NOT_STICKY
                }
            }
            ACTION_DISCONNECT -> {
                stopVpnConnection()
                stopSelf()
                START_NOT_STICKY
            }
            else -> {
                stopSelf()
                START_NOT_STICKY
            }
        }
    }
    
    private fun startVpnConnection(gatewayIp: String) {
        if (isRunning) return
        
        isRunning = true
        scope = CoroutineScope(Dispatchers.Default + Job())
        
        vpnThread = Thread {
            try {
                setupVpn(gatewayIp)
            } catch (e: Exception) {
                Log.e(TAG, "Error in VPN thread", e)
                isRunning = false
            }
        }.apply {
            start()
        }
    }
    
    private fun setupVpn(gatewayIp: String) {
        try {
            val builder = Builder()
            builder.setSession("Socks5 VPN")
            builder.addAddress("10.0.0.2", 24)
            builder.addDnsServer("8.8.8.8")
            builder.addDnsServer("8.8.4.4")
            builder.addRoute("0.0.0.0", 0)
            builder.setMtu(1500)
            builder.allowFamily(OsConstants.AF_INET)
            builder.allowFamily(OsConstants.AF_INET6)
            
            val pfd = builder.establish() ?: return
            
            // Maintain VPN connection
            while (isRunning) {
                Thread.sleep(1000)
            }
            
            pfd.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up VPN", e)
            isRunning = false
        }
    }
    
    private fun stopVpnConnection() {
        isRunning = false
        vpnThread?.join(3000)
        vpnThread = null
        scope?.cancel()
        scope = null
    }
    
    override fun onDestroy() {
        stopVpnConnection()
        super.onDestroy()
    }
}
