import SwiftUI

/// Port of `src/app/_layout.tsx`.
/// Drives the onboarding flow: 0 = Welcome, 1 = Tags, 2 = Tabs.
struct RootView: View {
    @State private var onboardingStep = 0

    var body: some View {
        ZStack {
            switch onboardingStep {
            case 0:
                WelcomeScreen(onContinue: handleWelcomeContinue)
            case 1:
                // The original layout passes an empty handler here, so the
                // Tags screen's Continue button is intentionally a no-op.
                TagsScreen(onContinue: {})
            default:
                AppTabsView()
            }

            AnimatedSplashOverlay()
        }
        .preferredColorScheme(.light)
    }

    private func handleWelcomeContinue() {
        onboardingStep = 1
    }

    // Kept for parity with the original `handleTagsContinue`.
    private func handleTagsContinue() {
        onboardingStep = 2
    }
}

#Preview {
    RootView()
}
