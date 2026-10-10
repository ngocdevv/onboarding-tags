import SwiftUI

/// Port of `src/components/animated-icon.tsx` (`AnimatedSplashOverlay`).
///
/// Keyframes from the original (600 ms total):
///   0%   scale = screenHeight / 90, opacity 1
///   20%  opacity 1
///   70%  opacity 0 (elastic easing)
///   100% scale = 1, opacity 0
struct AnimatedSplashOverlay: View {
    private static let duration: Double = 0.6
    private static let initialScaleFactor = UIScreen.main.bounds.height / 90

    @State private var visible = true
    @State private var scale: CGFloat = AnimatedSplashOverlay.initialScaleFactor
    @State private var opacity: Double = 1

    var body: some View {
        if visible {
            Color(hex: 0x208AEF)
                .ignoresSafeArea()
                .scaleEffect(scale)
                .opacity(opacity)
                .zIndex(1000)
                .allowsHitTesting(false)
                .onAppear(perform: run)
        }
    }

    private func run() {
        let total = Self.duration
        withAnimation(.easeInOut(duration: total)) {
            scale = 1
        }
        // Hold full opacity until 20%, then fade out by 70%.
        withAnimation(.easeOut(duration: total * 0.5).delay(total * 0.2)) {
            opacity = 0
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + total + 0.05) {
            visible = false
        }
    }
}
