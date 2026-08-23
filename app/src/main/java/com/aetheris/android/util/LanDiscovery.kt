package com.aetheris.android.util

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface

/**
 * Discovers Aetheris panel instances on the local network.
 *
 * Uses a simple UDP broadcast probe on port 9090.
 * Each Aetheris panel responds with its name and API base URL.
 */
object LanDiscovery {

    data class DiscoveredServer(
        val name: String,
        val ip: String,
        val port: Int = 80,
        val url: String,
        val version: String = ""
    )

    private const val DISCOVERY_PORT = 9090
    private const val DISCOVERY_MAGIC = "AETHERIS_DISCOVER"
    private const val DISCOVERY_RESPONSE_MAGIC = "AETHERIS_HERE"
    private const val TIMEOUT_MS = 3000

    /**
     * Broadcast a discovery probe on the LAN and collect responses.
     */
    suspend fun discover(): List<DiscoveredServer> = withContext(Dispatchers.IO) {
        val results = mutableListOf<DiscoveredServer>()

        try {
            // Multicast or broadcast address
            val broadcastAddress = getBroadcastAddress() ?: InetAddress.getByName("255.255.255.255")
            val socket = DatagramSocket()
            socket.broadcast = true
            socket.soTimeout = TIMEOUT_MS

            // Send probe
            val probeData = DISCOVERY_MAGIC.toByteArray()
            val probePacket = DatagramPacket(
                probeData, probeData.size, broadcastAddress, DISCOVERY_PORT
            )
            socket.send(probePacket)

            // Collect responses
            val buffer = ByteArray(1024)
            val deadline = System.currentTimeMillis() + TIMEOUT_MS

            while (System.currentTimeMillis() < deadline) {
                try {
                    val responsePacket = DatagramPacket(buffer, buffer.size)
                    socket.receive(responsePacket)

                    val response = String(responsePacket.data, 0, responsePacket.length)
                    if (response.startsWith(DISCOVERY_RESPONSE_MAGIC)) {
                        // Format: AETHERIS_HERE|name|port|version
                        val parts = response.split("|")
                        if (parts.size >= 3) {
                            val senderIp = responsePacket.address.hostAddress ?: continue
                            val name = parts.getOrElse(1) { "Aetheris Panel" }
                            val port = parts.getOrElse(2) { "80" }.toIntOrNull() ?: 80
                            val version = parts.getOrElse(3) { "" }

                            results.add(
                                DiscoveredServer(
                                    name = name,
                                    ip = senderIp,
                                    port = port,
                                    url = "http://$senderIp:$port",
                                    version = version
                                )
                            )
                        }
                    }
                } catch (_: java.net.SocketTimeoutException) {
                    break
                }
            }

            socket.close()
        } catch (_: Exception) {
            // Discovery failed, return empty
        }

        results.distinctBy { it.ip }
    }

    /**
     * Get the broadcast address for the current WiFi network.
     */
    private fun getBroadcastAddress(): InetAddress? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue

                for (interfaceAddress in networkInterface.interfaceAddresses) {
                    val broadcast = interfaceAddress.broadcast
                    if (broadcast != null) return broadcast
                }
            }
        } catch (_: Exception) {}

        // Fallback: try to compute from subnet
        return try {
            val wifiManager = null // Would need context; this is a fallback
            InetAddress.getByName("255.255.255.255")
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Probe a specific IP to check if an Aetheris panel is running.
     */
    suspend fun probe(ip: String, port: Int = 80): DiscoveredServer? = withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket()
            socket.soTimeout = 1500

            val probeData = DISCOVERY_MAGIC.toByteArray()
            val packet = DatagramPacket(
                probeData, probeData.size, InetAddress.getByName(ip), DISCOVERY_PORT
            )
            socket.send(packet)

            val buffer = ByteArray(1024)
            val responsePacket = DatagramPacket(buffer, buffer.size)
            socket.receive(responsePacket)
            socket.close()

            val response = String(responsePacket.data, 0, responsePacket.length)
            if (response.startsWith(DISCOVERY_RESPONSE_MAGIC)) {
                val parts = response.split("|")
                DiscoveredServer(
                    name = parts.getOrElse(1) { "Aetheris Panel" },
                    ip = ip,
                    port = port,
                    url = "http://$ip:$port",
                    version = parts.getOrElse(3) { "" }
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
