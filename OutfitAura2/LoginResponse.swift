//
//  LoginResponse.swift
//  OutfitAura
//
//  Created by OutfitAura on 20/11/2024.
//

import Foundation
struct LoginResponse: Codable {
    let success: Bool
    let token: String? 
    let message: String?
}

