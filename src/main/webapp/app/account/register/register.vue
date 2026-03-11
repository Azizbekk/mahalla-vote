<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 500px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="person_add" size="48px" color="primary" />
        <div class="text-h5 q-mt-sm text-weight-bold" id="register-title" data-cy="registerTitle">
          {{ t$('register.title') }}
        </div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          <span v-html="t$('register.messages.success')"></span>
        </q-banner>

        <q-banner v-if="error" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('register.messages.error.fail')"></span>
        </q-banner>

        <q-banner v-if="errorUserExists" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('register.messages.error.userexists')"></span>
        </q-banner>

        <q-banner v-if="errorEmailExists" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('register.messages.error.emailexists')"></span>
        </q-banner>

        <q-form v-if="!success" @submit.prevent="register" class="q-gutter-md" id="register-form">
          <q-input
            v-model="registerAccount.login"
            :label="t$('global.form.usernameLabel')"
            :placeholder="t$('global.form.usernamePlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="username"
            :rules="[
              val => !!val || t$('register.messages.validate.login.required'),
              val => (val && val.length >= 1) || t$('register.messages.validate.login.minlength'),
              val => (val && val.length <= 50) || t$('register.messages.validate.login.maxlength'),
              val => loginPattern.test(val) || t$('register.messages.validate.login.pattern'),
            ]"
          >
            <template #prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="registerAccount.email"
            :label="t$('global.form.emailLabel')"
            :placeholder="t$('global.form.emailPlaceholder')"
            type="email"
            outlined
            dense
            lazy-rules
            data-cy="email"
            :rules="[
              val => !!val || t$('validate.email.required'),
              val => (val && val.length >= 5) || t$('validate.email.minlength'),
              val => (val && val.length <= 254) || t$('validate.email.maxlength'),
              val => /.+@.+\..+/.test(val) || t$('validate.email.invalid'),
            ]"
          >
            <template #prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="registerAccount.password"
            :type="showPassword ? 'text' : 'password'"
            :label="t$('global.form.newPasswordLabel')"
            :placeholder="t$('global.form.newPasswordPlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="firstPassword"
            :rules="[
              val => !!val || t$('validate.newpassword.required'),
              val => (val && val.length >= 4) || t$('validate.newpassword.minlength'),
              val => (val && val.length <= 50) || t$('validate.newpassword.maxlength'),
            ]"
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon :name="showPassword ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="showPassword = !showPassword" />
            </template>
          </q-input>

          <q-input
            v-model="confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            :label="t$('global.form.confirmPasswordLabel')"
            :placeholder="t$('global.form.confirmPasswordPlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="secondPassword"
            :rules="[
              val => !!val || t$('validate.confirmpassword.required'),
              val => (val && val.length >= 4) || t$('validate.confirmpassword.minlength'),
              val => (val && val.length <= 50) || t$('validate.confirmpassword.maxlength'),
              val => val === registerAccount.password || t$('global.messages.error.dontmatch'),
            ]"
          >
            <template #prepend>
              <q-icon name="lock_outline" />
            </template>
            <template #append>
              <q-icon
                :name="showConfirmPassword ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="showConfirmPassword = !showConfirmPassword"
              />
            </template>
          </q-input>

          <q-btn type="submit" color="primary" unelevated class="full-width" :label="t$('register.form.button')" data-cy="submit" />
        </q-form>
      </q-card-section>

      <q-separator />

      <q-card-section class="text-center">
        <span>{{ t$('global.messages.info.authenticated.prefix') }}</span>
        <a class="text-primary cursor-pointer" @click="showLogin()">{{ t$('global.messages.info.authenticated.link') }}</a>
        <span v-html="t$('global.messages.info.authenticated.suffix')"></span>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { useLoginModal } from '@/account/login-modal';
import RegisterService from '@/account/register/register.service';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from '@/constants';

const { t: t$ } = useI18n();
const { showLogin } = useLoginModal();
const registerService = inject('registerService', () => new RegisterService(), true);
const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

const loginPattern = /^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/;

const error = ref(false);
const errorEmailExists = ref(false);
const errorUserExists = ref(false);
const success = ref(false);
const showPassword = ref(false);
const showConfirmPassword = ref(false);

const confirmPassword = ref('');
const registerAccount = ref({
  login: '',
  email: '',
  password: '',
});

const register = async () => {
  error.value = false;
  errorUserExists.value = false;
  errorEmailExists.value = false;

  try {
    await registerService.processRegistration({
      ...registerAccount.value,
      langKey: currentLanguage.value,
    });
    success.value = true;
  } catch (err: any) {
    success.value = false;
    if (err.response?.status === 400 && err.response?.data?.type === LOGIN_ALREADY_USED_TYPE) {
      errorUserExists.value = true;
    } else if (err.response?.status === 400 && err.response?.data?.type === EMAIL_ALREADY_USED_TYPE) {
      errorEmailExists.value = true;
    } else {
      error.value = true;
    }
  }
};
</script>
