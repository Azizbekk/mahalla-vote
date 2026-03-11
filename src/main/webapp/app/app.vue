<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary">
      <app-navbar />
    </q-header>
    <q-page-container>
      <q-page padding>
        <router-view />
      </q-page>
    </q-page-container>
    <q-footer class="bg-grey-2 text-grey-8 text-center q-pa-sm">
      <app-footer />
    </q-footer>
    <q-dialog v-model="loginModalOpen">
      <login-form @close="loginModalOpen = false" />
    </q-dialog>
  </q-layout>
</template>

<script lang="ts" setup>
import { provide } from 'vue';
import { storeToRefs } from 'pinia';

import { useLoginModal } from '@/account/login-modal';
import LoginForm from '@/account/login-form/login-form.vue';
import AppFooter from '@/core/footer/footer.vue';
import AppNavbar from '@/core/navbar/navbar.vue';
import { useAlertService } from '@/shared/alert/alert.service';
import '@/shared/config/dayjs';

provide('alertService', useAlertService());
const { loginModalOpen } = storeToRefs(useLoginModal());
</script>
