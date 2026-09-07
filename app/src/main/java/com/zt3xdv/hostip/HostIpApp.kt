package com.zt3xdv.hostip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun HostIpApp(vm: MainViewModel) {
  MaterialTheme {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("Device Wi‑Fi IP", fontSize = 18.sp)
      Text(vm.wifiIp.value, fontSize = 16.sp)
      Text("Wi‑Fi Gateway", fontSize = 18.sp)
      Text(vm.gatewayIp.value, fontSize = 16.sp)
      Text("Public IP", fontSize = 18.sp)
      Text(vm.publicIp.value, fontSize = 16.sp)
      Button(onClick = { vm.refresh() }) { Text("Refresh") }
    }
  }
}
