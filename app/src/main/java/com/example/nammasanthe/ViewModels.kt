package com.example.nammasanthe

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow

class AppViewModel : ViewModel() {
    val state: StateFlow<AppState> = Store.state

    fun addCustomer(name: String, phone: String?): Long = Store.addCustomer(name, phone)
    fun addCredit(customerId: Long, amount: Long) = Store.addTxn(customerId, TxnType.CREDIT, amount)
    fun addPayment(customerId: Long, amount: Long) = Store.addTxn(customerId, TxnType.PAYMENT, amount)
    fun addCashSale(amount: Long) = Store.addTxn(null, TxnType.SALE, amount)
}