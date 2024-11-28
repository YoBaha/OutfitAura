//
 // CameraView.swift
 // OutfitAura
 //
 // Created by OutfitAura on 20/11/2024.
 //
 import Foundation
 import SwiftUI
 import UIKit
 struct CameraView: UIViewControllerRepresentable {
 @Binding var isCameraActive: Bool
 @Binding var capturedImage: UIImage?
 class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
 var parent: CameraView
 init(parent: CameraView) {
 self.parent = parent
 }
 func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
 if let image = info[.originalImage] as? UIImage {
 parent.capturedImage = image
 }
 parent.isCameraActive = false
 }
 func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
 parent.isCameraActive = false
 }
 }
 func makeCoordinator() -> Coordinator {
 return Coordinator(parent: self)
 }
 func makeUIViewController(context: Context) -> UIImagePickerController {
 let picker = UIImagePickerController()
 picker.sourceType = .camera
 picker.delegate = context.coordinator
 return picker
 }
 func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
 }
