import SwiftUI

struct HomePageView: View {
    @State private var isCameraActive = false
    @State private var isGalleryActive = false
    @State private var searchText = ""
    @State private var isImagePickerPresented = false
    @State private var selectedImage: UIImage? = nil
    
    @State private var imageList: [(image: UIImage, prediction: String)] = []
  
    let modelImages = ["model1", "model2", "model3", "model4"]
    
    var body: some View {
        ZStack {
            // Gradient Background
            LinearGradient(gradient: Gradient(colors: [Color.white, Color(UIColor.systemGray5)]), startPoint: .top, endPoint: .bottom)
                .edgesIgnoringSafeArea(.all)
            
            ScrollView {
                VStack(spacing: 10) {
                    Text("Outfitaura")
                        .font(.system(size: 36, weight: .bold))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [Color(hex: "#BCFF5E"), Color(hex: "#282b30")],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .padding(.top, 20)
                    
                    HStack {
                        // Custom Search Icon from assets
                        Image("search")  // Use the name of your custom image here
                            .resizable()
                            .scaledToFit()
                            .frame(width: 24, height: 24)  // Adjust the size of your image
                            .foregroundColor(.white)
                            .padding(.leading, 12)
                        
                        TextField("Search...", text: $searchText)
                            .padding()
                            .background(Color(hex: "#282B30"))
                            .foregroundColor(.white)
                            .cornerRadius(10)
                            .padding(.horizontal)
                    }
                    .frame(height: 50)
                    
                    // Marketing Cards
                    MarketingCard(imageName: "card", text: "Discover our latest features and tools!")
                    MarketingCard(imageName: "card2", text: "Explore new possibilities with our services!")
                    
                    // Grid for Model Images
                    Text("Our Models")
                        .font(.headline)
                        .foregroundColor(.black)
                        .padding(.horizontal)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 16) {
                        ForEach(modelImages, id: \.self) { modelImage in
                            Image(modelImage)
                                .resizable()
                                .scaledToFill()
                                .frame(width: 120, height: 120)
                                .clipped()
                                .cornerRadius(8)
                        }
                    }
                    .padding(.horizontal)
                    
                    // Button for Uploading Image
                    Button(action: {
                        isImagePickerPresented.toggle()
                    }) {
                        Text("Upload Image")
                            .font(.headline)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color(hex: "#BCFF5E"))
                            .foregroundColor(.black)
                            .cornerRadius(10)
                            .padding(.horizontal)
                    }
                    
                    Spacer(minLength: 120)
                }
                .padding(.horizontal)
                .padding(.top, 20)
            }
            
            VStack {
                Spacer()
                HStack {
                    // Wardrobe Button
                    WardrobeButton()
                    
                    // Gallery Button
                    Button(action: {
                        isGalleryActive.toggle()
                    }) {
                        Image(systemName: "photo.on.rectangle.angled")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 60, height: 60)
                            .background(Color(hex: "#BCFF5E"))
                            .clipShape(Circle())
                            .foregroundColor(.white)
                            .padding()
                    }
                    
                    // Camera Button
                    Button(action: {
                        isCameraActive.toggle()
                    }) {
                        Image(systemName: "camera")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 60, height: 60)
                            .background(Color(hex: "#BCFF5E"))
                            .clipShape(Circle())
                            .foregroundColor(.white)
                            .padding()
                    }
                }
                .padding()
                .background(Color.black, in: RoundedRectangle(cornerRadius: 20))
                .padding(.horizontal)
            }
        }
        .navigationBarHidden(true)
        .sheet(isPresented: $isImagePickerPresented) {
            ImagePicker(selectedImage: $selectedImage, isImagePickerPresented: $isImagePickerPresented)
                .onChange(of: selectedImage) { newImage in
                    if let newImage = newImage {
                        imageList.append((image: newImage, prediction: "New Image"))
                    }
                }
        }
    }
}

struct MarketingCard: View {
    let imageName: String
    let text: String
    
    var body: some View {
        VStack {
            ZStack {
                // Background container with the color #282b30
                Color(hex: "#282B30")  // Container color set here
                    .cornerRadius(8)
                    .shadow(radius: 5)  // Optional: Add shadow for a more polished look
                    .frame(height: 180)  // Reduce height of the container
                
                // Card image and content
                VStack {
                    Image(imageName)
                        .resizable()
                        .scaledToFill()
                        .frame(width: 150, height: 120)  // Smaller image size
                        .cornerRadius(8)
                        .clipped()
                    
                    Text(text)
                        .font(.subheadline)
                        .foregroundColor(.white)
                        .padding(.top, 8)
                        .padding(.horizontal, 16)  // Smaller horizontal padding
                    
                    Button(action: {
                        // Action for "Learn More" button here
                    }) {
                        Text("Learn More")
                            .fontWeight(.semibold)
                            .padding(8)  // Smaller padding for the button
                            .frame(maxWidth: .infinity)
                            .background(Color(hex: "#BCFF5E"))
                            .foregroundColor(.black)
                            .cornerRadius(8)
                    }
                    .padding([.leading, .trailing], 16)  // Smaller horizontal padding
                }
                .padding()  // Padding to ensure that content inside has space
            }
            .padding([.leading, .trailing])  // Outer padding to prevent edges touching the container
        }
        .background(Color(UIColor.systemGray4))  // Set background of the entire card container
        .cornerRadius(8)  // Rounded corners for the container
        .padding(.horizontal)  // Ensure it has space from the edges
    }
}

struct WardrobeButton: View {
    var body: some View {
        Button(action: {
            // Action for wardrobe button
        }) {
            Image(systemName: "wardrobe")
                .resizable()
                .scaledToFit()
                .frame(width: 60, height: 60)
                .background(Color(hex: "#BCFF5E"))
                .clipShape(Circle())
                .foregroundColor(.white)
                .padding()
        }
    }
}

struct HomeScreenView_Previews: PreviewProvider {
    static var previews: some View {
        HomePageView()
    }
}
