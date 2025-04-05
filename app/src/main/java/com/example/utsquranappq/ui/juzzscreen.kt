package com.example.utsquranappq.ui
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.utsquranappq.R
import com.example.utsquranappq.model.JuzInfo
import com.example.utsquranappq.model.juzListStatic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzTab(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(juzListStatic) { juz ->
            JuzCard(juzInfo = juz, onClick = {
                navController.navigate("juz_detail/${juz.number}")
            })
        }
    }
}

@Composable
fun JuzCard(juzInfo: JuzInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nomor Juz di dalam ikon bulat
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nomorsurah),
                    contentDescription = "Nomor Juz",
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = juzInfo.number.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Juz ${juzInfo.number}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "${juzInfo.startSurah} - Ayat ${juzInfo.startAyah}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.arrowback),
                contentDescription = "Lihat Detail",
                tint = Color(0xFFBB86FC)
            )
        }
    }
}