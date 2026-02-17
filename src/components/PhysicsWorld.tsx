import React, { useEffect, useState, useMemo } from 'react';
import { useWindowDimensions, Platform } from 'react-native';
import { Canvas, Group, RoundedRect, Text, matchFont, vec, LinearGradient, Shadow } from '@shopify/react-native-skia';
import Matter from 'matter-js';
import { TAGS } from '../constants/Tags';

// Physics Configuration
const ENGINE_UPDATE_DELTA = 1000 / 60;
const TAG_WIDTH = 100; // Adjusted for better screen fit
const TAG_HEIGHT = 44;
const TAG_RADIUS = 22;

export default function PhysicsWorld() {
    const { width, height } = useWindowDimensions();
    const [bodies, setBodies] = useState<Matter.Body[]>([]);

    // Load System Font
    const font = useMemo(() => {
        return matchFont({
            fontFamily: Platform.select({ ios: 'Helvetica Neue', android: 'sans-serif' }),
            fontSize: 14,
            fontWeight: 'bold',
        });
    }, []);

    useEffect(() => {
        if (!width || !height) return;

        // 1. Setup Matter.js Engine
        const engine = Matter.Engine.create();
        const world = engine.world;
        engine.gravity.y = 0.8; // Slightly reduced gravity for "floaty" feel

        // 2. Setup Boundaries
        // Ground needs to be thick so items don't tunnel through
        const ground = Matter.Bodies.rectangle(width / 2, height - 100, width, 200, {
            isStatic: true,
            label: 'Ground'
        });

        // Walls
        const wallThickness = 100;
        const leftWall = Matter.Bodies.rectangle(0 - wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });
        const rightWall = Matter.Bodies.rectangle(width + wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });

        Matter.World.add(world, [ground, leftWall, rightWall]);

        // 3. Create Tag Bodies
        const tagBodies: Matter.Body[] = [];

        TAGS.forEach((tag, index) => {
            // Random starting X
            const x = Math.random() * (width - 100) + 50;
            // Staggered Y starting height (mostly above screen)
            const y = -200 - (index * 80);

            // Create Body
            const body = Matter.Bodies.rectangle(x, y, TAG_WIDTH, TAG_HEIGHT, {
                chamfer: { radius: TAG_RADIUS },
                restitution: 0.6, // Bounciness
                friction: 0.5,
                frictionAir: 0.02,
                label: tag.label,
                angle: (Math.random() - 0.5) * 0.5, // Slight random initial rotation
                // Custom render properties specifically for our loop
                plugin: {
                    color: tag.color,
                    label: tag.label,
                    icon: tag.icon
                }
            });

            tagBodies.push(body);
        });

        // Add bodies staggered or all at once? 
        // MatterJS can handle them all being added, gravity will pull them down.
        // To simulate the video's "raining" effect more precisely, we can add them to the world over time.
        // For React State simplicity, let's add them all but check their positions.
        // Actually, adding them all is fine because their initial Y is staggered.
        Matter.World.add(world, tagBodies);

        // 4. Game Loop
        let running = true;
        const loop = () => {
            if (!running) return;

            // Update Physics
            Matter.Engine.update(engine, ENGINE_UPDATE_DELTA);

            // Update State
            // Optimization: In production, do NOT set state here. Map bodies to SharedValues.
            // But for this prototype to work immediately without complex boilerplate:
            setBodies([...Matter.Composite.allBodies(world)]);

            requestAnimationFrame(loop);
        };

        requestAnimationFrame(loop);

        return () => {
            running = false;
            Matter.Engine.clear(engine);
        };
    }, [width, height]);

    if (!font) {
        return null; // Or a loading view
    }

    return (
        <Canvas style={{ flex: 1 }}>
            {bodies.map((body, index) => {
                if (body.isStatic) return null; // Don't render walls

                const { x, y } = body.position;
                const { angle } = body;
                const color = body.plugin.color || '#ccc';
                const label = body.plugin.label || '';

                // Calculate text width to center it
                const textWidth = font.getTextWidth(label);
                const textX = -textWidth / 2 + 10; // Offset for icon
                const textY = font.getSize() / 3;

                return (
                    <Group
                        key={body.id}
                        origin={vec(x, y)}
                        transform={[{ rotate: angle }]}
                    >
                        {/* Shadow for depth */}
                        <Group>
                            <RoundedRect
                                x={x - TAG_WIDTH / 2}
                                y={y - TAG_HEIGHT / 2}
                                width={TAG_WIDTH}
                                height={TAG_HEIGHT}
                                r={TAG_RADIUS}
                                color={color}
                            >
                                <Shadow dx={2} dy={2} blur={4} color="rgba(0,0,0,0.1)" />
                            </RoundedRect>
                        </Group>

                        {/* Icon Placeholder (Circle) */}
                        <Group transform={[{ translateX: x - TAG_WIDTH / 2 + 20 }, { translateY: y }]}>
                            <RoundedRect x={-10} y={-10} width={20} height={20} r={10} color="rgba(0,0,0,0.1)" />
                        </Group>

                        {/* Label */}
                        <Text
                            x={x + textX}
                            y={y + textY}
                            text={label}
                            font={font}
                            color="#333"
                        />
                    </Group>
                );
            })}
        </Canvas>
    );
}
