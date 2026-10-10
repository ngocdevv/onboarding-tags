import SwiftUI

/// Port of `src/components/TagsScreen.tsx`.
struct TagsScreen: View {
    let onContinue: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            PhysicsWorldView()
                .padding(.horizontal, 24)

            ContinueButton(action: onContinue)
                .padding(.horizontal, 24)
                .padding(.top, 16)
                .padding(.bottom, 16)
                .entering(.fadeInDown, delay: 0.6, duration: 0.8)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.white.ignoresSafeArea())
    }
}

#Preview {
    TagsScreen(onContinue: {})
}
