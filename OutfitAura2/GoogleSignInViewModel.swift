//
//  GoogleSignInViewModel.swift
//  OutfitAura2
//
//  Created by OutfitAura on 26/11/2024.
//

import Foundation
import GoogleSignIn
import GoogleSignInSwift

class GoogleSignInViewModel: ObservableObject {
    @Published var isSignedIn = false

    func signIn() {
        guard let rootViewController = UIApplication.shared.windows.first?.rootViewController else {
            return
        }

        GIDSignIn.sharedInstance.signIn(
            withPresenting: rootViewController
        ) { result, error in
            if let error = error {
                print("Google Sign-In failed: \(error.localizedDescription)")
                return
            }

            if let user = result?.user {
                self.isSignedIn = true
                print("User signed in: \(user.profile?.email ?? "No email")")
            }
        }
    }

    func signOut() {
        GIDSignIn.sharedInstance.signOut()
        isSignedIn = false
    }
}
