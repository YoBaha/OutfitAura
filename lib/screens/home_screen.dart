import 'package:flutter/material.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[200],
      body: Stack(
        children: [
          // Gradient Background
          Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [Colors.white, Color(0xFFEFEFEF)],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
              ),
            ),
          ),
          SafeArea(
            child: SingleChildScrollView(
              child: Column(
                children: [
                  // Header
                  Padding(
                    padding: const EdgeInsets.only(top: 20.0),
                    child: Text(
                      'Outfitaura',
                      style: TextStyle(
                        fontSize: 36,
                        fontWeight: FontWeight.bold,
                        foreground: Paint()
                          ..shader = const LinearGradient(
                            colors: [Color(0xFFBCFF5E), Color(0xFF282b30)],
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                          ).createShader(Rect.fromLTWH(0.0, 0.0, 200.0, 70.0)),
                      ),
                    ),
                  ),

                  const SizedBox(height: 20),

                  // Search Bar
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Container(
                      height: 50,
                      decoration: BoxDecoration(
                        color: Color(0xFF282B30),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Row(
                        children: [
                          const Padding(
                            padding: EdgeInsets.only(left: 12.0),
                            child: Icon(Icons.search, color: Colors.white),
                          ),
                          Expanded(
                            child: TextField(
                              decoration: InputDecoration(
                                hintText: 'Search...',
                                hintStyle: TextStyle(color: Colors.white),
                                border: InputBorder.none,
                                contentPadding:
                                    EdgeInsets.symmetric(horizontal: 16.0),
                              ),
                              style: TextStyle(color: Colors.white),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),

                  const SizedBox(height: 10),

                  // Marketing Cards
                  MarketingCard(
                      imageName: 'assets/card.jpg',
                      text: 'Discover our latest features and tools!'),
                  MarketingCard(
                      imageName: 'assets/card2.jpg',
                      text: 'Explore new possibilities with our services!'),

                  const SizedBox(height: 20),

                  // Our Models
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: Text(
                        'Our Models',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 10),

                  // Grid of Model Images
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: GridView.builder(
                      shrinkWrap: true,
                      physics: NeverScrollableScrollPhysics(),
                      gridDelegate:
                          const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        crossAxisSpacing: 16,
                        mainAxisSpacing: 16,
                      ),
                      itemCount: 4,
                      itemBuilder: (context, index) {
                        return ClipRRect(
                          borderRadius: BorderRadius.circular(8),
                          child: Image.asset(
                            'assets/model${index + 1}.jpg',
                            fit: BoxFit.cover,
                          ),
                        );
                      },
                    ),
                  ),

                  const SizedBox(height: 20),

                  // Upload Image Button
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Color(0xFFBCFF5E),
                        padding: EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10),
                        ),
                      ),
                      onPressed: () {},
                      child: const Text(
                        'Upload Image',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 120),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class MarketingCard extends StatelessWidget {
  final String imageName;
  final String text;

  const MarketingCard({required this.imageName, required this.text});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
      child: Container(
        decoration: BoxDecoration(
          color: Color(0xFF282B30),
          borderRadius: BorderRadius.circular(8),
          boxShadow: [BoxShadow(blurRadius: 5, color: Colors.black26)],
        ),
        child: Column(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: Image.asset(
                imageName,
                width: 150,
                height: 120,
                fit: BoxFit.cover,
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(
                text,
                style: TextStyle(color: Colors.white, fontSize: 14),
                textAlign: TextAlign.center,
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Color(0xFFBCFF5E),
                  padding: EdgeInsets.symmetric(vertical: 8),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                onPressed: () {},
                child: const Text(
                  'Learn More',
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: Colors.black,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
