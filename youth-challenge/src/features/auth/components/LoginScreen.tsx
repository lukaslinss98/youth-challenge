import { Link } from 'expo-router';
import { useForm } from 'react-hook-form';
import { Pressable, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { FormTextInput } from '@/shared/components/form-text-input';

import { useLogin } from '../api/use-login';
import { useSessionStore } from '../store/session-store';

type LoginFormValues = {
  email: string;
  password: string;
};

export function LoginScreen() {
  const theme = useTheme();
  const loginMutation = useLogin();
  const setToken = useSessionStore((state) => state.setToken);
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({ defaultValues: { email: '', password: '' } });

  const onSubmit = (values: LoginFormValues) => {
    loginMutation.mutate(values, {
      onSuccess: (result) => {
        if (result.type === 'success') {
          setToken(result.token);
        }
      },
    });
  };

  const result = loginMutation.data;

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedText type="title" style={styles.title}>
          Log in
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
            rules={{ required: 'Password is required' }}
            errorMessage={errors.password?.message}
          />

          <Pressable
            onPress={handleSubmit(onSubmit)}
            disabled={loginMutation.isPending}
            style={({ pressed }) => [
              styles.submitButton,
              { backgroundColor: theme.backgroundElement },
              pressed && styles.pressed,
            ]}>
            <ThemedText type="smallBold">
              {loginMutation.isPending ? 'Logging in…' : 'Log in'}
            </ThemedText>
          </Pressable>

          {result?.type === 'invalidCredentials' && (
            <ThemedText type="small">Incorrect email or password.</ThemedText>
          )}
          {result?.type === 'error' && <ThemedText type="small">{result.message}</ThemedText>}

          <Link href="/register">
            <ThemedText type="link">Don&apos;t have an account? Register</ThemedText>
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
    justifyContent: 'center',
  },
  title: {
    textAlign: 'center',
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
