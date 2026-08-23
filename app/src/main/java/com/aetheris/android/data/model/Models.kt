package com.aetheris.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val id: String,
    val name: String,
    val location: String,
    val status: NodeStatus,
    val cpu: Int = 0,
    val memory: Int = 0,
    val disk: Int = 0,
    val servers: Int = 0,
    val maxServers: Int = 0,
    val hypervisor: String = ""
)

@Serializable
enum class NodeStatus {
    ONLINE,
    OFFLINE,
    MAINTENANCE
}

@Serializable
data class Alert(
    val id: String,
    val type: AlertType,
    val severity: AlertSeverity,
    val title: String,
    val message: String,
    val timestamp: String = "",
    val acknowledged: Boolean = false,
    val serverId: String? = null
)

@Serializable
enum class AlertType {
    SERVER_DOWN,
    HIGH_CPU,
    HIGH_MEMORY,
    DISK_FULL,
    PAYMENT_FAILED,
    INVOICE_OVERDUE,
    NODE_OFFLINE,
    BACKUP_FAILED
}

@Serializable
enum class AlertSeverity {
    CRITICAL,
    WARNING,
    INFO
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String = "client"
)

@Serializable
data class DashboardStats(
    val activeServers: Int = 0,
    val totalNodes: Int = 0,
    val monthlyRevenue: Double = 0.0,
    val totalClients: Int = 0,
    val outstanding: Double = 0.0,
    val uptime: String = "99.97%"
)

@Serializable
data class Activity(
    val id: String,
    val type: String,
    val message: String,
    val timestamp: String = "",
    val serverId: String? = null
)

@Serializable
data class ApiError(
    val code: Int,
    val message: String
)
