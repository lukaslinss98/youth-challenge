import { router } from 'expo-router';
import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Pressable, StyleSheet, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';

import { useRegister } from '../api/use-register';
import { useSessionStore } from '../store/session-store';

type RegisterFormValues = {
  email: string;
  password: string;
};

export function RegisterScreen() {
  const theme = useTheme();
  const registerMutation = useRegister();
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({ defaultValues: { email: '', password: '' } });

  const onSubmit = (values: RegisterFormValues) => {
    registerMutation.mutate(values);
  };

  const result = registerMutation.data;
  const setToken = useSessionStore((state) => state.setToken);

  useEffect(() => {
    if (result?.type === 'success') {
      setToken(result.token);
      router.replace('/(tabs)');
    }
  }, [result, setToken]);

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedText type="title" style={styles.title}>
          Create account
        </ThemedText>

        <ThemedView style={styles.form}>
          <Controller
            control={control}
            name="email"
            rules={{
              required: 'Email is required',
              pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Enter a valid email' },
            }}
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                placeholder="Email"
                placeholderTextColor={theme.textSecondary}
                autoCapitalize="none"
                keyboardType="email-address"
                style={[styles.input, { color: theme.text, borderColor: theme.backgroundElement }]}
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
          {errors.email && <ThemedText type="small">{errors.email.message}</ThemedText>}

          <Controller
            control={control}
            name="password"
            rules={{
              required: 'Password is required',
              minLength: { value: 8, message: 'Password must be at least 8 characters' },
            }}
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                placeholder="Password"
                placeholderTextColor={theme.textSecondary}
                secureTextEntry
                style={[styles.input, { color: theme.text, borderColor: theme.backgroundElement }]}
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
          {errors.password && <ThemedText type="small">{errors.password.message}</ThemedText>}

          <Pressable
            onPress={handleSubmit(onSubmit)}
            disabled={registerMutation.isPending}
            style={({ pressed }) => [
              styles.submitButton,
              { backgroundColor: theme.backgroundElement },
              pressed && styles.pressed,
            ]}>
            <ThemedText type="smallBold">
              {registerMutation.isPending ? 'Creating account…' : 'Register'}
            </ThemedText>
          </Pressable>

          {result?.type === 'userExists' && (
            <ThemedText type="small">An account with this email already exists.</ThemedText>
          )}
          {result?.type === 'error' && <ThemedText type="small">{result.message}</ThemedText>}
        </ThemedView>
      </SafeAreaView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  safeArea: {
    flex: 1,
    paddingHorizontal: Spacing.four,
    gap: Spacing.four,
  },
  title: {
    marginTop: Spacing.five,
  },
  form: {
    gap: Spacing.three,
  },
  input: {
    borderWidth: 1,
    borderRadius: Spacing.two,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
    fontSize: 16,
  },
  submitButton: {
    borderRadius: Spacing.two,
    paddingVertical: Spacing.three,
    alignItems: 'center',
  },
  pressed: {
    opacity: 0.7,
  },
});
