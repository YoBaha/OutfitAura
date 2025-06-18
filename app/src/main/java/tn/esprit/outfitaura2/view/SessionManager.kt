package tn.esprit.outfitaura2.view

import android.content.Context
import tn.esprit.outfitaura2.network.User

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("OutfitAuraPrefs", Context.MODE_PRIVATE)

    fun saveUser(user: tn.esprit.outfitaura2.models.User, token: String) {
        val editor = prefs.edit()
        editor.putString("user_email", user.email)
        editor.putString("jwt_token", token)
        editor.apply()
    }

    fun getUser(): User? {
        val email = prefs.getString("user_email", null) ?: return null
        return User(email)
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}