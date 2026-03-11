<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 600px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="settings" size="48px" color="primary" />
        <div v-if="username" class="text-h5 q-mt-sm text-weight-bold" id="settings-title">
          <span v-html="t$('settings.title', { username })"></span>
        </div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          <span v-html="t$('settings.messages.success')"></span>
        </q-banner>

        <q-banner v-if="errorEmailExists" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('register.messages.error.emailexists')"></span>
        </q-banner>

        <q-form v-if="settingsAccount" @submit.prevent="save" class="q-gutter-md" id="settings-form">
          <q-input
            v-model="settingsAccount.firstName"
            :label="t$('settings.form.firstname')"
            :placeholder="t$('settings.form.firstnamePlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="firstname"
            :rules="[
              val => !!val || t$('settings.messages.validate.firstname.required'),
              val => (val && val.length >= 1) || t$('settings.messages.validate.firstname.minlength'),
              val => (val && val.length <= 50) || t$('settings.messages.validate.firstname.maxlength'),
            ]"
          >
            <template #prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="settingsAccount.lastName"
            :label="t$('settings.form.lastname')"
            :placeholder="t$('settings.form.lastnamePlaceholder')"
            outlined
            dense
            lazy-rules
            data-cy="lastname"
            :rules="[
              val => !!val || t$('settings.messages.validate.lastname.required'),
              val => (val && val.length >= 1) || t$('settings.messages.validate.lastname.minlength'),
              val => (val && val.length <= 50) || t$('settings.messages.validate.lastname.maxlength'),
            ]"
          >
            <template #prepend>
              <q-icon name="badge" />
            </template>
          </q-input>

          <q-input
            v-model="settingsAccount.email"
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

          <q-select
            v-if="languageOptions.length > 1"
            v-model="settingsAccount.langKey"
            :options="languageOptions"
            :label="t$('settings.form.language')"
            outlined
            dense
            emit-value
            map-options
            data-cy="langKey"
          >
            <template #prepend>
              <q-icon name="language" />
            </template>
          </q-select>

          <q-btn type="submit" color="primary" unelevated class="full-width" :label="t$('settings.form.button')" data-cy="submit" />
        </q-form>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { computed, inject, type ComputedRef } from 'vue';
import { useI18n } from 'vue-i18n';
import axios from 'axios';
import { ref } from 'vue';
import languages from '@/shared/config/languages';
import { EMAIL_ALREADY_USED_TYPE } from '@/constants';
import { useStore } from '@/store';

const { t: t$ } = useI18n();
const store = useStore();

const success = ref(false);
const error = ref(false);
const errorEmailExists = ref(false);

const settingsAccount = computed(() => store.account);
const username = inject<ComputedRef<string>>('currentUsername', () => computed(() => store.account?.login), true);

const langs = languages();
const languageOptions = Object.entries(langs).map(([key, val]: [string, any]) => ({
  value: key,
  label: val.name,
}));

const save = async () => {
  error.value = false;
  errorEmailExists.value = false;
  try {
    await axios.post('api/account', settingsAccount.value);
    success.value = true;
    error.value = false;
    errorEmailExists.value = false;
  } catch (ex: any) {
    success.value = false;
    if (ex.response?.status === 400 && ex.response?.data?.type === EMAIL_ALREADY_USED_TYPE) {
      errorEmailExists.value = true;
    } else {
      error.value = true;
    }
  }
};
</script>
