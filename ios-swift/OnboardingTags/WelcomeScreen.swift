import SwiftUI

/// Port of `src/components/WelcomeScreen.tsx`.
struct WelcomeScreen: View {
    let onContinue: () -> Void

    private struct Feature {
        let symbol: String
        let color: UInt32
        let iconColor: UInt32
        let title: String
        let description: String
    }

    // Icon families from the original (Ionicons / MaterialCommunityIcons)
    // are mapped to SF Symbols.
    private static let features: [Feature] = [
        Feature(symbol: "magnifyingglass", color: 0xE8F0FE, iconColor: 0x4A90D9,
                title: "Find new activities",
                description: "Discover fresh ideas that spark your interest"),
        Feature(symbol: "leaf.fill", color: 0xE8F5E9, iconColor: 0x4CAF50,
                title: "Stay calm and mindful",
                description: "Find new activities that spark your interest"),
        Feature(symbol: "lightbulb", color: 0xFFF3E0, iconColor: 0xFF9800,
                title: "Boost your creativity",
                description: "Stay calm and mindful while exploring habits"),
        Feature(symbol: "trophy", color: 0xF3E5F5, iconColor: 0x9C27B0,
                title: "Build lasting routines",
                description: "Boost your creativity with daily inspiration"),
    ]

    var body: some View {
        VStack(spacing: 0) {
            // Rocket Illustration
            Image("rocket")
                .resizable()
                .scaledToFit()
                .frame(width: 200, height: 200)
                .entering(.zoomIn, duration: 1.0, springify: false)
                .frame(maxWidth: .infinity, maxHeight: 260)

            // Feature List
            VStack(alignment: .leading, spacing: 28) {
                ForEach(Array(Self.features.enumerated()), id: \.offset) { index, feature in
                    HStack(alignment: .top, spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(Color(hex: feature.color))
                                .frame(width: 44, height: 44)
                            Image(systemName: feature.symbol)
                                .font(.system(size: 20))
                                .foregroundStyle(Color(hex: feature.iconColor))
                        }
                        .padding(.top, 2)

                        VStack(alignment: .leading, spacing: 4) {
                            Text(feature.title)
                                .font(.system(size: 17, weight: .bold))
                                .kerning(-0.2)
                                .foregroundStyle(Color(hex: 0x1A1A1A))
                            Text(feature.description)
                                .font(.system(size: 14))
                                .lineSpacing(3)
                                .foregroundStyle(Color(hex: 0x888888))
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .entering(.fadeInDown, delay: 0.3 + Double(index) * 0.2, duration: 0.8)
                }
            }
            .frame(maxHeight: .infinity)

            // Continue Button
            ContinueButton(action: onContinue)
                .padding(.top, 16)
                .entering(.fadeInUp, delay: 1.4, duration: 0.8)
        }
        .padding(.horizontal, 24)
        .padding(.top, 20)
        .padding(.bottom, 16)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.white.ignoresSafeArea())
    }
}

#Preview {
    WelcomeScreen(onContinue: {})
}
