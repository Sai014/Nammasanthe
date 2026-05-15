package com.example.nammasanthe

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Customer(
    val id: Long,
    val name: String,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
enum class TxnType { SALE, CREDIT, PAYMENT }

@Serializable
data class Txn(
    val id: Long,
    val customerId: Long?,
    val type: TxnType,
    val amount: Long, // paise
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class AppState(
    val customers: List<Customer> = emptyList(),
    val txns: List<Txn> = emptyList(),
    val nextCustomerId: Long = 1,
    val nextTxnId: Long = 1
)

object Store {
    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences("namma_santhe", Context.MODE_PRIVATE)
        load()
    }

    private fun load() {
        val str = prefs.getString("state", null)
        if (str != null) {
            try { _state.value = json.decodeFromString<AppState>(str) } catch (_: Exception) {}
        }
    }

    private fun save() {
        prefs.edit().putString("state", json.encodeToString(_state.value)).apply()
    }

    fun addCustomer(name: String, phone: String?): Long {
        val s = _state.value
        val id = s.nextCustomerId
        _state.value = s.copy(
            customers = s.customers + Customer(id, name, phone),
            nextCustomerId = id + 1
        )
        save()
        return id
    }

    fun addTxn(customerId: Long?, type: TxnType, amount: Long, note: String? = null) {
        val s = _state.value
        val id = s.nextTxnId
        _state.value = s.copy(
            txns = s.txns + Txn(id, customerId, type, amount, note),
            nextTxnId = id + 1
        )
        save()
    }

    fun customerById(id: Long): Customer? = _state.value.customers.find { it.id == id }

    fun customerBalance(customerId: Long): Long {
        return _state.value.txns
            .filter { it.customerId == customerId }
            .sumOf {
                when (it.type) {
                    TxnType.CREDIT -> it.amount
                    TxnType.PAYMENT -> -it.amount
                    TxnType.SALE -> 0L
                }
            }
    }

    fun txnsForCustomer(customerId: Long): List<Txn> =
        _state.value.txns.filter { it.customerId == customerId }.sortedByDescending { it.createdAt }

    fun todaySales(): Long {
        val start = startOfTodayMillis()
        return _state.value.txns.filter { it.type == TxnType.SALE && it.createdAt >= start }.sumOf { it.amount }
    }

    fun todayCredit(): Long {
        val start = startOfTodayMillis()
        return _state.value.txns.filter { it.type == TxnType.CREDIT && it.createdAt >= start }.sumOf { it.amount }
    }

    fun totalOutstanding(): Long {
        return _state.value.txns
            .filter { it.customerId != null }
            .sumOf {
                when (it.type) {
                    TxnType.CREDIT -> it.amount
                    TxnType.PAYMENT -> -it.amount
                    TxnType.SALE -> 0L
                }
            }
    }
}

fun startOfTodayMillis(): Long {
    val cal = java.util.Calendar.getInstance()
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun formatRupees(paise: Long): String {
    val rupees = paise / 100.0
    val nf = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN"))
    return nf.format(rupees)
}