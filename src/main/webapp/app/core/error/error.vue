<template>
  <div class="row justify-center q-mt-xl">
    <div class="col-12 col-md-6 text-center">
      <q-icon :name="error404 ? 'search_off' : 'block'" size="100px" :color="error404 ? 'warning' : 'negative'" />
      <div class="text-h4 q-mt-md">{{ t$('error.title') }}</div>

      <q-banner v-if="errorMessage" rounded class="bg-negative text-white q-mt-lg">
        <template v-slot:avatar>
          <q-icon name="error" />
        </template>
        {{ errorMessage }}
      </q-banner>

      <q-banner v-if="error403" rounded class="bg-negative text-white q-mt-lg">
        <template v-slot:avatar>
          <q-icon name="lock" />
        </template>
        {{ t$('error.http.403') }}
      </q-banner>

      <q-banner v-if="error404" rounded class="bg-warning text-dark q-mt-lg">
        <template v-slot:avatar>
          <q-icon name="search_off" />
        </template>
        {{ t$('error.http.404') }}
      </q-banner>

      <q-btn class="q-mt-lg" color="primary" icon="home" label="Go Home" no-caps to="/" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type ComputedRef, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useLoginModal } from '@/account/login-modal';

const { t: t$ } = useI18n();
const { showLogin } = useLoginModal();
const authenticated = inject<ComputedRef<boolean>>('authenticated');
const route = useRoute();

const errorMessage = ref<string | null>(null);
const error403 = ref(false);
const error404 = ref(false);

if (route.meta) {
  errorMessage.value = (route.meta.errorMessage as string) ?? null;
  error403.value = (route.meta.error403 as boolean) ?? false;
  error404.value = (route.meta.error404 as boolean) ?? false;
  if (!authenticated.value && error403.value) {
    showLogin();
  }
}
</script>
