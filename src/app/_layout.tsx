import { DarkTheme, DefaultTheme, ThemeProvider } from '@react-navigation/native';
import React, { useState } from 'react';
import { useColorScheme } from 'react-native';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

import { AnimatedSplashOverlay } from '@/components/animated-icon';
import AppTabs from '@/components/app-tabs';
import TagsScreen from '@/components/TagsScreen';
import WelcomeScreen from '@/components/WelcomeScreen';

export default function TabLayout() {
  const colorScheme = useColorScheme();
  const [onboardingStep, setOnboardingStep] = useState(0); // 0: Welcome, 1: Tags, 2: Tabs

  const handleWelcomeContinue = () => {
    setOnboardingStep(1);
  };

  const handleTagsContinue = () => {
    setOnboardingStep(2);
  };

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
        <AnimatedSplashOverlay />

        {onboardingStep === 0 && (
          <WelcomeScreen onContinue={handleWelcomeContinue} />
        )}

        {onboardingStep === 1 && (
          <TagsScreen onContinue={handleTagsContinue} />
        )}

        {onboardingStep === 2 && (
          <AppTabs />
        )}

      </ThemeProvider>
    </GestureHandlerRootView>
  );
}
