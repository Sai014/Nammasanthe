package com.example.nammasanthe

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

// ============== BRAND PALETTE ==============
private val GreenStart = Color(0xFF11998E)
private val GreenEnd = Color(0xFF38EF7D)
private val OrangeStart = Color(0xFFFF6A00)
private val OrangeEnd = Color(0xFFFFB347)
private val RedStart = Color(0xFFD31027)
private val RedEnd = Color(0xFFEA384D)
private val BgStart = Color(0xFFFFF8E1)
private val BgEnd = Color(0xFFFFE0B2)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A2E)
private val TextMuted = Color(0xFF6B7280)

// ============== NAV ==============
@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav) }
        composable("customers") { CustomerListScreen(nav) }
        composable("addCustomer") { AddCustomerScreen(nav) }
        composable("customer/{id}") {
            val id = it.arguments?.getString("id")?.toLongOrNull() ?: 0L
            CustomerDetailScreen(nav, id)
        }
        composable("cashSale") { CashSaleScreen(nav) }
    }
}

// ============== REUSABLE FANCY BITS ==============
@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgStart, BgEnd)))
    ) { content() }
}

@Composable
fun FancyKpiCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    gradientStart: Color,
    gradientEnd: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = gradientStart),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .background(Brush.linearGradient(listOf(gradientStart, gradientEnd)))
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = Color.White)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(label, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp,
                        fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(8.dp))
                Text(value, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    gradientStart: Color,
    gradientEnd: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = gradientStart)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(gradientStart, gradientEnd)))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White,
                modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AvatarCircle(name: String, size: Int = 44) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    val colors = listOf(
        Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2))),
        Brush.linearGradient(listOf(Color(0xFFF093FB), Color(0xFFF5576C))),
        Brush.linearGradient(listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))),
        Brush.linearGradient(listOf(Color(0xFF43E97B), Color(0xFF38F9D7))),
        Brush.linearGradient(listOf(Color(0xFFFA709A), Color(0xFFFEE140))),
        Brush.linearGradient(listOf(Color(0xFF30CFD0), Color(0xFF330867))),
    )
    val brush = colors[name.hashCode().mod(colors.size).let { if (it < 0) it + colors.size else it }]
    Box(
        Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size * 0.4).sp)
    }
}

