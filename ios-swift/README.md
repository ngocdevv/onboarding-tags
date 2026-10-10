# OnboardingTags — iOS (Swift / SwiftUI)

Native Swift port of the Expo / React Native `onboarding-tags` app in the repo root.

## Requirements

- Xcode 16 or newer (the project uses Xcode's synchronized folders, so new files added under `OnboardingTags/` are picked up automatically)
- iOS 17.0+

## Run

```sh
open OnboardingTags.xcodeproj
```

Pick an iPhone simulator and press ⌘R. No third-party dependencies, no package install step.

## How the RN pieces map to Swift

| React Native (TypeScript)                         | Swift                                                       |
| ------------------------------------------------- | ----------------------------------------------------------- |
| `src/app/_layout.tsx` (onboarding step state)     | `RootView.swift`                                            |
| `src/components/animated-icon.tsx` splash overlay | `AnimatedSplashOverlay.swift`                               |
| `src/components/WelcomeScreen.tsx`                | `WelcomeScreen.swift`                                       |
| `src/components/TagsScreen.tsx`                   | `TagsScreen.swift`                                          |
| `src/components/PhysicsWorld.tsx` (matter-js + Skia) | `PhysicsWorldView.swift` (overlay) + `PhysicsScene.swift` (SpriteKit) |
| `src/constants/Tags.ts`                           | `Tags.swift`                                                |
| `src/constants/theme.ts`                          | `Theme.swift`                                               |
| `src/components/app-tabs.tsx`, `src/app/index.tsx` | `AppTabsView.swift`                                        |
| `src/app/explore.tsx` + `ui/collapsible.tsx`      | `ExploreScreen.swift`                                       |
| reanimated `FadeInDown / FadeInUp / ZoomIn`       | `EnteringAnimation.swift` (`.entering(...)` modifier)       |
| `expo-splash-screen` config                       | `Info.plist` `UILaunchScreen` + `LaunchBackground` color set |

### Physics

matter-js has no native equivalent, so the simulation runs in **SpriteKit** (`SKPhysicsWorld`), which is bundled with iOS.
Tuning was converted from matter-js units:

| matter-js                     | SpriteKit (150 pt = 1 m)        |
| ----------------------------- | ------------------------------- |
| `gravity.y = 0.5`             | `gravity.dy = -3.33`            |
| `frictionAir = 0.02` per step | `linearDamping = 1.2`           |
| `restitution 0.6`, `friction 0.5` | same                        |
| `chamfer { radius: 22 }`      | convex octagon physics body     |
| `applyForce { y: -0.05 }`     | `applyImpulse(mass × 190)` (scene pt/s; approximate kick) |

Tap detection uses `physicsWorld.body(at:)`, the SpriteKit equivalent of `Matter.Query.point`.

### Icons

The original draws MaterialCommunityIcons / Ionicons glyphs. The Swift port uses the closest **SF Symbols** instead,
so no font files need to be bundled. The mapping lives in `Tags.swift` and `WelcomeScreen.swift`.

## Behavior notes

- As in the original `_layout.tsx`, the Tags screen's **Continue** button is wired to an empty handler, so step 2 (the Home/Explore tabs) is present but not reached from the UI.
- `rocket.png` in the RN assets is actually JPEG data; it is stored as `rocket.jpg` in the asset catalog.
