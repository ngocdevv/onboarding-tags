import SpriteKit
import UIKit

// Physics / layout configuration (mirrors PhysicsWorld.tsx constants)
private let TAG_HEIGHT: CGFloat = 44
private let TAG_RADIUS: CGFloat = 22
private let GAP: CGFloat = 2
// Layout: [padding 12] [icon 16] [gap 4] [label] [padding 16]
private let ICON_AREA: CGFloat = 32
private let H_PADDING: CGFloat = 16
private let MIN_TAG_WIDTH: CGFloat = 80
private let ICON_SIZE: CGFloat = 16

// matter-js tuning converted to SpriteKit units (150 pt = 1 m for gravity).
//   gravity.y = 0.5  →  500 pt/s²  →  3.33 m/s²
//   frictionAir 0.02 per 60 Hz step  →  linear damping ≈ 1.2 /s
//   applyForce {y: -0.05}  →  approximately 190 pt/s upward kick.
//   applyImpulse(mass × velocity) uses scene points/s; do not divide by 150.
private let GRAVITY: CGFloat = -3.33
private let AIR_DAMPING: CGFloat = 1.2
private let TAP_KICK_VELOCITY: CGFloat = 190

/// One falling tag: rounded pill, icon and label, plus its physics body.
final class TagNode: SKNode {
    let tag: Tag
    private let shape: SKShapeNode

    var isSelected = false {
        didSet { shape.lineWidth = isSelected ? 2 : 0 }
    }

    init(tag: Tag, width: CGFloat, font: UIFont) {
        self.tag = tag
        self.shape = SKShapeNode(
            rect: CGRect(x: -width / 2, y: -TAG_HEIGHT / 2, width: width, height: TAG_HEIGHT),
            cornerRadius: TAG_RADIUS
        )
        super.init()

        shape.fillColor = UIColor(hex: tag.colorHex)
        shape.strokeColor = .black
        shape.lineWidth = 0
        shape.isAntialiased = true
        addChild(shape)

        let leftEdge = -width / 2

        // Icon (SF Symbol in place of the MaterialCommunityIcons glyph)
        let config = UIImage.SymbolConfiguration(pointSize: ICON_SIZE, weight: .regular)
        if let image = UIImage(systemName: tag.symbol, withConfiguration: config)?
            .withTintColor(UIColor(hex: 0x555555), renderingMode: .alwaysOriginal) {
            let sprite = SKSpriteNode(texture: SKTexture(image: image))
            let scale = ICON_SIZE / max(image.size.width, image.size.height)
            sprite.size = CGSize(width: image.size.width * scale, height: image.size.height * scale)
            sprite.position = CGPoint(x: leftEdge + 12 + ICON_SIZE / 2, y: 0)
            addChild(sprite)
        }

        // Label
        let label = SKLabelNode(fontNamed: font.fontName)
        label.text = tag.label
        label.fontSize = font.pointSize
        label.fontColor = UIColor(hex: 0x333333)
        label.horizontalAlignmentMode = .left
        label.verticalAlignmentMode = .center
        label.position = CGPoint(x: leftEdge + ICON_AREA, y: 0)
        addChild(label)

        // Body: chamfered rectangle, slightly larger than the drawn pill (GAP).
        let body = SKPhysicsBody(polygonFrom: Self.chamferedRectPath(
            width: width + GAP, height: TAG_HEIGHT + GAP, cut: TAG_RADIUS * 0.6
        ))
        body.restitution = 0.6
        body.friction = 0.5
        body.linearDamping = AIR_DAMPING
        body.angularDamping = AIR_DAMPING
        body.allowsRotation = true
        physicsBody = body
    }

