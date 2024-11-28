//
//  ResetPasswordRequest.swift
//  OutfitAura
//
//  Created by OutfitAura on 21/11/2024.
//

import Foundation

// Request model for password reset
struct ResetPasswordRequest: Codable {
    let resetCode: String
    let newPassword: String
    let confirmPassword: String
}
