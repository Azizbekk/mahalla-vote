<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 440px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="lock_open" size="48px" color="primary" />
        <div class="text-h5 q-mt-sm text-weight-bold">{{ t$('login.title') }}</div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="authenticationError" class="bg-negative text-white q-mb-md" rounded data-cy="loginError">
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('login.errorAuthentication')"></span>
        </q-banner>

        <q-form @submit.prevent="doLogin" class="q-gutter-md">
          <q-input
            v-model="login"
            :label="t$('global.form.usernameLabel')"
            :placeholder="t$('global.form.usernamePlaceholder')"
            outlined
            dense
            autofocus
            lazy-rules
            data-cy="username"
            :rules="[val => !!val || t$('validate.newpassword.required')]"
          >
            <template #prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            :label="t$('login.form.password')"
            :placeholder="t$('login.form.passwordPlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="password"
            :rules="[val => !!val || t$('validate.newpassword.required')]"
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon :name="showPassword ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="showPassword = !showPassword" />
            </template>
          </q-input>

          <q-checkbox v-model="rememberMe" :label="t$('login.form.rememberme')" dense data-cy="rememberMe" />

          <q-btn type="submit" color="primary" unelevated class="full-width q-mt-md" :label="t$('login.form.button')" data-cy="submit" />
        </q-form>
      </q-card-section>

      <q-separator />

      <q-card-section class="q-gutter-sm">
        <div class="text-center">
          <router-link to="/account/reset/request" class="text-primary" data-cy="forgetYourPasswordSelector">
            {{ t$('login.password.forgot') }}
          </router-link>
        </div>
        <div class="text-center">
          <span>{{ t$('global.messages.info.register.noaccount') }} </span>
          <router-link to="/register" class="text-primary">
            {{ t$('global.messages.info.register.link') }}
          </router-link>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import axios from 'axios';
import type AccountService from '../account.service';
import { useLoginModal } from '@/account/login-modal';

const { t: t$ } = useI18n();
const { hideLogin } = useLoginModal();
const route = useRoute();
const router = useRouter();
const accountService = inject<AccountService>('accountService')!;

const authenticationError = ref(false);
const login = ref('');
const password = ref('');
const rememberMe = ref(false);
const showPassword = ref(false);

const doLogin = async () => {
  const data = { username: login.value, password: password.value, rememberMe: rememberMe.value };
  try {
    const result = await axios.post('api/authenticate', data);
    const bearerToken = result.headers.authorization;
    if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
      const jwt = bearerToken.slice(7, bearerToken.length);
      if (rememberMe.value) {
        localStorage.setItem('authenticationToken', jwt);
        sessionStorage.removeItem('authenticationToken');
      } else {
        sessionStorage.setItem('authenticationToken', jwt);
        localStorage.removeItem('authenticationToken');
      }
    }

    authenticationError.value = false;
    hideLogin();
    await accountService.retrieveAccount();
    if (route.path === '/forbidden') {
      router.go(-1);
    }
  } catch {
    authenticationError.value = true;
  }
};
</script>
