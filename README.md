# SOCKS5 VPN Gateway Proxy

A modern Android application that connects to a SOCKS5 proxy server running on your WiFi gateway (port 180) and provides VPN functionality.

## Features

- **Automatic Gateway Detection**: Detects your WiFi gateway IP automatically
- **SOCKS5 VPN Proxy**: Connects to a SOCKS5 server running on your gateway's port 180
- **Modern UI**: Clean and intuitive Material Design interface
- **Manual Control**: Choose when to connect - no auto-connect by default
- **Auto-Connect Option**: Enable automatic connection when WiFi is available
- **Auto-Reconnect**: Option to automatically reconnect if VPN drops
- **VPN Status Monitoring**: Real-time status display and connection indicators
- **IP Information**: Shows local IP, gateway IP, and public IP address
- **Network Refresh**: Quick refresh button to update network information

## Requirements

- Android 5.0+ (API level 24+)
- WiFi connection with a SOCKS5 server on gateway:180
- Location permission to query WiFi network info

## Installation

1. Clone the repository
2. Open in Android Studio
3. Build and install on your device

## Usage

1. **Connect to WiFi**: The app will automatically detect your gateway IP
2. **Toggle VPN**: Use the main toggle button to connect/disconnect
3. **Configure Settings**: 
   - Enable "Auto Connect on WiFi" for automatic connection when joining networks
   - Enable "Auto Reconnect" to handle dropped connections automatically
4. **Monitor Status**: View real-time VPN connection status and IP information

## Permissions

The app requires the following permissions:
- `INTERNET`: For network connectivity
- `ACCESS_WIFI_STATE`: To get WiFi gateway information
- `ACCESS_NETWORK_STATE`: To monitor network state
- `ACCESS_FINE_LOCATION`: To query WiFi details (required for DHCP info)
- `BIND_VPN_SERVICE`: For VPN service functionality
- `CHANGE_NETWORK_STATE`: For network routing

## Gateway Setup

Ensure your WiFi gateway has a SOCKS5 proxy server running on port 180:

Example with Dante SOCKS server:
```
# Listen on port 180
internal: 192.168.1.1 port = 180
external: 192.168.1.1
method: none
logoutput: syslog
route {
    from: 0.0.0.0/0 to: 0.0.0.0/0
    via: 192.168.1.1 port = 180
}
```

## Architecture

- **Socks5VpnService**: Android VPN service handling the actual VPN connection
- **VpnManager**: Manages VPN state and connection logic
- **MainViewModel**: Handles WiFi detection and UI state
- **HostIpApp**: Modern Jetpack Compose UI with Material Design 3

## License

MIT License - see LICENSE file
