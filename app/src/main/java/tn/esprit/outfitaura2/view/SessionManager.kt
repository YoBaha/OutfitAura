package tn.esprit.outfitaura2.view

import android.content.Context
import tn.esprit.outfitaura2.network.User

class SessionManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Save user data with email instead of login
    fun saveUser(user: User) {
        val editor = sharedPref.edit()
        editor.putInt("user_id", user.id)
        editor.putString("user_email", user.email) // Save email instead of login
        editor.apply()
    }

    // Retrieve user data
    fun getUser(): User? {
        val userId = sharedPref.getInt("user_id", -1)
        val userEmail = sharedPref.getString("user_email", null) // Retrieve email instead of login
        return if (userId != -1 && userEmail != null) {
            User(userId, userEmail) // Use email instead of login
        } else null
    }

    // Clear session data
    fun clearSession() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}
