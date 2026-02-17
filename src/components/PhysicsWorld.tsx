import { Canvas, Group, RoundedRect, Text, matchFont, useFont, vec } from '@shopify/react-native-skia';
import Matter from 'matter-js';
import React, { useEffect, useMemo, useState } from 'react';
import { LayoutChangeEvent, Platform, View } from 'react-native';
import { Gesture, GestureDetector } from 'react-native-gesture-handler';
import { runOnJS } from 'react-native-reanimated';
import { ICON_GLYPHS, TAGS } from '../constants/Tags';

// eslint-disable-next-line @typescript-eslint/no-var-requires
const iconFontFile = require('../../node_modules/@expo/vector-icons/build/vendor/react-native-vector-icons/Fonts/MaterialCommunityIcons.ttf');


// Physics Configuration
const ENGINE_UPDATE_DELTA = 1000 / 60;
const TAG_HEIGHT = 44;
const TAG_RADIUS = 22;
const GAP = 2;
// Layout: [padding] [icon 16px] [gap 8px] [label] [padding]
const ICON_AREA = 32; // left padding (12) + icon (16) + gap (4)
const H_PADDING = 16; // right padding
const MIN_TAG_WIDTH = 80;

export default function PhysicsWorld() {
    const [dimensions, setDimensions] = useState<{ width: number; height: number } | null>(null);
    const [bodies, setBodies] = useState<Matter.Body[]>([]);
    const [selectedTagIds, setSelectedTagIds] = useState<Set<string>>(new Set());
    const [physicsRef, setPhysicsRef] = useState<{ engine: Matter.Engine, world: Matter.World } | null>(null);

    const font = useMemo(() => {
        return matchFont({
            fontFamily: Platform.select({ ios: 'Helvetica Neue', android: 'sans-serif' }),
            fontSize: 14,
            fontWeight: 'bold',
        });
    }, []);

    const iconFont = useFont(iconFontFile, 16);

    const onLayout = (event: LayoutChangeEvent) => {
        const { width, height } = event.nativeEvent.layout;
        setDimensions({ width, height });
    };

    useEffect(() => {
        if (!dimensions || !font) return;

        const { width, height } = dimensions;

        const engine = Matter.Engine.create();
        const world = engine.world;
        engine.gravity.y = 0.5; // Reduced gravity for smoother feel

        // Add walls and ground based on container dimensions
        const ground = Matter.Bodies.rectangle(width / 2, height + 50, width, 100, { isStatic: true, label: 'Ground' });
        const wallThickness = 100;
        const leftWall = Matter.Bodies.rectangle(0 - wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });
        const rightWall = Matter.Bodies.rectangle(width + wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });

        Matter.World.add(world, [ground, leftWall, rightWall]);

        const tagBodies: Matter.Body[] = [];
        TAGS.forEach((tag, index) => {
            // Dynamic width based on label text
            const textWidth = font.getTextWidth(tag.label);
            const tagWidth = Math.max(MIN_TAG_WIDTH, ICON_AREA + textWidth + H_PADDING);

            const x = Math.random() * (width - tagWidth) + tagWidth / 2;
            const y = -100 - (index * 60); // Start falling from above
            const body = Matter.Bodies.rectangle(x, y, tagWidth + GAP, TAG_HEIGHT + GAP, {
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
                    icon: tag.icon,
                    tagWidth,
                }
            });
            tagBodies.push(body);
        });
        Matter.World.add(world, tagBodies);

        setPhysicsRef({ engine, world });

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
    }, [dimensions, font]);

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
            Matter.Body.applyForce(clickedBody, clickedBody.position, { x: 0, y: -0.05 });
        }
    };

    const tapGesture = Gesture.Tap().onStart((e) => {
        runOnJS(handleTap)(e.x, e.y);
    });

    if (!font || !iconFont) return null;

    return (
        <View style={{ flex: 1 }} onLayout={onLayout}>
            {dimensions && (
                <GestureDetector gesture={tapGesture}>
                    <Canvas style={{ flex: 1 }}>
                        {bodies.map((body, index) => {
                            if (body.isStatic) return null;

                            const { x, y } = body.position;
                            const { angle } = body;
                            const tagData = body.plugin;
                            const isSelected = selectedTagIds.has(tagData.id);

                            // Dynamic width from physics plugin
                            const tagW = tagData.tagWidth || MIN_TAG_WIDTH;
                            const leftEdge = x - tagW / 2;
                            const ICON_X = leftEdge + 12;
                            const LABEL_X = leftEdge + ICON_AREA;
                            const textY = font.getSize() / 3;

                            return (
                                <Group
                                    key={body.id}
                                    origin={vec(x, y)}
                                    transform={[{ rotate: angle }]}
                                >
                                    <Group>
                                        <RoundedRect
                                            x={leftEdge}
                                            y={y - TAG_HEIGHT / 2}
                                            width={tagW}
                                            height={TAG_HEIGHT}
                                            r={TAG_RADIUS}
                                            color={tagData.color || '#ccc'}
                                        />

                                        {isSelected && (
                                            <RoundedRect
                                                x={leftEdge}
                                                y={y - TAG_HEIGHT / 2}
                                                width={tagW}
                                                height={TAG_HEIGHT}
                                                r={TAG_RADIUS}
                                                color="black"
                                                style="stroke"
                                                strokeWidth={2}
                                            />
                                        )}
                                    </Group>

                                    {/* Icon from MaterialCommunityIcons */}
                                    {tagData.icon && ICON_GLYPHS[tagData.icon] && (
                                        <Text
                                            x={ICON_X}
                                            y={y + iconFont.getSize() / 3}
                                            text={ICON_GLYPHS[tagData.icon]}
                                            font={iconFont}
                                            color="#555"
                                        />
                                    )}

                                    <Text
                                        x={LABEL_X}
                                        y={y + textY}
                                        text={tagData.label}
                                        font={font}
                                        color="#333"
                                    />
                                </Group>
                            );
                        })}
                    </Canvas>
                </GestureDetector>
            )}
        </View>
    );
}
