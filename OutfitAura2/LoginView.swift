import SwiftUI
import GoogleSignIn

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var showAlert = false
    @State private var alertMessage = ""
    @State private var isLoggedIn = false
    @State private var user: GIDGoogleUser?
    @EnvironmentObject var authService: AuthService // Inject AuthService as an environment object

    // Function to validate email format using regex
    func isValidEmail(_ email: String) -> Bool {
        let regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        let test = NSPredicate(format: "SELF MATCHES %@", regex)
        return test.evaluate(with: email)
    }
    
    // Function to handle login
    func handleLogin() {
        guard isValidEmail(email) else {
            alertMessage = "Please enter a valid email."
            showAlert = true
            return
        }
        
        authService.login(email: email, password: password) { success, errorMessage in
            if success {
                isLoggedIn = true
            } else {
                alertMessage = errorMessage ?? "Login failed. Please try again."
                showAlert = true
            }
        }
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
                        .padding(.top, 60)
                    
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
                    
                    // Login Button
                    Button(action: {
                        handleLogin()
                    }) {
                        Text("Login")
                            .fontWeight(.bold)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color(hex: "#BCFF5E"))
                            .foregroundColor(.black)
                            .cornerRadius(10)
                    }
                    .padding()
                    
                    // Sign Up Text Button
                    NavigationLink(
                        destination: SignUpView(),
                        label: {
                            Text("Sign up")
                                .foregroundColor(Color(hex: "#BCFF5E"))
                                .padding()
                        }
                    )
                    .frame(maxWidth: .infinity)
                    
                    // Forgot Password Text Button
                    NavigationLink(
                        destination: ForgotPasswordView(),
                        label: {
                            Text("Forgot Password?")
                                .foregroundColor(Color(hex: "#BCFF5E"))
                                .padding()
                        }
                    )
                    .frame(maxWidth: .infinity)
                    
                    // Google Sign-In Button
                    Button(action: {
                        
                    }) {
                        HStack {
                            Image("google-logo")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 20, height: 20)
                            Text("Sign Up with Google")
                                .fontWeight(.bold)
                                .foregroundColor(.black)
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.white.opacity(0.9))
                        .cornerRadius(10)
                    }
                    .padding()
                    
                    Spacer()
                }
                .alert(isPresented: $showAlert) {
                    Alert(title: Text("Alert"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
                }
                .navigationDestination(isPresented: $isLoggedIn) {
                    HomePageView()
                }
                .padding(.horizontal)
            }
            .navigationBarBackButtonHidden(true)
        }
    }
}


// extension for using hex colors
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // 
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