    @available(*, unavailable)
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) is not supported")
    }

    /// Convex octagon approximating `chamfer: { radius }` from matter-js.
    private static func chamferedRectPath(width: CGFloat, height: CGFloat, cut: CGFloat) -> CGPath {
        let hx = width / 2
        let hy = height / 2
        let c = min(cut, min(hx, hy))
        // Counter-clockwise winding, as SpriteKit requires.
        let points = [
            CGPoint(x: hx - c, y: -hy),
            CGPoint(x: hx, y: -hy + c),
            CGPoint(x: hx, y: hy - c),
            CGPoint(x: hx - c, y: hy),
            CGPoint(x: -hx + c, y: hy),
            CGPoint(x: -hx, y: hy - c),
            CGPoint(x: -hx, y: -hy + c),
            CGPoint(x: -hx + c, y: -hy),
        ]
        let path = CGMutablePath()
        path.addLines(between: points)
        path.closeSubpath()
        return path
    }
}

/// SpriteKit replacement for the matter-js engine + Skia canvas.
final class PhysicsScene: SKScene {
    private(set) var selectedTagIds = Set<String>()
    private var built = false

    private let labelFont: UIFont = UIFont(name: "HelveticaNeue-Bold", size: 14)
        ?? .boldSystemFont(ofSize: 14)

    override func didMove(to view: SKView) {
        super.didMove(to: view)
        anchorPoint = .zero
        backgroundColor = .clear
        view.allowsTransparency = true
    }

    /// The world is built on the first simulation frame rather than in
    /// `didMove`/`didChangeSize`: SwiftUI's `SpriteView` lays the SKView out
    /// with a placeholder frame first, and `.resizeFill` would otherwise hand
    /// us that interim size.
    override func update(_ currentTime: TimeInterval) {
        super.update(currentTime)
        buildWorldIfNeeded()
    }

    private func buildWorldIfNeeded() {
        guard !built,
              let view,
              size == view.bounds.size,
              size.width >= 100, size.height >= 100 else { return }
        built = true

        let width = size.width
        let height = size.height

        physicsWorld.gravity = CGVector(dx: 0, dy: GRAVITY)

        // Ground + walls (SpriteKit y axis points up; the ground sits at y = 0).
        let wallThickness: CGFloat = 100
        addStatic(center: CGPoint(x: width / 2, y: -50), size: CGSize(width: width, height: 100))
        addStatic(center: CGPoint(x: -wallThickness / 2, y: height / 2),
                  size: CGSize(width: wallThickness, height: height * 2))
        addStatic(center: CGPoint(x: width + wallThickness / 2, y: height / 2),
                  size: CGSize(width: wallThickness, height: height * 2))

        for (index, tag) in TAGS.enumerated() {
            // Dynamic width based on label text
            let textWidth = (tag.label as NSString)
                .size(withAttributes: [.font: labelFont]).width
            let tagWidth = max(MIN_TAG_WIDTH, ICON_AREA + ceil(textWidth) + H_PADDING)

            let node = TagNode(tag: tag, width: tagWidth, font: labelFont)
            let x = CGFloat.random(in: 0...(max(0, width - tagWidth))) + tagWidth / 2
            let y = height + 100 + CGFloat(index) * 60 // start falling from above
            node.position = CGPoint(x: x, y: y)
            node.zRotation = CGFloat.random(in: -0.25...0.25)
            addChild(node)
        }
    }

    private func addStatic(center: CGPoint, size: CGSize) {
        let node = SKNode()
        node.position = center
        let body = SKPhysicsBody(rectangleOf: size)
        body.isDynamic = false
        body.friction = 0.5
        node.physicsBody = body
        addChild(node)
    }

    // MARK: - Tap handling (Gesture.Tap + Matter.Query.point)

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard let touch = touches.first else { return }
        let point = touch.location(in: self)

        guard let body = physicsWorld.body(at: point),
              let node = body.node as? TagNode else { return }

        if selectedTagIds.contains(node.tag.id) {
            selectedTagIds.remove(node.tag.id)
        } else {
            selectedTagIds.insert(node.tag.id)
        }
        node.isSelected = selectedTagIds.contains(node.tag.id)

        // Matter.Body.applyForce(body, position, { x: 0, y: -0.05 })
        body.applyImpulse(CGVector(dx: 0, dy: body.mass * TAP_KICK_VELOCITY))
    }
}
