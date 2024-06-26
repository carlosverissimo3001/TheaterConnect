package org.feup.carlosverissimo3001.theaterpal.screens.fragments.cafeteria.voucher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.feup.carlosverissimo3001.theaterpal.formatPrice
import org.feup.carlosverissimo3001.theaterpal.marcherFontFamily
import org.feup.carlosverissimo3001.theaterpal.models.Parser.parseVoucherType
import org.feup.carlosverissimo3001.theaterpal.models.Voucher
import java.util.Locale

@Composable
fun VouchersTab(
    vouchers: List<Voucher>,
    onFilterChanged: (Boolean) -> Unit,
    isChoosingVoucher: Boolean,
    onSubmitted: (List<Voucher>, Double) -> Unit,
    total : Double = 0.0,
    navController: NavController
) {
    val (isChecked, setFilterChecked) = remember { mutableStateOf(true) }

    // When user selects more than 2 vouchers
    val (isSelectingMore, setSelectingMore) = remember { mutableStateOf(false) }
    val (isDiscountAlreadyApplied, setDiscountAlreadyApplied) = remember { mutableStateOf(false) }

    val selectedVouchers = remember { mutableStateOf(emptyList<Voucher>()) }

    // when a voucher is not in view, its state should still be remembered
    val selectedVouchersIndices = remember { mutableStateListOf<Int>() }
    var updatedTotal by remember { mutableDoubleStateOf(total) }

    val possibleDiscount = String.format(Locale.US, "%.2f", total * 0.05).toDouble()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        if (!isChoosingVoucher) {
            if (vouchers.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            setFilterChecked(it)
                            onFilterChanged(it)
                        },
                    )
                    Text(
                        text = "View only active vouchers", style = TextStyle(
                            fontFamily = marcherFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectingMore) {
                    Text(
                        text = "You can only select up to 2 vouchers", style = TextStyle(
                            fontFamily = marcherFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.Red
                        )
                    )
                }
                else {
                    if (vouchers.isNotEmpty()) {
                        Text(
                            text = "Please select up to 2 vouchers", style = TextStyle(
                                fontFamily = marcherFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        )
                    }

                    else {
                        Text(
                            text = "No vouchers available :(",
                            style = TextStyle(
                                fontFamily = marcherFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                color = Color.White
                            )
                        )
                    }
                }
            }


        }

        if (vouchers.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(10.dp)
            ) {
                items(vouchers.size) { index ->
                    val vch = vouchers[index]

                    Voucher(
                        voucher = vch,
                        // Can select a new one if there are less than 2 selected
                        // Second condition to prevent the case where 2 vouchers are selected and the user tries to unslect one
                        canSelect = selectedVouchers.value.size < 2 || selectedVouchers.value.contains(
                            vch
                        ),
                        selectionMode = isChoosingVoucher,
                        onSelected = { voucher, success ->
                            // Voucher already selected, remove it
                            if (selectedVouchers.value.contains(voucher)) {
                                selectedVouchers.value =
                                    selectedVouchers.value.filter { it != voucher }
                                selectedVouchersIndices.remove(index)

                                if (parseVoucherType(voucher.voucherType) == "5% Discount") {
                                    updatedTotal += possibleDiscount
                                    setDiscountAlreadyApplied(false)
                                }

                            } else {
                                if (selectedVouchers.value.size < 2) {
                                    selectedVouchers.value += voucher
                                    selectedVouchersIndices.add(index)
                                }
                                if (parseVoucherType(voucher.voucherType) == "5% Discount") {
                                    updatedTotal -= possibleDiscount
                                    setDiscountAlreadyApplied(true)
                                }
                            }

                            setSelectingMore(!success)
                        },
                        isDiscountAlreadyApplied = isDiscountAlreadyApplied && !selectedVouchers.value.contains(
                            vch
                        )
                    )
                }
            }
        }
        else {
            NoVouchers(
                onClick = {route ->
                    navController.navigate(route)
                },
            )
        }
    }

    if (isChoosingVoucher) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            FloatingActionButton(
                onClick = {
                    onSubmitted(selectedVouchers.value, updatedTotal)
                },
                modifier = Modifier
                    .width(200.dp),
                containerColor = Color(0xFF43A047),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Submit Order",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = marcherFontFamily,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    )
                    Text(
                        text = "Your total is ${formatPrice(updatedTotal)}",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            fontFamily = marcherFontFamily,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    )
                }
            }
        }
    }
}
