package com.zt3xdv.hostip

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HostIpApp(
    viewModel: MainViewModel,
    onRequestPermissions: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            HeaderSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // WiFi Info Card
            if (viewModel.wifiConnected.value) {
                WifiInfoCard(viewModel)
            } else {
                NotConnectedCard()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // VPN Control Card
            VpnControlCard(viewModel, onRequestPermissions)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Settings Card
            SettingsCard(viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Public IP Card
            PublicIpCard(viewModel)
            
            // Error Card
            viewModel.errorMessage.value?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                ErrorCard(error, onRequestPermissions)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "VPN Shield",
            modifier = Modifier.size(48.dp),
            tint = Color(0xFF3F51B5)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "SOCKS5 VPN",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        
        Text(
            text = "Gateway Proxy Connection",
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun NotConnectedCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFFFFEEEE),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Not connected",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFB00020)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Not connected to WiFi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB00020)
            )
        }
    }
}

@Composable
private fun WifiInfoCard(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "WiFi Network Info",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InfoRow("Local IP", viewModel.wifiIp.value)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow("Gateway IP", viewModel.gatewayIp.value)
        }
    }
}

@Composable
private fun VpnControlCard(viewModel: MainViewModel, onRequestPermissions: () -> Unit) {
    val vpnConnected = viewModel.vpnManager.isVpnConnected.value
    val isConnecting = viewModel.vpnManager.isConnecting.value
    val buttonColor by animateColorAsState(
        targetValue = if (vpnConnected) Color(0xFF4CAF50) else Color(0xFF3F51B5)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "VPN Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = if (vpnConnected) "Connected" else "Disconnected",
                        fontSize = 12.sp,
                        color = if (vpnConnected) Color(0xFF4CAF50) else Color(0xFF999999)
                    )
                }
                
                Icon(
                    imageVector = if (vpnConnected) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = "VPN Status",
                    modifier = Modifier.size(24.dp),
                    tint = if (vpnConnected) Color(0xFF4CAF50) else Color(0xFF999999)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.toggleVpn() },
                enabled = !isConnecting && viewModel.wifiConnected.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = buttonColor,
                    disabledBackgroundColor = Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when {
                        isConnecting -> "Connecting..."
                        vpnConnected -> "Disconnect VPN"
                        else -> "Connect VPN"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "VPN Settings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingToggle(
                title = "Auto Connect on WiFi",
                description = "Automatically connect when WiFi is available",
                checked = viewModel.autoConnect.value,
                onCheckedChange = { viewModel.setAutoConnect(it) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingToggle(
                title = "Auto Reconnect",
                description = "Reconnect if VPN connection drops",
                checked = viewModel.autoReconnect.value,
                onCheckedChange = { viewModel.setAutoReconnect(it) }
            )
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF999999)
            )
        }
        
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PublicIpCard(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Public IP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Text(
                        text = viewModel.publicIp.value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                }
                
                IconButton(
                    onClick = { viewModel.refresh() },
                    enabled = !viewModel.isLoading.value,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = if (viewModel.isLoading.value) Color(0xFFBDBDBD) else Color(0xFF3F51B5)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun ErrorCard(error: String, onRequestPermissions: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFFFFE0E0),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = error,
                color = Color(0xFFB00020),
                fontSize = 12.sp
            )
            
            if ("permission" in error.lowercase()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onRequestPermissions,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFB00020)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Request Permissions", color = Color.White)
                }
            }
        }
    }
}
