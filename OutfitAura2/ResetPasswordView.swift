import Foundation
import SwiftUI

struct ResetPasswordView: View {
    @State private var resetCode = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    // Networking service to interact with the backend
    @ObservedObject var authService = AuthService()
    
    var body: some View {
        VStack {
            // Reset Code Input Field
            TextField("Enter reset code", text: $resetCode)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            
            // New Password Input Field
            SecureField("Enter new password", text: $newPassword)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            
            // Confirm Password Input Field
            SecureField("Confirm new password", text: $confirmPassword)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            
            Button("Reset Password") {
                // Call backend to reset the password
                authService.resetPassword(resetCode: resetCode, newPassword: newPassword, confirmPassword: confirmPassword) { success, message in
                    if success {
                        alertMessage = "Password reset successfully!"
                    } else {
                        alertMessage = message ?? "Something went wrong."
                    }
                    showAlert = true
                }
            }
            .padding()
            .background(Color.green)
            .foregroundColor(.white)
            .cornerRadius(10)
            .padding(.top)
            
            Spacer()
        }
        .padding()
        .alert(isPresented: $showAlert) {
            Alert(title: Text("Alert"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
        }
    }
}

struct ResetPasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ResetPasswordView()
    }
}
