import SwiftUI

/// Port of `src/components/app-tabs.tsx` (expo-router NativeTabs).
struct AppTabsView: View {
    var body: some View {
        TabView {
            HomeScreen()
                .tabItem { Label("Home", image: "tab-home") }

            ExploreScreen()
                .tabItem { Label("Explore", image: "tab-explore") }
        }
    }
}

/// Port of `src/app/index.tsx`.
struct HomeScreen: View {
    var body: some View {
        PhysicsWorldView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.white.ignoresSafeArea())
    }
}

#Preview {
    AppTabsView()
}
