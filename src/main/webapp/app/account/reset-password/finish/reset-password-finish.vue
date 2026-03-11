<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 480px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="lock_reset" size="48px" color="primary" />
        <div class="text-h5 q-mt-sm text-weight-bold">{{ t$('reset.request.title') }}</div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="keyMissing" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('reset.finish.messages.keymissing')"></span>
        </q-banner>

        <q-banner v-if="error" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          {{ t$('reset.finish.messages.error') }}
        </q-banner>

        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          <span v-html="t$('reset.finish.messages.success')"></span>
          <a class="text-white text-weight-bold cursor-pointer q-ml-xs" @click="showLogin()">
            {{ t$('global.messages.info.authenticated.link') }}
          </a>
        </q-banner>

        <q-banner v-if="doNotMatch" class="bg-warning text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="warning" color="white" />
          </template>
          {{ t$('global.messages.error.dontmatch') }}
        </q-banner>

        <q-banner v-if="!success && !keyMissing" class="bg-info text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="info" color="white" />
          </template>
          {{ t$('reset.finish.messages.info') }}
        </q-banner>

        <div v-if="!keyMissing">
          <q-form v-if="!success" @submit.prevent="finishReset" class="q-gutter-md">
            <q-input
              v-model="resetAccount.newPassword"
              :type="showNewPassword ? 'text' : 'password'"
              :label="t$('global.form.newPasswordLabel')"
              :placeholder="t$('global.form.newPasswordPlaceholder')"
              outlined
              dense
              lazy-rules
              data-cy="resetPassword"
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
                <q-icon
                  :name="showNewPassword ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="showNewPassword = !showNewPassword"
                />
              </template>
            </q-input>

            <q-input
              v-model="resetAccount.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              :label="t$('global.form.confirmPasswordLabel')"
              :placeholder="t$('global.form.confirmPasswordPlaceholder')"
              outlined
              dense
              lazy-rules
              data-cy="confirmResetPassword"
              :rules="[
                val => !!val || t$('validate.confirmpassword.required'),
                val => val === resetAccount.newPassword || t$('global.messages.error.dontmatch'),
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

            <q-btn type="submit" color="primary" unelevated class="full-width" :label="t$('password.form.button')" data-cy="submit" />
          </q-form>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { useLoginModal } from '@/account/login-modal';

const { t: t$ } = useI18n();
const { showLogin } = useLoginModal();
const route = useRoute();

const doNotMatch = ref(false);
const success = ref(false);
const error = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

const key = ref<string | null>(null);
const keyMissing = ref(false);

const resetAccount = ref({
  newPassword: '',
  confirmPassword: '',
});

// Read key from route query
const queryKey = route.query?.key;
if (queryKey !== undefined) {
  key.value = Array.isArray(queryKey) ? (queryKey[0] as string) : (queryKey as string);
}
keyMissing.value = !key.value;

const finishReset = async () => {
  doNotMatch.value = false;
  success.value = false;
  error.value = false;

  if (resetAccount.value.newPassword !== resetAccount.value.confirmPassword) {
    doNotMatch.value = true;
    return;
  }

  try {
    await axios.post('api/account/reset-password/finish', {
      key: key.value,
      newPassword: resetAccount.value.newPassword,
    });
    success.value = true;
  } catch {
    success.value = false;
    error.value = true;
  }
};
</script>
