import SwiftUI
import UIKit

/// Port of `src/constants/theme.ts`.
enum Theme {
    struct Palette {
        let text: Color
        let background: Color
        let backgroundElement: Color
        let backgroundSelected: Color
        let textSecondary: Color
    }

    static let light = Palette(
        text: Color(hex: 0x000000),
        background: Color(hex: 0xFFFFFF),
        backgroundElement: Color(hex: 0xF0F0F3),
        backgroundSelected: Color(hex: 0xE0E1E6),
        textSecondary: Color(hex: 0x60646C)
    )

    static let dark = Palette(
        text: Color(hex: 0xFFFFFF),
        background: Color(hex: 0x000000),
        backgroundElement: Color(hex: 0x212225),
        backgroundSelected: Color(hex: 0x2E3135),
        textSecondary: Color(hex: 0xB0B4BA)
    )

    static func palette(for scheme: ColorScheme) -> Palette {
        scheme == .dark ? dark : light
    }

    enum Spacing {
        static let half: CGFloat = 2
        static let one: CGFloat = 4
        static let two: CGFloat = 8
        static let three: CGFloat = 16
        static let four: CGFloat = 24
        static let five: CGFloat = 32
        static let six: CGFloat = 64
    }

    static let bottomTabInset: CGFloat = 50
    static let maxContentWidth: CGFloat = 800
}

extension Color {
    init(hex: UInt32, opacity: Double = 1) {
        let r = Double((hex >> 16) & 0xFF) / 255
        let g = Double((hex >> 8) & 0xFF) / 255
        let b = Double(hex & 0xFF) / 255
        self.init(.sRGB, red: r, green: g, blue: b, opacity: opacity)
    }
}

extension UIColor {
    convenience init(hex: UInt32, alpha: CGFloat = 1) {
        let r = CGFloat((hex >> 16) & 0xFF) / 255
        let g = CGFloat((hex >> 8) & 0xFF) / 255
        let b = CGFloat(hex & 0xFF) / 255
        self.init(red: r, green: g, blue: b, alpha: alpha)
    }
}
