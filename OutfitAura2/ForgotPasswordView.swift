//
//  ForgotPasswordView.swift
//  OutfitAura
//
//  Created by OutfitAura on 21/11/2024.
//

import Foundation
import SwiftUI
struct ForgotPasswordView: View {
    @State private var email = ""
    @State private var showAlert = false
    @State private var alertMessage = ""
    

    @ObservedObject var authService = AuthService() 
    
    var body: some View {
        VStack {
            // Email Input Field
            TextField("Enter your email", text: $email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            
            Button("Send Reset Code") {
                // Call backend to send reset code
                authService.resetPassword(resetCode: "exampleResetCode", newPassword: "newPassword", confirmPassword: "newPassword") { success, message in
                    if success {
                        alertMessage = "Reset code sent successfully!"
                    } else {
                        alertMessage = message ?? "Something went wrong."
                    }
                    showAlert = true
                }
            }
        }
        .padding()
        .background(Color.blue)
        .foregroundColor(.white)
        .cornerRadius(10)
        .padding(.top)
        
        Spacer()
        
            .padding()
            .alert(isPresented: $showAlert) {
                Alert(title: Text("Alert"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
            }
    }
    
    
    struct ForgotPasswordView_Previews: PreviewProvider {
        static var previews: some View {
            ForgotPasswordView()
        }
    }
    
}
