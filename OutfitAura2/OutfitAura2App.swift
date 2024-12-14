//
//  OutfitAura2App.swift
//  OutfitAura2
//
//  Created by OutfitAura on 26/11/2024.
//

import SwiftUI
import UIKit

@main
struct OutfitAuraApp: App {
    @StateObject private var authService = AuthService()

    var body: some Scene {
        WindowGroup {
            LoginView()
                .environmentObject(authService)
        }
    }
}
