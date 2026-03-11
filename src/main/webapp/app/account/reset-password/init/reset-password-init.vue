<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 480px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="mark_email_read" size="48px" color="primary" />
        <div class="text-h5 q-mt-sm text-weight-bold">{{ t$('reset.request.title') }}</div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="!success" class="bg-warning text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="info" color="white" />
          </template>
          {{ t$('reset.request.messages.info') }}
        </q-banner>

        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          {{ t$('reset.request.messages.success') }}
        </q-banner>

        <q-form v-if="!success" @submit.prevent="requestReset" class="q-gutter-md">
          <q-input
            v-model="resetAccount.email"
            :label="t$('global.form.emailLabel')"
            :placeholder="t$('global.form.emailPlaceholder')"
            type="email"
            outlined
            dense
            lazy-rules
            data-cy="emailResetPassword"
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

          <q-btn type="submit" color="primary" unelevated class="full-width" :label="t$('reset.request.form.button')" data-cy="submit" />
        </q-form>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import axios from 'axios';

const { t: t$ } = useI18n();

const error = ref(false);
const success = ref(false);
const resetAccount = ref({
  email: '',
});

const requestReset = async () => {
  error.value = false;
  success.value = false;
  try {
    await axios.post('api/account/reset-password/init', resetAccount.value.email, {
      headers: {
        'content-type': 'text/plain',
      },
    });
    success.value = true;
  } catch {
    success.value = false;
    error.value = true;
  }
};
</script>
