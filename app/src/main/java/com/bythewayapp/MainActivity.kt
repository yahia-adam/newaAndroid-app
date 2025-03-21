package com.bythewayapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.bythewayapp.core.ConnectionStateManager
import com.bythewayapp.core.PrivyManager
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var privyManager: PrivyManager
    @Inject lateinit var connectionStateMarnager: ConnectionStateManager

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {

                //Log.d("ConnectionStateManager", "${connectionStateManager.connectionState}")
                /*
                                    Log.i("PrivyManager", "start")
                                    lifecycleScope.launch {
                                        val res1 = privyManager.sendOTPCode("sozako67@gmail.com")
                                        res1.fold(
                                            onSuccess = {
                                                Log.d("PrivyManager", "Succès : le code OTP a été envoyé avec succès.")
                                            },
                                            onFailure = { error ->
                                                Log.e("PrivyManager", "Échec d'envoi du code OTP : ${error.message}")
                                            }
                                        )
                                        val res = privyManager.verifyOTPCode("sozako67@gmail.com", "954166")
                                        res.fold(
                                            onSuccess = { user ->
                                                Log.d("PrivyManager", "Succès ${user}")
                                            },
                                            onFailure = { error ->
                                                Log.e("PrivyManager", "Échec de la verification du code OTP : ${error.message}")
                                            }
                                        )
                    }
                    Log.i("PrivyManager", "end ")
                //Text(text = "HI form main activiy")
*/
                BythewayApp()
            }
        }
    }
}