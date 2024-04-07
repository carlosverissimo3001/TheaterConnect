package org.feup.carlosverissimo3001.theaterpal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.feup.carlosverissimo3001.theaterpal.marcherFontFamily
import org.feup.carlosverissimo3001.theaterpal.models.Card
import org.feup.carlosverissimo3001.theaterpal.models.User
import org.feup.carlosverissimo3001.theaterpal.screens.fragments.Register.CreditCard
import org.feup.carlosverissimo3001.theaterpal.screens.fragments.Register.fieldColors

@Composable
fun Register(
    onSubmit: (User) -> Unit
){
    var name by remember {
        mutableStateOf("")
    }

    var nif by remember {
        mutableStateOf("")
    }

    var card by remember {
        mutableStateOf<Card?>(null)
    }

    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.onBackground
            )
    ){
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(20.dp)
        ) {
            Text(
                "Welcome to TheaterPal",
                style = TextStyle(
                    color = Color.White,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    textAlign = TextAlign.Center,
                    fontFamily = marcherFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "Please enter your personal information",
                style = TextStyle(
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    textAlign = TextAlign.Center,
                    fontFamily = marcherFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )

            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription =
                        "Enter your name"
                    )
                },
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        "Name",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            textAlign = TextAlign.Center,
                            fontFamily = marcherFontFamily
                        )
                    )
                },
                colors = fieldColors()
            )

            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    capitalization = KeyboardCapitalization.Words
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Money,
                        contentDescription =
                        "Add Card"
                    )
                },
                value = nif,
                onValueChange = { if (it.length <= 9) nif = it },
                label = {
                    Text(
                        "Fiscal Identification Number",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            textAlign = TextAlign.Center,
                            fontFamily = marcherFontFamily
                        )
                    )
                },
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Please enter your credit card information",
                style = TextStyle(
                    color = Color.White,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textAlign = TextAlign.Center,
                    fontFamily = marcherFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )

            CreditCard(
                onCardChange = {
                    println("Card changed")
                    card = it
                }
            )

            Button(
                enabled = name.isNotEmpty() && nif.length == 9 && card != null,
                onClick = { onSubmit(
                    User(
                        name = name,
                        nif = nif,
                        creditCard = card!!
                    )
                ) },
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = "Register",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = marcherFontFamily,
                    ),
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}