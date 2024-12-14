import SwiftUI

struct SignUpView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    @StateObject var authService = AuthService() // Initialize AuthService
    
    // Function to validate email format using regex
    func isValidEmail(_ email: String) -> Bool {
        let regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        let test = NSPredicate(format: "SELF MATCHES %@", regex)
        return test.evaluate(with: email)
    }

    // Function to validate password length
    func isValidPassword(_ password: String) -> Bool {
        return password.count >= 8
    }

    var body: some View {
        NavigationStack {
            ZStack {
                Color.black.edgesIgnoringSafeArea(.all)

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

                    Image("v")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 290, height: 290)
                        .padding(.top, 10)

                    Spacer()

                    // Email TextField
                    TextField("Email", text: $email)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                        .background(Color(hex: "#BCFF5E"))
                        .frame(maxWidth: 350)

                    // Password SecureField
                    SecureField("Password", text: $password)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                        .background(Color(hex: "#BCFF5E"))
                        .frame(maxWidth: 350)

                    // Confirm Password SecureField
                    SecureField("Confirm Password", text: $confirmPassword)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()
                        .background(Color(hex: "#BCFF5E"))
                        .frame(maxWidth: 350)

                    // Sign-Up Button
                    Button(action: {
                        if !isValidEmail(email) {
                            alertMessage = "Please enter a valid email."
                            showAlert = true
                            return
                        }

                        if password != confirmPassword {
                            alertMessage = "Passwords do not match!"
                            showAlert = true
                            return
                        }

                        if !isValidPassword(password) {
                            alertMessage = "Password must be at least 8 characters long."
                            showAlert = true
                            return
                        }

                        // Call the sign-up function from AuthService
                        authService.signUp(email: email, password: password) { success, message in
                            if success {
                                alertMessage = "Sign-Up Successful!"
                            } else {
                                alertMessage = message ?? "An error occurred"
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
                        .foregroundColor(Color(hex: "#BCFF5E"))
                        .padding()

                    Spacer()
                }
                .alert(isPresented: $showAlert) {
                    Alert(title: Text("Alert"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
                }
                .padding(.horizontal)
            }
            .navigationBarBackButtonHidden(true) // Hides the back button
        }
    }
}
