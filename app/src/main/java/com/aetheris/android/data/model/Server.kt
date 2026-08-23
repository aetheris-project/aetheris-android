package com.aetheris.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Server(
    val id: String,
    val name: String,
    val node: String,
    val status: ServerStatus,
    val ip: String,
    val port: Int = 25565,
    val cpu: ResourceUsage = ResourceUsage(),
    val memory: ResourceUsage = ResourceUsage(),
    val disk: ResourceUsage = ResourceUsage(),
    val network: NetworkUsage = NetworkUsage(),
    val game: String = "",
    val players: PlayerCount = PlayerCount(),
    val createdAt: String = "",
    val owner: String = ""
)

@Serializable
data class ResourceUsage(
    val current: Int = 0,
    val max: Int = 0,
    val percentage: Float = 0f
)

@Serializable
data class NetworkUsage(
    val upload: Long = 0L,
    val download: Long = 0L
)

@Serializable
data class PlayerCount(
    val online: Int = 0,
    val max: Int = 0
)

@Serializable
enum class ServerStatus {
    RUNNING,
    STOPPED,
    STARTING,
    STOPPING,
    ERROR,
    SUSPENDED
}
