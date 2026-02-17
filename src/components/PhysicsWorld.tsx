import { Canvas, Group, RoundedRect, Shadow, Text, matchFont, useFont, vec } from '@shopify/react-native-skia';
import Matter from 'matter-js';
import React, { useEffect, useMemo, useState } from 'react';
import { Platform, useWindowDimensions } from 'react-native';
import { Gesture, GestureDetector } from 'react-native-gesture-handler';
import { runOnJS } from 'react-native-reanimated';
import { ICON_GLYPHS, TAGS } from '../constants/Tags';

// eslint-disable-next-line @typescript-eslint/no-var-requires
const iconFontFile = require('../../node_modules/@expo/vector-icons/build/vendor/react-native-vector-icons/Fonts/MaterialCommunityIcons.ttf');


// Physics Configuration
const ENGINE_UPDATE_DELTA = 1000 / 60;
const TAG_WIDTH = 100;
const TAG_HEIGHT = 44;
const TAG_RADIUS = 22;
const GAP = 2; // Visual gap (physics size vs render size)

export default function PhysicsWorld() {
    const { width, height } = useWindowDimensions();
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

    useEffect(() => {
        if (!width || !height) return;

        const engine = Matter.Engine.create();
        const world = engine.world;
        engine.gravity.y = 0.8;

        const ground = Matter.Bodies.rectangle(width / 2, height - 100, width, 200, { isStatic: true, label: 'Ground' });
        const wallThickness = 100;
        const leftWall = Matter.Bodies.rectangle(0 - wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });
        const rightWall = Matter.Bodies.rectangle(width + wallThickness / 2, height / 2, wallThickness, height * 2, { isStatic: true });

        Matter.World.add(world, [ground, leftWall, rightWall]);

        const tagBodies: Matter.Body[] = [];
        TAGS.forEach((tag, index) => {
            const x = Math.random() * (width - 100) + 50;
            const y = -200 - (index * 80);
            // Physics body is slightly larger than render to create "gap" visual
            const body = Matter.Bodies.rectangle(x, y, TAG_WIDTH + GAP, TAG_HEIGHT + GAP, {
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
        <GestureDetector gesture={tapGesture}>
            <Canvas style={{ flex: 1 }}>
                {bodies.map((body, index) => {
                    if (body.isStatic) return null;

                    const { x, y } = body.position;
                    const { angle } = body;
                    const tagData = body.plugin;
                    const isSelected = selectedTagIds.has(tagData.id);

                    const iconOffset = tagData.icon ? 14 : 0; // shift text right when icon present
                    const textWidth = font.getTextWidth(tagData.label || '');
                    const textX = -textWidth / 2 + iconOffset;
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
                                    color={tagData.color || '#ccc'}
                                >
                                    <Shadow dx={2} dy={2} blur={4} color="rgba(0,0,0,0.1)" />
                                </RoundedRect>

                                {/*
                  If selected, we need to draw the border stroke with 'black'.
                  The above RoundedRect sets style='stroke' so color controls stroke color.
                  Wait, if I set color transparent above, stroke will be transparent.
                  I need separate logic or simply change color prop based on selection.
                */}
                                {isSelected && (
                                    <RoundedRect
                                        x={x - TAG_WIDTH / 2}
                                        y={y - TAG_HEIGHT / 2}
                                        width={TAG_WIDTH}
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
                                    x={x - TAG_WIDTH / 2 + 12}
                                    y={y + iconFont.getSize() / 3}
                                    text={ICON_GLYPHS[tagData.icon]}
                                    font={iconFont}
                                    color="#555"
                                />
                            )}

                            <Text
                                x={x + textX}
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
    );
}
