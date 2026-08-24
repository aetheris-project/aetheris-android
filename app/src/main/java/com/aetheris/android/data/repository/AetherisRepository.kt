package com.aetheris.android.data.repository

import com.aetheris.android.data.api.AetherisApi
import com.aetheris.android.data.local.PreferencesManager
import com.aetheris.android.data.model.*
import com.aetheris.android.data.api.ConsoleToken
import com.aetheris.android.data.api.AppSettings
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AetherisRepository @Inject constructor(
    private val api: AetherisApi,
    private val prefs: PreferencesManager
) {
    // Auth
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            prefs.saveAuth(response.token, response.user.id, response.user.name, response.user.email)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        prefs.clearAuth()
    }

    suspend fun isAuthenticated(): Boolean = prefs.token.first() != null

    suspend fun getToken(): String? = prefs.token.first()

    // Dashboard
    suspend fun getDashboardStats(): Result<DashboardStats> = safeApi { api.getDashboardStats() }
    suspend fun getActivity(limit: Int = 20): Result<List<Activity>> = safeApi { api.getActivity(limit) }

    // Servers
    suspend fun getServers(): Result<List<Server>> = safeApi { api.getServers() }
    suspend fun getServer(id: String): Result<Server> = safeApi { api.getServer(id) }
    suspend fun startServer(id: String): Result<Unit> = safeApi { api.startServer(id) }
    suspend fun stopServer(id: String): Result<Unit> = safeApi { api.stopServer(id) }
    suspend fun restartServer(id: String): Result<Unit> = safeApi { api.restartServer(id) }
    suspend fun suspendServer(id: String): Result<Unit> = safeApi { api.suspendServer(id) }
    suspend fun getConsoleToken(id: String): Result<ConsoleToken> = safeApi { api.getConsoleToken(id) }

    // Nodes
    suspend fun getNodes(): Result<List<Node>> = safeApi { api.getNodes() }
    suspend fun getNode(id: String): Result<Node> = safeApi { api.getNode(id) }

    // Billing
    suspend fun getInvoices(): Result<List<Invoice>> = safeApi { api.getInvoices() }
    suspend fun getInvoice(id: String): Result<Invoice> = safeApi { api.getInvoice(id) }
    suspend fun getServices(): Result<List<Service>> = safeApi { api.getServices() }
    suspend fun getPaymentMethods(): Result<List<PaymentMethod>> = safeApi { api.getPaymentMethods() }
    suspend fun getTransactions(): Result<List<Transaction>> = safeApi { api.getTransactions() }

    // Alerts
    suspend fun getAlerts(unreadOnly: Boolean = false): Result<List<Alert>> = safeApi { api.getAlerts(unreadOnly) }
    suspend fun acknowledgeAlert(id: String): Result<Unit> = safeApi { api.acknowledgeAlert(id) }

    // Generic safe API call
    private suspend fun <T> safeApi(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                401 -> Result.failure(AuthException("Session expired"))
                403 -> Result.failure(AuthException("Access denied"))
                404 -> Result.failure(NotFoundException("Resource not found"))
                500 -> Result.failure(ServerException("Server error"))
                else -> Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class AuthException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
class ServerException(message: String) : Exception(message)
