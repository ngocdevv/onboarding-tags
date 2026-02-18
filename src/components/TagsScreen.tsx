import React from 'react';
import {
    Pressable,
    StyleSheet,
    Text,
    View,
} from 'react-native';
import Animated, { FadeInDown } from 'react-native-reanimated';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import PhysicsWorld from './PhysicsWorld';

type Props = {
    onContinue: () => void;
};

export default function TagsScreen({ onContinue }: Props) {
    const insets = useSafeAreaInsets();

    return (
        <View style={styles.container}>
            <View style={styles.content}>
                <PhysicsWorld />
            </View>
            <Animated.View
                style={[styles.buttonContainer, { paddingBottom: insets.bottom + 16 }]}
                entering={FadeInDown.delay(600).duration(800).springify()}
            >
                <Pressable
                    style={({ pressed }) => [styles.continueButton, pressed && styles.buttonPressed]}
                    onPress={onContinue}
                >
                    <Text style={styles.continueText}>Continue</Text>
                </Pressable>
            </Animated.View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#FFFFFF',
    },
    content: {
        flex: 1,
        paddingHorizontal: 24,
    },
    buttonContainer: {
        paddingHorizontal: 24,
        paddingTop: 16,
    },
    continueButton: {
        backgroundColor: '#1A1A1A',
        borderRadius: 16,
        paddingVertical: 18,
        alignItems: 'center',
        justifyContent: 'center',
    },
    buttonPressed: {
        opacity: 0.85,
        transform: [{ scale: 0.98 }],
    },
    continueText: {
        color: '#FFFFFF',
        fontSize: 17,
        fontWeight: '600',
        letterSpacing: 0.2,
    },
});