@Composable
fun StatusPill(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ============== HOME ==============
@Composable
fun HomeScreen(nav: NavHostController) {
    val vm: AppViewModel = viewModel()
    val state by vm.state.collectAsState()
    val sales = remember(state) { Store.todaySales() }
    val credit = remember(state) { Store.todayCredit() }
    val outstanding = remember(state) { Store.totalOutstanding() }
    val today = remember { SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH).format(Date()) }

    GradientBackground {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 24.dp)
        ) {
            // Header
            item {
                Column {
                    Text("Namaste 🙏", color = TextMuted, fontSize = 14.sp)
                    Text("Namma-Santhe Ledger", color = TextDark,
                        fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(today, color = TextMuted, fontSize = 13.sp)
                }
                Spacer(Modifier.height(20.dp))
            }
            // KPI cards
            item {
                FancyKpiCard(
                    Icons.Default.TrendingUp, "Today's Sales",
                    formatRupees(sales), GreenStart, GreenEnd,
                    Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
            }
            item {
                Row {
                    FancyKpiCard(
                        Icons.Default.Schedule, "Today's Credit",
                        formatRupees(credit), OrangeStart, OrangeEnd,
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    FancyKpiCard(
                        Icons.Default.AccountBalanceWallet, "Outstanding",
                        formatRupees(outstanding), RedStart, RedEnd,
                        Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(20.dp))
            }
            // Action buttons
            item {
                Row {
                    GradientButton(
                        "+ Credit (Udari)", Icons.Default.Add,
                        { nav.navigate("customers") },
                        OrangeStart, OrangeEnd, Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    GradientButton(
                        "+ Cash Sale", Icons.Default.PointOfSale,
                        { nav.navigate("cashSale") },
                        GreenStart, GreenEnd, Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
            // Customers heading
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Customers", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = TextDark)
                    Spacer(Modifier.width(8.dp))
                    StatusPill("${state.customers.size}", TextMuted)
                }
                Spacer(Modifier.height(12.dp))
            }
            // Customer list
            if (state.customers.isEmpty()) {
                item {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PeopleOutline, null,
                                tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No customers yet", color = TextDark,
                                fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Tap '+ Credit' to add one", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(state.customers, key = { it.id }) { c ->
                    val bal = remember(state) { Store.customerBalance(c.id) }
                    FancyCustomerRow(c.name, bal) { nav.navigate("customer/${c.id}") }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun FancyCustomerRow(name: String, balance: Long, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(name)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = TextDark)
                Spacer(Modifier.height(2.dp))
                StatusPill(
                    if (balance > 0) "Outstanding" else "Settled",
                    if (balance > 0) RedStart else GreenStart
                )
            }
            Text(
                if (balance > 0) formatRupees(balance) else "—",
                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = if (balance > 0) RedStart else GreenStart
            )
        }
    }
}

// ============== CUSTOMER LIST (picker) ==============
@Composable
fun CustomerListScreen(nav: NavHostController) {
    val vm: AppViewModel = viewModel()
    val state by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }
    val filtered = remember(state, query) {
        if (query.isBlank()) state.customers
        else state.customers.filter { it.name.contains(query, ignoreCase = true) }
    }

    GradientBackground {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 48.dp)) {
            FancyTopBar("Pick Customer") { nav.popBackStack() }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = query, onValueChange = { query = it },
                placeholder = { Text("Search customer...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            GradientButton(
                "+ Add New Customer", Icons.Default.PersonAdd,
                { nav.navigate("addCustomer") },
                GreenStart, GreenEnd,
                Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(filtered, key = { it.id }) { c ->
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                            nav.navigate("customer/${c.id}")
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AvatarCircle(c.name, size = 36)
                            Spacer(Modifier.width(12.dp))
                            Text(c.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FancyTopBar(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ArrowBack, "Back", tint = TextDark)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

// ============== ADD CUSTOMER ==============
@Composable
fun AddCustomerScreen(nav: NavHostController) {
    val vm: AppViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    GradientBackground {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 48.dp)) {
            FancyTopBar("New Customer") { nav.popBackStack() }
            Spacer(Modifier.height(24.dp))

            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(Modifier.padding(20.dp)) {
                    if (name.isNotBlank()) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AvatarCircle(name, size = 64)
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Customer Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text("Phone (for WhatsApp)") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            GradientButton(
                "Save Customer", Icons.Default.Check,
                {
                    if (name.isBlank()) return@GradientButton
                    val id = vm.addCustomer(name.trim(), phone.trim().ifBlank { null })
                    nav.navigate("customer/$id") { popUpTo("home") }
                },
                GreenStart, GreenEnd,
                Modifier.fillMaxWidth()
            )
        }
    }
}

// ============== CUSTOMER DETAIL ==============
@Composable
fun CustomerDetailScreen(nav: NavHostController, customerId: Long) {
    val vm: AppViewModel = viewModel()
    val state by vm.state.collectAsState()
    val customer = remember(state, customerId) { Store.customerById(customerId) }
    val balance = remember(state, customerId) { Store.customerBalance(customerId) }
    val txns = remember(state, customerId) { Store.txnsForCustomer(customerId) }
    val ctx = LocalContext.current

    var amountInput by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("CREDIT") }

    GradientBackground {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 24.dp)
        ) {
            item {
                FancyTopBar(customer?.name ?: "Customer") { nav.popBackStack() }
                Spacer(Modifier.height(16.dp))
            }
            // Big balance card
            item {
                Card(
                    Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        Modifier.background(
                            Brush.linearGradient(
                                if (balance > 0) listOf(RedStart, RedEnd)
                                else listOf(GreenStart, GreenEnd)
                            )
                        ).padding(24.dp).fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()) {
                            AvatarCircle(customer?.name ?: "?", size = 56)
                            Spacer(Modifier.height(8.dp))
                            Text("Outstanding Balance",
                                color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                            Text(
                                if (balance > 0) formatRupees(balance) else "All Settled ✓",
                                color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            // Mode toggle
            item {
                Row {
                    ModeChip("Add Credit", Icons.Default.ArrowUpward,
                        mode == "CREDIT", OrangeStart) { mode = "CREDIT" }
                    Spacer(Modifier.width(8.dp))
                    ModeChip("Payment", Icons.Default.ArrowDownward,
                        mode == "PAYMENT", GreenStart) { mode = "PAYMENT" }
                }
                Spacer(Modifier.height(12.dp))
            }
            // Amount display
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Text(
                        if (amountInput.isEmpty()) "₹0" else "₹$amountInput",
                        Modifier.fillMaxWidth().padding(20.dp),
                        textAlign = TextAlign.Center, fontSize = 42.sp,
                        fontWeight = FontWeight.Bold, color = TextDark
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            // Keypad
            item {
                NumericKeypad(
                    onDigit = { d -> if (amountInput.length < 7) amountInput += d },
                    onBackspace = { if (amountInput.isNotEmpty()) amountInput = amountInput.dropLast(1) }
                )
                Spacer(Modifier.height(8.dp))
            }
            // Save button
            item {
                GradientButton(
                    if (mode == "CREDIT") "Save Credit" else "Save Payment",
                    Icons.Default.Check,
                    {
                        val rupees = amountInput.toLongOrNull() ?: return@GradientButton
                        val paise = rupees * 100
                        if (mode == "CREDIT") vm.addCredit(customerId, paise)
                        else vm.addPayment(customerId, paise)
                        amountInput = ""
                    },
                    if (mode == "CREDIT") OrangeStart else GreenStart,
                    if (mode == "CREDIT") OrangeEnd else GreenEnd,
                    Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }
            // WhatsApp
            if (customer?.phone != null && balance > 0) {
                item {
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            val msg = "Namaste ${customer.name} ji, aapka ${formatRupees(balance)} ka udari pending hai. Kripya jaldi chukane ki kripa karein. Dhanyavaad."
                            val url = "https://wa.me/${customer.phone}?text=${Uri.encode(msg)}"
                            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF25D366))
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, null, tint = Color.White)
                            Spacer(Modifier.width(10.dp))
                            Text("Send WhatsApp Reminder",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
            // History
            item {
                Text("Transaction History", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
            }
            items(txns, key = { it.id }) { t ->
                TxnRow(t)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun ModeChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
             selected: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent else CardBg)
            .border(1.dp, accent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (selected) Color.White else accent,
                modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, color = if (selected) Color.White else accent,
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TxnRow(t: Txn) {
    val (color, icon, sign) = when (t.type) {
        TxnType.CREDIT -> Triple(RedStart, Icons.Default.ArrowUpward, "+")
        TxnType.PAYMENT -> Triple(GreenStart, Icons.Default.ArrowDownward, "-")
        TxnType.SALE -> Triple(GreenStart, Icons.Default.PointOfSale, "")
    }
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(t.type.name, fontWeight = FontWeight.SemiBold, color = TextDark, fontSize = 14.sp)
                Text(SimpleDateFormat("d MMM, h:mm a", Locale.ENGLISH).format(Date(t.createdAt)),
                    color = TextMuted, fontSize = 11.sp)
            }
            Text("$sign${formatRupees(t.amount)}",
                color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// ============== KEYPAD ==============
@Composable
fun NumericKeypad(onDigit: (String) -> Unit, onBackspace: () -> Unit) {
    val keys = listOf(
        listOf("1","2","3"), listOf("4","5","6"),
        listOf("7","8","9"), listOf("0","00","⌫")
    )
    Column {
        keys.forEach { row ->
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                row.forEach { k ->
                    Card(
                        Modifier
                            .weight(1f)
                            .height(58.dp)
                            .padding(horizontal = 3.dp)
                            .clickable { if (k == "⌫") onBackspace() else onDigit(k) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (k == "⌫") Color(0xFFFFEBEE) else CardBg
                        )
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(k, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = if (k == "⌫") RedStart else TextDark)
                        }
                    }
                }
            }
        }
    }
}

// ============== CASH SALE ==============
@Composable
fun CashSaleScreen(nav: NavHostController) {
    val vm: AppViewModel = viewModel()
    var amountInput by remember { mutableStateOf("") }

    GradientBackground {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 48.dp)) {
            FancyTopBar("Cash Sale") { nav.popBackStack() }
            Spacer(Modifier.height(20.dp))

            Card(
                Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    Modifier.background(Brush.linearGradient(listOf(GreenStart, GreenEnd)))
                        .padding(24.dp).fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PointOfSale, null, tint = Color.White,
                            modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Cash Sale Amount", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        Text(
                            if (amountInput.isEmpty()) "₹0" else "₹$amountInput",
                            color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            NumericKeypad(
                onDigit = { d -> if (amountInput.length < 7) amountInput += d },
                onBackspace = { if (amountInput.isNotEmpty()) amountInput = amountInput.dropLast(1) }
            )
            Spacer(Modifier.height(12.dp))
            GradientButton(
                "Save Cash Sale", Icons.Default.Check,
                {
                    val rupees = amountInput.toLongOrNull() ?: return@GradientButton
                    vm.addCashSale(rupees * 100)
                    nav.popBackStack()
                },
                GreenStart, GreenEnd,
                Modifier.fillMaxWidth()
            )
        }
    }
}