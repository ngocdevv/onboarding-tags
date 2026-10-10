# OnboardingTags — Android (Kotlin / Jetpack Compose)

Native Kotlin port of the Expo / React Native `onboarding-tags` app in the repo root.

## Requirements

- Android Studio (Ladybug or newer) or JDK 17+ with the Android SDK (`compileSdk 36`)
- minSdk 26

## Run

Open the `android-kotlin` folder in Android Studio and press Run, or from the terminal:

```sh
./gradlew :app:installDebug
```

`local.properties` (gitignored) must point `sdk.dir` at your Android SDK; Android Studio creates it automatically.

## How the RN pieces map to Kotlin

| React Native (TypeScript)                         | Kotlin                                                                   |
| ------------------------------------------------- | ------------------------------------------------------------------------ |
| `src/app/_layout.tsx` (onboarding step state)     | `RootScreen.kt`                                                          |
| `src/components/animated-icon.tsx` splash overlay | `ui/components/AnimatedSplashOverlay.kt`                                 |
| `src/components/WelcomeScreen.tsx`                | `screens/WelcomeScreen.kt`                                               |
| `src/components/TagsScreen.tsx`                   | `screens/TagsScreen.kt`                                                  |
| `src/components/PhysicsWorld.tsx` (matter-js + Skia) | `physics/PhysicsWorld.kt` (Canvas + overlay) + `physics/PhysicsEngine.kt` (JBox2D) |
| `src/constants/Tags.ts`                           | `physics/Tags.kt`                                                        |
| `src/constants/theme.ts`                          | `ui/theme/Theme.kt`                                                      |
| `src/components/app-tabs.tsx`, `src/app/index.tsx` | `screens/AppTabs.kt`                                                    |
| `src/app/explore.tsx` + `ui/collapsible.tsx`      | `screens/ExploreScreen.kt`                                               |
| reanimated `FadeInDown / FadeInUp / ZoomIn`       | `ui/components/Entering.kt` (`Modifier.entering(...)`)                   |
| `expo-splash-screen` config                       | `res/values-v31/themes.xml` (`windowSplashScreenBackground` / icon)      |
| `app.json` adaptive icon                          | `res/mipmap-anydpi-v26/ic_launcher.xml`                                  |

### Physics

matter-js is replaced by **JBox2D** (`org.jbox2d:jbox2d-library`), a Java port of Box2D.
`PhysicsEngine` converts between pixels and meters (1 m = 100 dp) and keeps the original tuning:

| matter-js                     | JBox2D                          |
| ----------------------------- | ------------------------------- |
| `gravity.y = 0.5`             | `gravity = (0, 5)` m/s²         |
| `frictionAir = 0.02` per step | `linearDamping = 1.2`           |
| `restitution 0.6`, `friction 0.5` | same                        |
| `chamfer { radius: 22 }`      | convex octagon `PolygonShape`   |
| `applyForce { y: -0.05 }`     | `applyLinearImpulse(mass × 1.9)`|
| `Engine.update(1000/60)` per rAF | `world.step(1/60)` per `withFrameNanos` |

Tap detection iterates fixtures with `Fixture.testPoint`, the Box2D equivalent of `Matter.Query.point`.

### Icons

The original draws MaterialCommunityIcons / Ionicons glyphs. The Kotlin port uses the closest icons from
`material-icons-extended`, drawn onto the Canvas with `rememberVectorPainter`. The mapping lives in
`physics/Tags.kt` and `screens/WelcomeScreen.kt`.

## Behavior notes

- As in the original `_layout.tsx`, the Tags screen's **Continue** button is wired to an empty handler, so step 2 (the Home/Explore tabs) is present but not reached from the UI.
- `rocket.png` in the RN assets is actually JPEG data; it is stored as `drawable-nodpi/rocket.jpg`.
