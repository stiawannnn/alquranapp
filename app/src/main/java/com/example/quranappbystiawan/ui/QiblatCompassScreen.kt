package com.example.quranappbystiawan.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quranappbystiawan.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaCompassScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }
    var qiblaAngle by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    var statusMessage by remember { mutableStateOf("Memulai...") }

    // Permission handling
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Get location
    LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            statusMessage = "Membutuhkan izin lokasi"
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return@LaunchedEffect
        }

        statusMessage = "Mencari lokasi..."
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        userLat = location.latitude
                        userLng = location.longitude
                        qiblaAngle = calculateQiblaDirection(location.latitude, location.longitude)
                        statusMessage = "Lokasi ditemukan"
                    } else {
                        getCurrentLocation(fusedLocationClient) { lat, lng ->
                            userLat = lat
                            userLng = lng
                            qiblaAngle = calculateQiblaDirection(lat, lng)
                            statusMessage = "Lokasi ditemukan"
                        }
                    }
                }
                .addOnFailureListener {
                    statusMessage = "Gagal mendapatkan lokasi: ${it.message}"
                }
        } catch (e: Exception) {
            statusMessage = "Error: ${e.message}"
        }
    }

    // Sensor handling
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    DisposableEffect(Unit) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val accelData = FloatArray(3)
        val magData = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> System.arraycopy(it.values, 0, accelData, 0, 3)
                        Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(it.values, 0, magData, 0, 3)
                    }

                    if (SensorManager.getRotationMatrix(rotationMatrix, null, accelData, magData)) {
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        rotation = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A41BE), // Biru muda
                        Color(0xFF2AB8D0), // Kuning muda
                        Color(0xFF9B3CBD)  // Hijau muda
                    )
                )
            ),
        contentAlignment = Alignment.Center

    ) {
        if (userLat == null || userLng == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = statusMessage,
                    fontSize = 18.sp,
                    color = if (statusMessage.contains("Gagal") || statusMessage.contains("Error")) Color.Red else Color.Black
                )
                if (!hasPermission) {
                    Text(
                        text = "Silakan izinkan akses lokasi di pengaturan",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            val adjustedAngle = (rotation - qiblaAngle + 360) % 360
            val isAligned = abs(adjustedAngle) < 5f || abs(adjustedAngle - 360) < 5f
            val currentDirection = (rotation + 360) % 360 // Normalisasi rotasi ke 0-360

            // Single rotating compass
            Image(
                painter = painterResource(id = R.drawable.kompas11),
                contentDescription = "Kompas Kiblat",
                modifier = Modifier
                    .size(999.dp)
                    .rotate(qiblaAngle - rotation)
            )

            // Information and feedback
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
            ) {
                Text(
                    text = if (isAligned) "Anda Tepat Menghadap Kiblat" else "Silakan Putar ke Arah Kiblat",
                    color = if (isAligned) Color.Green else Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lokasi Kiblat: ${String.format("%.1f", qiblaAngle)}° ${getDirection(qiblaAngle)}",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Sekarang Ada di Arah: ${String.format("%.1f", currentDirection)}° ${getDirection(currentDirection)}",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Glow effect when aligned
                if (isAligned) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Green, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

// Fungsi untuk mengubah derajat menjadi arah mata angin
private fun getDirection(degrees: Float): String {
    return when {
        degrees >= 337.5 || degrees < 22.5 -> "N"
        degrees >= 22.5 && degrees < 67.5 -> "NE"
        degrees >= 67.5 && degrees < 112.5 -> "E"
        degrees >= 112.5 && degrees < 157.5 -> "SE"
        degrees >= 157.5 && degrees < 202.5 -> "S"
        degrees >= 202.5 && degrees < 247.5 -> "SW"
        degrees >= 247.5 && degrees < 292.5 -> "NW"
        degrees >= 292.5 && degrees < 337.5 -> "W"
        else -> ""
    }
}

private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Double, Double) -> Unit
) {
    try {
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location.latitude, location.longitude)
            }
        }
    } catch (e: SecurityException) {
        // Handle permission issues
    }
}

fun calculateQiblaDirection(userLat: Double, userLng: Double): Float {
    val kaabaLat = 21.4225
    val kaabaLng = 39.8262

    val lat1 = Math.toRadians(userLat)
    val lon1 = Math.toRadians(userLng)
    val lat2 = Math.toRadians(kaabaLat)
    val lon2 = Math.toRadians(kaabaLng)

    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    var bearing = Math.toDegrees(atan2(y, x))

    bearing = (bearing + 360) % 360
    return bearing.toFloat()
}