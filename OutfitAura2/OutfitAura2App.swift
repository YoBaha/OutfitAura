//
//  OutfitAura2App.swift
//  OutfitAura2
//
//  Created by OutfitAura on 26/11/2024.
//

import SwiftUI
import UIKit

@main
struct OutfitAura2App: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            LoginView()
        }
    }
}
