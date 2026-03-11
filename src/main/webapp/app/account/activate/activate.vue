<template>
  <div class="auth-page">
    <q-card class="auth-card" flat bordered style="max-width: 500px; margin: 0 auto">
      <q-card-section class="text-center q-pt-lg">
        <q-icon name="verified_user" size="48px" color="primary" />
        <div class="text-h5 q-mt-sm text-weight-bold">{{ t$('activate.title') }}</div>
      </q-card-section>

      <q-card-section>
        <q-banner v-if="success" class="bg-positive text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="check_circle" color="white" />
          </template>
          <span v-html="t$('activate.messages.success')"></span>
          <a class="text-white text-weight-bold cursor-pointer q-ml-xs" @click="showLogin()">
            {{ t$('global.messages.info.authenticated.link') }} </a
          >.
        </q-banner>

        <q-banner v-if="error" class="bg-negative text-white q-mb-md" rounded>
          <template #avatar>
            <q-icon name="error" color="white" />
          </template>
          <span v-html="t$('activate.messages.error')"></span>
        </q-banner>

        <div v-if="!success && !error" class="text-center q-pa-lg">
          <q-spinner-dots color="primary" size="40px" />
          <div class="q-mt-md text-grey-7">{{ t$('activate.title') }}...</div>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import ActivateService from './activate.service';
import { useLoginModal } from '@/account/login-modal';

const { t: t$ } = useI18n();
const { showLogin } = useLoginModal();
const route = useRoute();
const activateService = inject('activateService', () => new ActivateService(), true);

const success = ref(false);
const error = ref(false);

onMounted(async () => {
  const key = Array.isArray(route.query.key) ? route.query.key[0] : route.query.key;
  try {
    await activateService.activateAccount(key as string);
    success.value = true;
    error.value = false;
  } catch {
    error.value = true;
    success.value = false;
  }
});
</script>
