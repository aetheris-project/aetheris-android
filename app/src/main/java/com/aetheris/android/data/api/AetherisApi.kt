package com.aetheris.android.data.api

import com.aetheris.android.data.model.*
import kotlinx.serialization.Serializable
import retrofit2.http.*

interface AetherisApi {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/logout")
    suspend fun logout()

    @GET("api/auth/user")
    suspend fun getCurrentUser(): User

    // Dashboard
    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(): DashboardStats

    @GET("api/dashboard/activity")
    suspend fun getActivity(
        @Query("limit") limit: Int = 20
    ): List<Activity>

    // Servers
    @GET("api/servers")
    suspend fun getServers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): List<Server>

    @GET("api/servers/{id}")
    suspend fun getServer(@Path("id") id: String): Server

    @POST("api/servers/{id}/start")
    suspend fun startServer(@Path("id") id: String)

    @POST("api/servers/{id}/stop")
    suspend fun stopServer(@Path("id") id: String)

    @POST("api/servers/{id}/restart")
    suspend fun restartServer(@Path("id") id: String)

    @POST("api/servers/{id}/suspend")
    suspend fun suspendServer(@Path("id") id: String)

    @GET("api/servers/{id}/console")
    suspend fun getConsoleToken(@Path("id") id: String): ConsoleToken

    // Nodes
    @GET("api/nodes")
    suspend fun getNodes(): List<Node>

    @GET("api/nodes/{id}")
    suspend fun getNode(@Path("id") id: String): Node

    // Billing
    @GET("api/billing/invoices")
    suspend fun getInvoices(
        @Query("page") page: Int = 1
    ): List<Invoice>

    @GET("api/billing/invoices/{id}")
    suspend fun getInvoice(@Path("id") id: String): Invoice

    @GET("api/billing/services")
    suspend fun getServices(): List<Service>

    @GET("api/billing/payment-methods")
    suspend fun getPaymentMethods(): List<PaymentMethod>

    @GET("api/billing/transactions")
    suspend fun getTransactions(
        @Query("limit") limit: Int = 20
    ): List<Transaction>

    // Alerts
    @GET("api/alerts")
    suspend fun getAlerts(
        @Query("unreadOnly") unreadOnly: Boolean = false
    ): List<Alert>

    @POST("api/alerts/{id}/acknowledge")
    suspend fun acknowledgeAlert(@Path("id") id: String)

    // Settings
    @GET("api/settings")
    suspend fun getSettings(): AppSettings

    @PUT("api/settings")
    suspend fun updateSettings(@Body settings: AppSettings)
}

@Serializable
data class ConsoleToken(
    val token: String,
    val url: String
)

@Serializable
data class AppSettings(
    val serverUrl: String = "",
    val autoConnect: Boolean = true,
    val lanDiscovery: Boolean = true,
    val notifications: Boolean = true,
    val darkMode: Boolean = true,
    val language: String = "en",
    val refreshInterval: Int = 30
)
