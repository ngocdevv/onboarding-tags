import Foundation

/// Port of `src/constants/Tags.ts`.
/// The original renders MaterialCommunityIcons glyphs through Skia;
/// here each tag maps to the closest SF Symbol instead.
struct Tag: Identifiable, Hashable {
    let id: String
    let label: String
    let colorHex: UInt32
    let symbol: String
}

let TAGS: [Tag] = [
    Tag(id: "1",  label: "Code",     colorHex: 0xFFE4E1, symbol: "chevron.left.forwardslash.chevron.right"),
    Tag(id: "2",  label: "Design",   colorHex: 0xFFFACD, symbol: "paintbrush"),
    Tag(id: "3",  label: "Music",    colorHex: 0xE0FFFF, symbol: "music.note"),
    Tag(id: "4",  label: "Travel",   colorHex: 0xF0E68C, symbol: "airplane"),
    Tag(id: "5",  label: "Finance",  colorHex: 0xDDA0DD, symbol: "wallet.pass"),
    Tag(id: "6",  label: "Cook",     colorHex: 0x90EE90, symbol: "fork.knife"),
    Tag(id: "7",  label: "Meditate", colorHex: 0xADD8E6, symbol: "figure.mind.and.body"),
    Tag(id: "8",  label: "Work",     colorHex: 0xFFB6C1, symbol: "laptopcomputer"),
    Tag(id: "9",  label: "Learn",    colorHex: 0xFAFAD2, symbol: "book"),
    Tag(id: "10", label: "Exercise", colorHex: 0x87CEFA, symbol: "figure.run"),
    Tag(id: "11", label: "Photo",    colorHex: 0xD3D3D3, symbol: "camera"),
    Tag(id: "12", label: "Garden",   colorHex: 0x98FB98, symbol: "camera.macro"),
    Tag(id: "13", label: "Explore",  colorHex: 0xAFEEEE, symbol: "safari"),
    Tag(id: "14", label: "Write",    colorHex: 0xFFDEAD, symbol: "pencil"),
    Tag(id: "15", label: "Research", colorHex: 0xE6E6FA, symbol: "flask"),
]
