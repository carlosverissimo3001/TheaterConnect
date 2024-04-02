package org.feup.carlosverissimo3001.theaterpal.screens.fragments.Cafeteria

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.feup.carlosverissimo3001.theaterpal.marcherFontFamily
import org.feup.carlosverissimo3001.theaterpal.models.CafeteriaItem
import org.feup.carlosverissimo3001.theaterpal.models.getCafeteriaItems


@Composable
fun BarTab(ctx: Context) {
    var barItems : List<CafeteriaItem> = getCafeteriaItems()
    var total by remember { mutableDoubleStateOf(0.0) }
    var order by remember { mutableStateOf("") }

    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(barItems.size) { index ->
                CafeteriaItem(
                    item = barItems[index],
                    onIncrement = { item ->
                        total += item.price
                    },
                    onDecrement = { item ->
                        total -= item.price
                    },
                    onRemove = { item, quantity ->
                        total -= item.price * quantity
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOTAL
            Box (
                modifier = Modifier.size(200.dp, 50.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(android.graphics.Color.parseColor("#36363e")),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .size(120.dp, 40.dp)
                        .padding(start = 10.dp, end = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row (
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ",
                            style = TextStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = marcherFontFamily,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        )
                        Text(
                            text = formatPrice(total),
                            style = TextStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Normal,
                                fontFamily = marcherFontFamily,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        )
                    }
                }
            }

            // NEXT STEP
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .width(200.dp),
                containerColor = Color(0xFF43A047),
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Next Step >",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = marcherFontFamily,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    )
                    Text(
                        text = "Apply vouchers",
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
