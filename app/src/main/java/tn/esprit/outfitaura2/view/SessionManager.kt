package tn.esprit.outfitaura2.view

import android.content.Context
import tn.esprit.outfitaura2.network.User

class SessionManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Save user data
    fun saveUser(user: User) {
        val editor = sharedPref.edit()
        editor.putString("user_email", user.email)  // Only save the email now
        editor.apply()
    }

    // Retrieve user data
    fun getUser(): User? {
        val userEmail = sharedPref.getString("user_email", null)
        return if (userEmail != null) {
            User(userEmail)  // Only create the User object with email
        } else null
    }

    // Clear session data
    fun clearSession() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPref.contains("user_email")  // Check if the email exists in the shared preferences
    }
}
