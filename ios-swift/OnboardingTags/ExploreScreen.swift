import SwiftUI

/// Port of `src/app/explore.tsx`.
struct ExploreScreen: View {
    @Environment(\.colorScheme) private var colorScheme

    private var theme: Theme.Palette { Theme.palette(for: colorScheme) }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                // Title
                VStack(spacing: Theme.Spacing.three) {
                    Text("Explore")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundStyle(theme.text)

                    Text("This starter app includes example\ncode to help you get started.")
                        .multilineTextAlignment(.center)
                        .foregroundStyle(theme.textSecondary)

                    Link(destination: URL(string: "https://docs.expo.dev")!) {
                        HStack(spacing: Theme.Spacing.one) {
                            Text("Expo documentation")
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundStyle(theme.text)
                            Image(systemName: "arrow.up.right.square")
                                .font(.system(size: 12))
                                .foregroundStyle(theme.text)
                        }
                        .padding(.horizontal, Theme.Spacing.four)
                        .padding(.vertical, Theme.Spacing.two)
                        .background(theme.backgroundElement, in: Capsule())
                    }
                    .buttonStyle(PressOpacityButtonStyle())
                }
                .padding(.horizontal, Theme.Spacing.four)
                .padding(.vertical, Theme.Spacing.six)

                // Sections
                VStack(alignment: .leading, spacing: Theme.Spacing.five) {
                    Collapsible(title: "File-based routing") {
                        Text("This app has two screens: ") + code("src/app/index.tsx") + Text(" and ") + code("src/app/explore.tsx")
                        Text("The layout file in ") + code("src/app/_layout.tsx") + Text(" sets up the tab navigator.")
                        learnMore("https://docs.expo.dev/router/introduction")
                    }

                    Collapsible(title: "Android, iOS, and web support") {
                        VStack(spacing: Theme.Spacing.two) {
                            Text("You can open this project on Android, iOS, and the web. To open the web version, press ")
                                + Text("w").bold()
                                + Text(" in the terminal running this project.")
                            Image("tutorial-web")
                                .resizable()
                                .aspectRatio(296.0 / 171.0, contentMode: .fit)
                                .clipShape(RoundedRectangle(cornerRadius: Theme.Spacing.three))
                        }
                        .padding(Theme.Spacing.three)
                        .background(theme.backgroundElement, in: RoundedRectangle(cornerRadius: Theme.Spacing.three))
                    }

                    Collapsible(title: "Images") {
                        Text("For static images, you can use the ") + code("@2x") + Text(" and ") + code("@3x")
                            + Text(" suffixes to provide files for different screen densities.")
                        Image("react-logo")
                            .frame(width: 100, height: 100)
                            .frame(maxWidth: .infinity)
                        learnMore("https://reactnative.dev/docs/images")
                    }

                    Collapsible(title: "Light and dark mode components") {
                        Text("This template has light and dark mode support. The ") + code("useColorScheme()")
                            + Text(" hook lets you inspect what the user's current color scheme is, and so you can adjust UI colors accordingly.")
                        learnMore("https://docs.expo.dev/develop/user-interface/color-themes/")
                    }

                    Collapsible(title: "Animations") {
                        Text("This template includes an example of an animated component. The ")
                            + code("src/components/ui/collapsible.tsx")
                            + Text(" component uses the powerful ") + code("react-native-reanimated")
                            + Text(" library to animate opening this hint.")
                    }
                }
                .padding(.horizontal, Theme.Spacing.four)
                .padding(.top, Theme.Spacing.three)
                .padding(.bottom, Theme.bottomTabInset + Theme.Spacing.three)
            }
            .frame(maxWidth: Theme.maxContentWidth)
            .frame(maxWidth: .infinity)
        }
        .background(theme.background.ignoresSafeArea())
    }

    private func code(_ text: String) -> Text {
        Text(text).font(.system(size: 14, design: .monospaced))
    }

    private func learnMore(_ url: String) -> some View {
        Link("Learn more", destination: URL(string: url)!)
            .font(.system(size: 14, weight: .semibold))
            .foregroundStyle(Color(hex: 0x208AEF))
    }
}

/// Port of `src/components/ui/collapsible.tsx`.
private struct Collapsible<Content: View>: View {
    let title: String
    @ViewBuilder let content: Content

    @Environment(\.colorScheme) private var colorScheme
    @State private var isOpen = false

    var body: some View {
        VStack(alignment: .leading, spacing: Theme.Spacing.two) {
            Button {
                withAnimation(.easeInOut(duration: 0.25)) { isOpen.toggle() }
            } label: {
                HStack(spacing: Theme.Spacing.two) {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .semibold))
                        .rotationEffect(.degrees(isOpen ? 90 : 0))
                    Text(title)
                        .font(.system(size: 16, weight: .semibold))
                }
                .foregroundStyle(Theme.palette(for: colorScheme).text)
            }
            .buttonStyle(PressOpacityButtonStyle())

            if isOpen {
                VStack(alignment: .leading, spacing: Theme.Spacing.two) {
                    content
                }
                .font(.system(size: 14))
                .foregroundStyle(Theme.palette(for: colorScheme).text)
                .padding(.leading, Theme.Spacing.four)
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
    }
}

struct PressOpacityButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label.opacity(configuration.isPressed ? 0.7 : 1)
    }
}

#Preview {
    ExploreScreen()
}
