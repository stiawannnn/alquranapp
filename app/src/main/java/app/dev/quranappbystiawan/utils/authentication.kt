package app.dev.quranappbystiawan.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import app.dev.quranappbystiawan.R

object FirebaseUtils {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signOut(context: Context, onComplete: () -> Unit) {
        auth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail().requestProfile()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        // Logout dari Google + revoke akses agar akun tidak disimpan
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                onComplete()
            }
        }
    }
}