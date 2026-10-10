import SwiftUI

/// SwiftUI stand-in for react-native-reanimated's `entering` layout animations
/// (`FadeInDown`, `FadeInUp`, `ZoomIn`) used across the onboarding screens.
enum EnteringAnimation {
    case fadeInDown
    case fadeInUp
    case zoomIn
}

private struct EnteringModifier: ViewModifier {
    let animation: EnteringAnimation
    let delay: Double
    let duration: Double
    let springify: Bool

    @State private var shown = false

    func body(content: Content) -> some View {
        content
            .opacity(opacity)
            .offset(y: shown ? 0 : initialOffset)
            .scaleEffect(shown ? 1 : initialScale)
            .onAppear {
                guard !shown else { return }
                let base: Animation = springify
                    ? .spring(duration: duration, bounce: 0.25)
                    : .easeOut(duration: duration)
                withAnimation(base.delay(delay)) {
                    shown = true
                }
            }
    }

    private var opacity: Double {
        // Reanimated's ZoomIn only animates scale, not opacity.
        if animation == .zoomIn { return 1 }
        return shown ? 1 : 0
    }

    private var initialOffset: CGFloat {
        switch animation {
        case .fadeInDown: return -25
        case .fadeInUp: return 25
        case .zoomIn: return 0
        }
    }

    private var initialScale: CGFloat {
        animation == .zoomIn ? 0.001 : 1
    }
}

extension View {
    /// Mirrors `entering={FadeInDown.delay(ms).duration(ms).springify()}`.
    func entering(
        _ animation: EnteringAnimation,
        delay: Double = 0,
        duration: Double = 0.8,
        springify: Bool = true
    ) -> some View {
        modifier(EnteringModifier(animation: animation, delay: delay, duration: duration, springify: springify))
    }
}
