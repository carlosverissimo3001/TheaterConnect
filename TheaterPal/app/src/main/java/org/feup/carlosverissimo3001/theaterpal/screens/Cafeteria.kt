package org.feup.carlosverissimo3001.theaterpal.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import org.feup.carlosverissimo3001.theaterpal.api.getUserVouchers
import org.feup.carlosverissimo3001.theaterpal.auth.Authentication
import org.feup.carlosverissimo3001.theaterpal.file.areVouchersStoredInCache
import org.feup.carlosverissimo3001.theaterpal.file.loadVouchersFromCache
import org.feup.carlosverissimo3001.theaterpal.file.saveVouchersToCache
import org.feup.carlosverissimo3001.theaterpal.marcherFontFamily
import org.feup.carlosverissimo3001.theaterpal.models.Auxiliary.setTotal
import org.feup.carlosverissimo3001.theaterpal.models.order.*
import org.feup.carlosverissimo3001.theaterpal.models.Voucher
import org.feup.carlosverissimo3001.theaterpal.nfc.buildOrderMessage
import org.feup.carlosverissimo3001.theaterpal.screens.fragments.cafeteria.bar.BarTab
import org.feup.carlosverissimo3001.theaterpal.screens.fragments.cafeteria.ordering.SendOrderActivity
import org.feup.carlosverissimo3001.theaterpal.screens.fragments.cafeteria.voucher.VouchersTab

@Composable
fun Cafeteria(ctx: Context, navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val areVouchersLoaded = remember { mutableStateOf(false) }
    var isChoosingVoucher by remember { mutableStateOf(false) }

    var isSendingOrder by remember { mutableStateOf(false) }

    var vouchersState by remember { mutableStateOf(emptyList<Voucher>()) }
    var filteredVouchers by remember { mutableStateOf(emptyList<Voucher>()) }

    var barOrder by remember { mutableStateOf<BarOrder?>(null) }
    var order by remember { mutableStateOf<Order?>(null) }

    val context = LocalContext.current

    val areVouchersCache = areVouchersStoredInCache(ctx)

    LaunchedEffect(Unit) {
        if (areVouchersCache) {
            loadVouchersFromCache(ctx) { vouchers ->
                vouchersState = vouchers
                areVouchersLoaded.value = true
                filteredVouchers = vouchers.filter { !it.isUsed }
            }
            return@LaunchedEffect
        }

        getUserVouchers(userId = Authentication(ctx).getUserID()) { vouchers ->
            vouchersState = vouchers
            areVouchersLoaded.value = true
            filteredVouchers = vouchers.filter { !it.isUsed }

            if (vouchersState.isNotEmpty()) {
                saveVouchersToCache(vouchers, ctx) { isSuccess ->
                    if (!isSuccess) {
                        Log.e("Cafeteria", "Failed to save vouchers to cache.")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text("Bar",
                        style = TextStyle(
                            fontFamily = marcherFontFamily,
                            color = Color.White,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = (selectedTabIndex == 0).let {
                                if (it) FontWeight.Bold else FontWeight.Normal
                            }
                        )
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text("Vouchers", style =
                    TextStyle(
                        fontFamily = marcherFontFamily,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = (selectedTabIndex == 1).let {
                            if (it) FontWeight.Bold else FontWeight.Normal
                        }
                    )
                    )
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Handle swipe gestures to change tabs
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount > 30) {
                            selectedTabIndex = 0
                        } else if (dragAmount < -30) {
                            selectedTabIndex = 1
                        }
                    }
                }
        ) {
            if (selectedTabIndex == 0) {
                isChoosingVoucher = false
                BarTab { order ->
                    // Navigate to next step
                    barOrder = order
                    isChoosingVoucher = true
                    selectedTabIndex = 1
                }
            } else {
                if (!areVouchersLoaded.value) {
                    LoadingSpinner()
                }
                else {
                    VouchersTab(
                        navController = navController,
                        vouchers = if (isChoosingVoucher) vouchersState.filter { !it.isUsed } else filteredVouchers,
                        onFilterChanged = { isChecked ->
                            // if checked, shows only active vouchers, else shows all vouchers
                            filteredVouchers = if (isChecked) {
                                vouchersState.filter { !it.isUsed }
                            } else {
                                vouchersState
                            }
                        },
                        isChoosingVoucher = isChoosingVoucher,
                        onSubmitted = { selectedVouchers, updatedTotal ->
                            isSendingOrder = true

                            // update total, user might have selected vouchers for discount
                            setTotal(barOrder!!, updatedTotal)

                            // create order
                            order = barOrder?.let {
                                Order(
                                    barOrder = it,
                                    vouchersUsed = selectedVouchers
                                )
                            }

                            // nfc message
                            val message = buildOrderMessage(order!!, ctx)

                            // create intent and put the message and order
                            val intent = Intent(context, SendOrderActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("valuetype", 1)
                            intent.putExtra("order", order)

                            context.startActivity(intent)
                        },
                        total = barOrder?.total ?: 0.00
                    )
                }
            }
        }
    }
}