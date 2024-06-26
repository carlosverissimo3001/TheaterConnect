package org.feup.carlosverissimo3001.theaterpal.screens.fragments.wallet.orders

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.feup.carlosverissimo3001.theaterpal.ParseOrderState
import org.feup.carlosverissimo3001.theaterpal.formatPrice
import org.feup.carlosverissimo3001.theaterpal.marcherFontFamily
import org.feup.carlosverissimo3001.theaterpal.models.order.OrderRcv

@Composable
fun Order(order: OrderRcv) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .background(
                color = Color(android.graphics.Color.parseColor("#36363e")),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                onClick = { println("Clicked on an order") }
            )
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(0.65f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Order Number: ${order.order_number}",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontFamily = marcherFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (order.status == "Preparing") {
                    Text(
                        text = "Estimated time: 5 minutes",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontFamily = marcherFontFamily
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // only when state is preparing or ready
                if (order.status == "Preparing" || order.status == "Ready") {
                    Text(
                        text = "(Please look for your order number in the cafeteria screen)",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontFamily = marcherFontFamily
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Total : ${formatPrice(order.total)}",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontFamily = marcherFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }


                Spacer(modifier = Modifier.height(3.dp))

                if (order.vouchers_used_cnt > 0) {
                    Text(
                        text = "You used " + order.vouchers_used_cnt.toString() + (if (order.vouchers_used_cnt != 1) " vouchers" else " voucher") + " in this order",
                        style = TextStyle(
                            color = Color.LightGray,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontFamily = marcherFontFamily
                        )
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Your order contains:",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontFamily = marcherFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                for (item in order.items) {
                    Text(
                        text = "${item.quantity} x ${item.name}",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontFamily = marcherFontFamily
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                ParseOrderState(order.status)
            }
        }
    }
}

