//
//  AuthService.swift
//  OutfitAura2
//
//  Created by OutfitAura on 26/11/2024.
//

import Foundation

class AuthService: ObservableObject {
    @Published var isAuthenticated = false
    @Published var errorMessage: String? = nil
    private let baseURL = "http://localhost:3000" // Backend URL
    
    // SignUp
    func signUp(email: String, password: String, completion: @escaping (Bool, String?) -> Void) {
        guard let url = URL(string: "\(baseURL)/user/signup") else { return }
        
        let signUpRequest = SignUpRequest(email: email, password: password)
        guard let jsonData = try? JSONEncoder().encode(signUpRequest) else {
            completion(false, "Error encoding sign up data")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.httpBody = jsonData
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {

                if let error = error {
                    completion(false, error.localizedDescription)
                    return
                }
                

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(false, "Invalid server response")
                    return
                }
                

                if httpResponse.statusCode >= 200 && httpResponse.statusCode < 300 {
                    if let data = data, let decodedResponse = try? JSONDecoder().decode(SignUpResponse.self, from: data) {
                        if decodedResponse.success {
                            self.isAuthenticated = true
                            completion(true, nil)  // Sign up success
                        } else {
                            self.errorMessage = "Signup failed: \(decodedResponse.message )"
                            completion(false, decodedResponse.message )
                        }
                    } else {
                        self.errorMessage = "Error decoding response data"
                        completion(false, "Error decoding response data")
                    }
                } else {
                    self.errorMessage = "Failed with status code: \(httpResponse.statusCode)"
                    completion(false, "Failed with status code: \(httpResponse.statusCode)")
                }
            }
        }
        task.resume()
    }
    
    // Log in user
    func login(email: String, password: String, completion: @escaping (Bool, String?) -> Void) {
        guard let url = URL(string: "\(baseURL)/user/login") else { return }
        
        let loginRequest = LoginRequest(email: email, password: password)
        guard let jsonData = try? JSONEncoder().encode(loginRequest) else {
            completion(false, "Error encoding login data")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.httpBody = jsonData
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(false, error.localizedDescription)
                    return
                }
                

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(false, "Invalid server response")
                    return
                }
                

                if httpResponse.statusCode >= 200 && httpResponse.statusCode < 300 {
                    if let data = data, let loginResponse = try? JSONDecoder().decode(LoginResponse.self, from: data), loginResponse.success {
                        self.isAuthenticated = true
                        completion(true, nil)
                    } else {

                        let message = try? JSONDecoder().decode(LoginResponse.self, from: data!).message
                        self.errorMessage = message ?? "Unknown error"
                        completion(false, message ?? "Invalid credentials")
                    }
                } else {

                    self.errorMessage = "Failed with status code: \(httpResponse.statusCode)"
                    completion(false, "Failed with status code: \(httpResponse.statusCode)")
                }
            }
        }
        task.resume()
    }
    

    func resetPassword(resetCode: String, newPassword: String, confirmPassword: String, completion: @escaping (Bool, String?) -> Void) {
        guard let url = URL(string: "\(baseURL)/user/reset-password") else {
            completion(false, "Invalid URL")
            return
        }
        
        let resetPasswordRequest = ResetPasswordRequest(resetCode: resetCode, newPassword: newPassword, confirmPassword: confirmPassword)
        guard let jsonData = try? JSONEncoder().encode(resetPasswordRequest) else {
            completion(false, "Error encoding reset password data")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.httpBody = jsonData
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            DispatchQueue.main.async {
                if let error = error {
                    completion(false, error.localizedDescription)
                    return
                }
                

                guard let httpResponse = response as? HTTPURLResponse else {
                    completion(false, "Invalid server response")
                    return
                }
                

                if httpResponse.statusCode >= 200 && httpResponse.statusCode < 300 {
                    if let data = data, let resetResponse = try? JSONDecoder().decode(ResetPasswordResponse.self, from: data), resetResponse.success {
                        completion(true, nil)
                    } else {

                        let message = try? JSONDecoder().decode(ResetPasswordResponse.self, from: data!).message
                        completion(false, message ?? "Password reset failed")
                    }
                } else {

                    completion(false, "Failed with status code: \(httpResponse.statusCode)")
                }
            }
        }
        task.resume()
    }
}
