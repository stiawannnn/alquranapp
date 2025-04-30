
package app.dev.quranappbystiawan.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dev.quranappbystiawan.navigation.HomeScreenActivity
import app.dev.quranappbystiawan.R
import app.dev.quranappbystiawan.utils.FirebaseUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseUtils.getCurrentUser() != null) {
                startActivity(Intent(this, HomeScreenActivity::class.java))
                finish()
            }
        }, if (allGranted) 3000 else 5000)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseUtils.auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    finish()
                }
            }
        } catch (e: Exception) {
            // Tangani error (misal, tampilkan toast)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.USE_EXACT_ALARM)
        }

        if (permissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissions.toTypedArray())
        } else if (FirebaseUtils.getCurrentUser() == null) {
            // Biarkan SplashScreen menampilkan UI login
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeScreenActivity::class.java))
                finish()
            }, 3000)
        }
    }

    @Composable
    fun SplashScreen() {
        var showLogin by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(3000)
            showLogin = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A41BE),
                            Color(0xFF2AB8D0),
                            Color(0xFF9B3CBD)
                        )
                    )
                )
        ) {
            // Gambar splash/logo diatur dengan offset ke atas/tengah sesuai kebutuhan
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Logo Aplikasi",
                modifier = Modifier
                    .size(233.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 247.dp) // Ganti ini untuk atur posisi vertikal
            )

            // Teks aplikasi & nama
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (16).dp), // Geser teks sesuai kebutuhan
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "My Quran App",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dendi Setiawan",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Gambar masjid di bawah, juga bisa pakai offset
            Image(
                painter = painterResource(id = R.drawable.ui),
                contentDescription = "Masjid",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = (1).dp) // Geser sedikit ke atas, bisa disesuaikan
            )

            // Tombol login Google
            AnimatedVisibility(
                visible = showLogin && FirebaseUtils.getCurrentUser() == null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (107).dp) // Geser sesuai kebutuhan
            ) {
                Button(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient =
                            GoogleSignIn.getClient(this@SplashScreenActivity, gso)
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Sign in with Google",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}