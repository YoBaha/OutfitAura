import SwiftUI

struct SignUpView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    @ObservedObject var authService = AuthService()


    func isValidPassword(_ password: String) -> Bool {

        let regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$&*])[A-Za-z\\d!@#$&*]{8,}$"
        let test = NSPredicate(format: "SELF MATCHES %@", regex)
        return test.evaluate(with: password)
    }

    var body: some View {
        NavigationStack {
            ZStack {
                Color.white.edgesIgnoringSafeArea(.all)

                VStack {

                    Text("OUTFITAURA")
                        .font(.system(size: 36, weight: .bold))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [Color(hex: "#BCFF5E"), Color(hex: "#282b30")],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .padding(.top, 50)

                    Spacer()

                    // Email TextField
                    TextField("Email", text: $email)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                        .background(Color.white.opacity(0.7))
                        .frame(maxWidth: 350)

                    // Password SecureField
                    SecureField("Password", text: $password)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                        .background(Color.white.opacity(0.7))
                        .frame(maxWidth: 350)

                    // Confirm Password SecureField
                    SecureField("Confirm Password", text: $confirmPassword)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                        .background(Color.white.opacity(0.7))
                        .frame(maxWidth: 350)

                    // Sign-Up Button
                    Button(action: {
                        if password != confirmPassword {
                            alertMessage = "Passwords do not match!"
                            showAlert = true
                            return
                        }
                        
                        if !isValidPassword(password) {
                            alertMessage = "Password must be at least 8 characters long, include an uppercase letter, a number, and a special character."
                            showAlert = true
                            return
                        }
                        
                        authService.signUp(email: email, password: password) { success, message in
                            if success {
                                alertMessage = "SignUp Successful!"
                            } else {
                                alertMessage = message ?? "Something went wrong"
                            }
                            showAlert = true
                        }
                    }) {
                        Text("Sign Up")
                            .fontWeight(.bold)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color(hex: "#BCFF5E"))
                            .foregroundColor(.black)
                            .cornerRadius(10)
                    }
                    .padding()

                    NavigationLink("Already have an account?", destination: LoginView())
                        .padding()

                    Spacer()
                }
                .alert(isPresented: $showAlert) {
                    Alert(title: Text("Alert"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
                }
                .padding(.horizontal)
            }
        }
    }
}

