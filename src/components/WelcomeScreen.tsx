import { Ionicons, MaterialCommunityIcons } from '@expo/vector-icons';
import React from 'react';
import {
    Pressable,
    StyleSheet,
    Text,
    View
} from 'react-native';
import Animated, { FadeInDown, FadeInUp, ZoomIn } from 'react-native-reanimated';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

const FEATURES = [
    {
        icon: 'search-outline' as const,
        iconFamily: 'ionicons' as const,
        color: '#E8F0FE',
        iconColor: '#4A90D9',
        title: 'Find new activities',
        description: 'Discover fresh ideas that spark your interest',
    },
    {
        icon: 'leaf' as const,
        iconFamily: 'mci' as const,
        color: '#E8F5E9',
        iconColor: '#4CAF50',
        title: 'Stay calm and mindful',
        description: 'Find new activities that spark your interest',
    },
    {
        icon: 'lightbulb-outline' as const,
        iconFamily: 'mci' as const,
        color: '#FFF3E0',
        iconColor: '#FF9800',
        title: 'Boost your creativity',
        description: 'Stay calm and mindful while exploring habits',
    },
    {
        icon: 'trophy-outline' as const,
        iconFamily: 'mci' as const,
        color: '#F3E5F5',
        iconColor: '#9C27B0',
        title: 'Build lasting routines',
        description: 'Boost your creativity with daily inspiration',
    },
];

type Props = {
    onContinue: () => void;
};

export default function WelcomeScreen({ onContinue }: Props) {
    const insets = useSafeAreaInsets();

    return (
        <View style={[styles.container, { paddingTop: insets.top + 20, paddingBottom: insets.bottom + 16 }]}>
            {/* Rocket Illustration */}
            <View style={styles.illustrationContainer}>
                <Animated.Image
                    source={require('@/assets/images/rocket.png')}
                    style={styles.rocketImage}
                    resizeMode="contain"
                    entering={ZoomIn.duration(1000)}
                />
            </View>

            {/* Feature List */}
            <View style={styles.featureList}>
                {FEATURES.map((feature, index) => (
                    <Animated.View
                        key={index}
                        style={styles.featureRow}
                        entering={FadeInDown.delay(300 + index * 200).duration(800).springify()}
                    >
                        <View style={[styles.iconCircle, { backgroundColor: feature.color }]}>
                            {feature.iconFamily === 'ionicons' ? (
                                <Ionicons name={feature.icon as any} size={20} color={feature.iconColor} />
                            ) : (
                                <MaterialCommunityIcons name={feature.icon as any} size={20} color={feature.iconColor} />
                            )}
                        </View>
                        <View style={styles.featureText}>
                            <Text style={styles.featureTitle}>{feature.title}</Text>
                            <Text style={styles.featureDescription}>{feature.description}</Text>
                        </View>
                    </Animated.View>
                ))}
            </View>

            {/* Continue Button */}
            <Animated.View style={styles.buttonContainer} entering={FadeInUp.delay(1400).duration(800).springify()}>
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
        paddingHorizontal: 24,
    },
    illustrationContainer: {
        alignItems: 'center',
        justifyContent: 'center',
        flex: 1,
        maxHeight: 260,
    },
    rocketImage: {
        width: 200,
        height: 200,
    },
    featureList: {
        flex: 1,
        justifyContent: 'center',
        gap: 28,
    },
    featureRow: {
        flexDirection: 'row',
        alignItems: 'flex-start',
        gap: 16,
    },
    iconCircle: {
        width: 44,
        height: 44,
        borderRadius: 22,
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: 2,
    },
    featureText: {
        flex: 1,
        gap: 4,
    },
    featureTitle: {
        fontSize: 17,
        fontWeight: '700',
        color: '#1A1A1A',
        letterSpacing: -0.2,
    },
    featureDescription: {
        fontSize: 14,
        color: '#888',
        lineHeight: 20,
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
