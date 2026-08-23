package com.aetheris.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Invoice(
    val id: String,
    val number: String,
    val status: InvoiceStatus,
    val amount: Double,
    val currency: String = "USD",
    val description: String = "",
    val createdAt: String = "",
    val dueDate: String = "",
    val paidAt: String? = null,
    val items: List<InvoiceItem> = emptyList()
)

@Serializable
data class InvoiceItem(
    val description: String,
    val quantity: Int = 1,
    val unitPrice: Double,
    val total: Double
)

@Serializable
enum class InvoiceStatus {
    DRAFT,
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED,
    REFUNDED
}

@Serializable
data class Service(
    val id: String,
    val name: String,
    val type: ServiceType,
    val status: ServiceStatus,
    val price: Double,
    val billingCycle: String = "monthly",
    val nextBillingDate: String = "",
    val server: String? = null
)

@Serializable
enum class ServiceType {
    GAME_SERVER,
    VPS,
    DEDICATED,
    WEB_HOSTING,
    DOMAIN,
    ADDON
}

@Serializable
enum class ServiceStatus {
    ACTIVE,
    SUSPENDED,
    TERMINATED,
    PENDING
}

@Serializable
data class PaymentMethod(
    val id: String,
    val type: String,
    val last4: String = "",
    val brand: String = "",
    val expiry: String = "",
    val isDefault: Boolean = false
)

@Serializable
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val currency: String = "USD",
    val description: String = "",
    val status: String = "",
    val date: String = ""
)

@Serializable
enum class TransactionType {
    CHARGE,
    PAYMENT,
    REFUND,
    CREDIT
}
