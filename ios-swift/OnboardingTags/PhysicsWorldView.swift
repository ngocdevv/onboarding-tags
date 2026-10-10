import SpriteKit
import SwiftUI

/// Port of `src/components/PhysicsWorld.tsx`.
///
/// The original drives matter-js and paints every body with Skia on each
/// frame. Here SpriteKit owns both the simulation and the rendering
/// (`PhysicsScene`), and SwiftUI layers the progress bar + header on top.
struct PhysicsWorldView: View {
    @State private var scene: PhysicsScene = {
        let scene = PhysicsScene()
        scene.scaleMode = .resizeFill
        scene.backgroundColor = .clear
        return scene
    }()

    var body: some View {
        ZStack(alignment: .top) {
            // Canvas: absoluteFill with `bottom: -12`, extends under the status bar.
            SpriteView(scene: scene, options: [.allowsTransparency])
                .padding(.bottom, -12)
                .ignoresSafeArea(edges: .top)

            // Content Overlay (pointerEvents="box-none")
            VStack(alignment: .leading, spacing: 0) {
                // Progress Bar
                HStack(spacing: 6) {
                    ForEach(0..<5, id: \.self) { index in
                        RoundedRectangle(cornerRadius: 3)
                            .fill(index == 0 ? Color(hex: 0x1A1A1A) : Color(hex: 0xF0F0F0))
                            .frame(height: 6)
                            .frame(maxWidth: .infinity)
                    }
                }
                .padding(.bottom, 32)
                .entering(.fadeInUp, delay: 0.3, duration: 0.8)

                // Header
                VStack(alignment: .leading, spacing: 12) {
                    Text("Build Around\nWhat You Love 💡")
                        .font(.system(size: 28, weight: .heavy))
                        .kerning(-0.5)
                        .foregroundStyle(Color(hex: 0x1A1A1A))
                        .entering(.fadeInDown, delay: 0.5, duration: 0.8)

                    Text("Choosing activities helps you track\nprogress and stay motivated every day")
                        .font(.system(size: 15))
                        .lineSpacing(4)
                        .foregroundStyle(Color(hex: 0x666666))
                        .entering(.fadeInDown, delay: 0.6, duration: 0.8)
                }
                .padding(.bottom, 24)
            }
            .padding(.top, 10)
            .frame(maxWidth: .infinity, alignment: .leading)
            .allowsHitTesting(false)
        }
    }
}

#Preview {
    PhysicsWorldView()
        .padding(.horizontal, 24)
}
