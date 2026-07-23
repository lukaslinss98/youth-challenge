import { Link } from 'expo-router';
import { useForm } from 'react-hook-form';
import { Pressable, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { YouthLogo } from '@/components/youth-logo';
import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { FormTextInput } from '@/shared/components/form-text-input';

import { useRegister } from '../api/use-register';
import { useSessionStore } from '../store/session-store';

type RegisterFormValues = {
  email: string;
  password: string;
};

export function RegisterScreen() {
  const theme = useTheme();
  const registerMutation = useRegister();
  const setToken = useSessionStore((state) => state.setToken);
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({ defaultValues: { email: '', password: '' } });

  const onSubmit = (values: RegisterFormValues) => {
    registerMutation.mutate(values, {
      onSuccess: (result) => {
        if (result.type === 'success') {
          setToken(result.token);
        }
      },
    });
  };

  const result = registerMutation.data;

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <YouthLogo style={styles.logo} />

        <ThemedText type="title" style={styles.title}>
          Create account
        </ThemedText>

        <ThemedView style={styles.form}>
          <FormTextInput
            control={control}
            name="email"
            placeholder="Email"
            autoCapitalize="none"
            keyboardType="email-address"
            rules={{
              required: 'Email is required',
              pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Enter a valid email' },
            }}
            errorMessage={errors.email?.message}
          />

          <FormTextInput
            control={control}
            name="password"
            placeholder="Password"
            secureTextEntry
            rules={{
              required: 'Password is required',
              minLength: { value: 8, message: 'Password must be at least 8 characters' },
            }}
            errorMessage={errors.password?.message}
          />

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

          <Link href="/login">
            <ThemedText type="link">Already have an account? Log in</ThemedText>
          </Link>
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
  logo: {
    marginTop: Spacing.five,
    marginBottom: Spacing.two,
  },
  title: {
    marginTop: Spacing.two,
  },
  form: {
    gap: Spacing.three,
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
