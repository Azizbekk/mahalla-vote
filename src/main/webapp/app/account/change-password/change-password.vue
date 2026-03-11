<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 500px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="vpn_key" size="48px" color="primary" />
        <div v-if="username" class="text-h5 q-mt-sm text-weight-bold" id="password-title">
          <span v-html="t$('password.title', { username })"></span>
        </div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          <span v-html="t$('password.messages.success')"></span>
        </q-banner>

        <q-banner v-if="error" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('password.messages.error')"></span>
        </q-banner>

        <q-banner v-if="doNotMatch" class="bg-warning text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="warning" color="white" />
          </template>
          {{ t$('global.messages.error.dontmatch') }}
        </q-banner>

        <q-form @submit.prevent="changePassword" class="q-gutter-md" id="password-form">
          <q-input
            v-model="resetPassword.currentPassword"
            :type="showCurrent ? 'text' : 'password'"
            :label="t$('global.form.currentPasswordLabel')"
            :placeholder="t$('global.form.currentPasswordPlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="currentPassword"
            :rules="[val => !!val || t$('validate.newpassword.required')]"
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon :name="showCurrent ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="showCurrent = !showCurrent" />
            </template>
          </q-input>

          <q-input
            v-model="resetPassword.newPassword"
            :type="showNew ? 'text' : 'password'"
            :label="t$('global.form.newPasswordLabel')"
            :placeholder="t$('global.form.newPasswordPlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="newPassword"
            :rules="[
              val => !!val || t$('validate.newpassword.required'),
              val => (val && val.length >= 4) || t$('validate.newpassword.minlength'),
              val => (val && val.length <= 50) || t$('validate.newpassword.maxlength'),
            ]"
          >
            <template #prepend>
              <q-icon name="lock_outline" />
            </template>
            <template #append>
              <q-icon :name="showNew ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="showNew = !showNew" />
            </template>
          </q-input>

          <q-linear-progress
            v-if="resetPassword.newPassword"
            :value="passwordStrength"
            :color="passwordStrengthColor"
            class="q-mt-none"
            style="margin-top: -12px !important"
            rounded
          />

          <q-input
            v-model="resetPassword.confirmPassword"
            :type="showConfirm ? 'text' : 'password'"
            :label="t$('global.form.confirmPasswordLabel')"
            :placeholder="t$('global.form.confirmPasswordPlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="confirmPassword"
            :rules="[
              val => !!val || t$('validate.confirmpassword.required'),
              val => val === resetPassword.newPassword || t$('global.messages.error.dontmatch'),
            ]"
          >
            <template #prepend>
              <q-icon name="lock_outline" />
            </template>
            <template #append>
              <q-icon :name="showConfirm ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="showConfirm = !showConfirm" />
            </template>
          </q-input>

          <q-btn type="submit" color="primary" unelevated class="full-width" :label="t$('password.form.button')" data-cy="submit" />
        </q-form>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, type ComputedRef } from 'vue';
import { useI18n } from 'vue-i18n';
import axios from 'axios';

const { t: t$ } = useI18n();
const username = inject<ComputedRef<string>>('currentUsername');

const success = ref(false);
const error = ref(false);
const doNotMatch = ref(false);
const showCurrent = ref(false);
const showNew = ref(false);
const showConfirm = ref(false);

const resetPassword = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const passwordStrength = computed(() => {
  const pw = resetPassword.value.newPassword;
  if (!pw) return 0;
  let score = 0;
  if (pw.length >= 4) score += 0.2;
  if (pw.length >= 8) score += 0.2;
  if (/[A-Z]/.test(pw)) score += 0.2;
  if (/[0-9]/.test(pw)) score += 0.2;
  if (/[^A-Za-z0-9]/.test(pw)) score += 0.2;
  return score;
});

const passwordStrengthColor = computed(() => {
  const s = passwordStrength.value;
  if (s <= 0.2) return 'negative';
  if (s <= 0.4) return 'warning';
  if (s <= 0.6) return 'info';
  return 'positive';
});

const changePassword = async () => {
  success.value = false;
  error.value = false;
  doNotMatch.value = false;

  if (resetPassword.value.newPassword !== resetPassword.value.confirmPassword) {
    doNotMatch.value = true;
    return;
  }

  try {
    await axios.post('api/account/change-password', {
      currentPassword: resetPassword.value.currentPassword,
      newPassword: resetPassword.value.newPassword,
    });
    success.value = true;
    error.value = false;
  } catch {
    success.value = false;
    error.value = true;
  }
};
</script>
