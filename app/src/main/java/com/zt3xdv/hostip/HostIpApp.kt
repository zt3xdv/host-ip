package com.zt3xdv.hostip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HostIpApp(
    viewModel: MainViewModel,
    onRequestPermissions: () -> Unit
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Internet information",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            IpRow("IP Wifi", viewModel.wifiIp.value)

            Spacer(modifier = Modifier.height(12.dp))

            IpRow("Gateway Wifi", viewModel.gatewayIp.value)

            Spacer(modifier = Modifier.height(12.dp))

            IpRow("Public IP", viewModel.publicIp.value)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.refresh()
                },
                enabled = !viewModel.isLoading.value
            ) {
                Text(
                    text = if (viewModel.isLoading.value) {
                        "Loading..."
                    } else {
                        "Refresh"
                    }
                )
            }

            viewModel.errorMessage.value?.let { error ->
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFFFE0E0)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFB00020),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onRequestPermissions
                        ) {
                            Text("Request Permisions")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IpRow(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 18.sp
        )

        Text(
            text = value,
            fontSize = 16.sp
        )
    }
}
