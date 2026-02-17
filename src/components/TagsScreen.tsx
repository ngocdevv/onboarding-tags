import React from 'react';
import {
    Pressable,
    StyleSheet,
    Text,
    View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import PhysicsWorld from './PhysicsWorld';

type Props = {
    onContinue: () => void;
};

export default function TagsScreen({ onContinue }: Props) {
    const insets = useSafeAreaInsets();

    return (
        <View style={[styles.container, { paddingTop: insets.top + 10, paddingBottom: insets.bottom + 16 }]}>
            {/* Progress Bar */}
            <View style={styles.progressContainer}>
                {[...Array(5)].map((_, index) => (
                    <View
                        key={index}
                        style={[
                            styles.progressSegment,
                            index === 0 ? styles.progressActive : styles.progressInactive
                        ]}
                    />
                ))}
            </View>

            {/* Header */}
            <View style={styles.header}>
                <Text style={styles.title}>
                    Build Around{'\n'}What You Love 💡
                </Text>
                <Text style={styles.subtitle}>
                    Choosing activities helps you track{'\n'}progress and stay motivated every day
                </Text>
            </View>

            {/* Physics Tags Area */}
            <View style={styles.physicsContainer}>
                <PhysicsWorld />
            </View>

            {/* Continue Button */}
            <View style={styles.buttonContainer}>
                <Pressable
                    style={({ pressed }) => [styles.continueButton, pressed && styles.buttonPressed]}
                    onPress={onContinue}
                >
                    <Text style={styles.continueText}>Continue</Text>
                </Pressable>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#FFFFFF',
        paddingHorizontal: 24,
    },
    progressContainer: {
        flexDirection: 'row',
        gap: 6,
        marginBottom: 32,
    },
    progressSegment: {
        flex: 1,
        height: 6,
        borderRadius: 3,
    },
    progressActive: {
        backgroundColor: '#1A1A1A',
    },
    progressInactive: {
        backgroundColor: '#F0F0F0',
    },
    header: {
        gap: 12,
        marginBottom: 24,
    },
    title: {
        fontSize: 28,
        fontWeight: '800',
        color: '#1A1A1A',
        lineHeight: 34,
        letterSpacing: -0.5,
    },
    subtitle: {
        fontSize: 15,
        color: '#666',
        lineHeight: 22,
    },
    physicsContainer: {
        flex: 1,
        // PhysicsWorld inside will take full width/height
        marginHorizontal: -24, // extend to edges for physics boundary
        marginBottom: 16,
    },
    buttonContainer: {
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
