import React, { useEffect, useState, useMemo } from 'react';
import { useWindowDimensions, Platform, View } from 'react-native';
import { Canvas, Group, RoundedRect, Text, matchFont, vec, LinearGradient, Shadow } from '@shopify/react-native-skia';
import Matter from 'matter-js';
import { TAGS } from '../constants/Tags';
import { Gesture, GestureDetector } from 'react-native-gesture-handler';
import { runOnJS } from 'react-native-reanimated';

// Physics Configuration
const ENGINE_UPDATE_DELTA = 1000 / 60;
const TAG_WIDTH = 100;
const TAG_HEIGHT = 44;
const TAG_RADIUS = 22;

export default function PhysicsWorld() {
    const { width, height } = useWindowDimensions();
    const [bodies, setBodies] = useState<Matter.Body[]>([]);
    const [selectedTagIds, setSelectedTagIds] = useState<Set<string>>(new Set());

    // Ref to access physics engine in callbacks without closures
    const [physicsRef, setPhysicsRef] = useState<{ engine: Matter.Engine, world: Matter.World } | null>(null);

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
        engine.gravity.y = 0.8;

        // 2. Setup Boundaries
        const ground = Matter.Bodies.rectangle(width / 2, height - 100, width, 200, { isStatic: true, label: 'Ground' });
        const wallThickness = 100;
        const leftWall = Matter.Bodies.rectangle(0 - wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });
        const rightWall = Matter.Bodies.rectangle(width + wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });

        Matter.World.add(world, [ground, leftWall, rightWall]);

        // 3. Create Tag Bodies
        const tagBodies: Matter.Body[] = [];
        TAGS.forEach((tag, index) => {
            const x = Math.random() * (width - 100) + 50;
            const y = -200 - (index * 80);
            const body = Matter.Bodies.rectangle(x, y, TAG_WIDTH, TAG_HEIGHT, {
                chamfer: { radius: TAG_RADIUS },
                restitution: 0.6,
                friction: 0.5,
                frictionAir: 0.02,
                label: tag.label,
                angle: (Math.random() - 0.5) * 0.5,
                plugin: {
                    id: tag.id,
                    color: tag.color,
                    label: tag.label,
                    icon: tag.icon
                }
            });
            tagBodies.push(body);
        });
        Matter.World.add(world, tagBodies);

        setPhysicsRef({ engine, world });

        // 4. Game Loop
        let running = true;
        const loop = () => {
            if (!running) return;
            Matter.Engine.update(engine, ENGINE_UPDATE_DELTA);
            setBodies([...Matter.Composite.allBodies(world)]);
            requestAnimationFrame(loop);
        };
        requestAnimationFrame(loop);

        return () => {
            running = false;
            Matter.Engine.clear(engine);
        };
    }, [width, height]);


    // Handle Tap Logic (runs on JS thread)
    const handleTap = (x: number, y: number) => {
        if (!physicsRef) return;

        const allBodies = Matter.Composite.allBodies(physicsRef.world);
        const clickedBodies = Matter.Query.point(allBodies, { x, y });
        const clickedBody = clickedBodies.find(b => !b.isStatic);

        if (clickedBody) {
            const tagId = clickedBody.plugin.id;
            setSelectedTagIds(prev => {
                const next = new Set(prev);
                if (next.has(tagId)) {
                    next.delete(tagId);
                } else {
                    next.add(tagId);
                }
                return next;
            });

            // Feedback impulse
            Matter.Body.applyForce(clickedBody, clickedBody.position, { x: 0, y: -0.05 });
        }
    };

    // Gesture Definition
    const tapGesture = Gesture.Tap()
        .onStart((e) => {
            runOnJS(handleTap)(e.x, e.y);
        });

    if (!font) return null;

    return (
        <GestureDetector gesture={tapGesture}>
            <Canvas style={{ flex: 1 }}>
                {bodies.map((body, index) => {
                    if (body.isStatic) return null;

                    const { x, y } = body.position;
                    const { angle } = body;
                    const tagData = body.plugin;
                    const isSelected = selectedTagIds.has(tagData.id);

                    const baseColor = tagData.color || '#ccc';
                    const displayColor = isSelected ? '#4a90e2' : baseColor;
                    const textColor = isSelected ? '#fff' : '#333';

                    const textWidth = font.getTextWidth(tagData.label || '');
                    const textX = -textWidth / 2 + 10;
                    const textY = font.getSize() / 3;

                    return (
                        <Group
                            key={body.id}
                            origin={vec(x, y)}
                            transform={[{ rotate: angle }]}
                        >
                            <Group>
                                <RoundedRect
                                    x={x - TAG_WIDTH / 2}
                                    y={y - TAG_HEIGHT / 2}
                                    width={TAG_WIDTH}
                                    height={TAG_HEIGHT}
                                    r={TAG_RADIUS}
                                    color={displayColor}
                                >
                                    <Shadow dx={2} dy={2} blur={4} color="rgba(0,0,0,0.1)" />
                                    {isSelected && <Shadow dx={0} dy={0} blur={8} color="#4a90e2" />}
                                </RoundedRect>
                            </Group>

                            <Group transform={[{ translateX: x - TAG_WIDTH / 2 + 20 }, { translateY: y }]}>
                                <RoundedRect x={-10} y={-10} width={20} height={20} r={10} color={isSelected ? "rgba(255,255,255,0.3)" : "rgba(0,0,0,0.1)"} />
                            </Group>

                            <Text
                                x={x + textX}
                                y={y + textY}
                                text={tagData.label}
                                font={font}
                                color={textColor}
                            />
                        </Group>
                    );
                })}
            </Canvas>
        </GestureDetector>
    );
}
