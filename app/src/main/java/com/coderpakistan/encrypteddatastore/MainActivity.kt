package com.coderpakistan.encrypteddatastore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.dataStore
import com.coderpakistan.encrypteddatastore.ui.theme.EncryptedDataStoreTheme
import com.coderpakistan.encrypteddatastore.utils.UserPreferenceSerializer
import com.coderpakistan.encrypteddatastore.utils.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private val Context.dataStore by dataStore(
    fileName = "user-preferences",
    serializer = UserPreferenceSerializer
)
private val SECRET_TOKEN = (1..1000).map {
    (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
}.joinToString(separator = "")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EncryptedDataStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val scope = rememberCoroutineScope()
                        var text by remember {
                            mutableStateOf("")
                        }
                        Button(onClick = {
                            scope.launch {
                                dataStore.updateData {
                                    UserPreferences(token = SECRET_TOKEN)
                                }
                            }
                        }) {
                            Text("Encrypt")
                        }
                        Button(onClick = {
                            scope.launch {
                                text = dataStore.data.first().token ?: ""
                            }
                        }) {
                            Text("Decrypt")
                        }
                        Text(text = text)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EncryptedDataStoreTheme {
        Greeting("Android")
    }
}