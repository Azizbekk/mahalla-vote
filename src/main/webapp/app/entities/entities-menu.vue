<template>
  <q-item clickable v-close-popup to="/telegram-user">
    <q-item-section avatar><q-icon name="telegram" /></q-item-section>
    <q-item-section>{{ t$('global.menu.entities.telegramUser') }}</q-item-section>
  </q-item>
  <q-item clickable v-close-popup to="/vote">
    <q-item-section avatar><q-icon name="how_to_vote" /></q-item-section>
    <q-item-section>{{ t$('global.menu.entities.vote') }}</q-item-section>
  </q-item>
  <q-item clickable v-close-popup to="/referral">
    <q-item-section avatar><q-icon name="group_add" /></q-item-section>
    <q-item-section>{{ t$('global.menu.entities.referral') }}</q-item-section>
  </q-item>
  <q-item clickable v-close-popup to="/project-setting">
    <q-item-section avatar><q-icon name="settings" /></q-item-section>
    <q-item-section>{{ t$('global.menu.entities.projectSetting') }}</q-item-section>
  </q-item>
  <q-item clickable v-close-popup to="/i18n-message">
    <q-item-section avatar><q-icon name="translate" /></q-item-section>
    <q-item-section>{{ t$('global.menu.entities.i18nMessage') }}</q-item-section>
  </q-item>
  <q-item clickable v-close-popup to="/vote-lot">
    <q-item-section avatar><q-icon name="campaign" /></q-item-section>
    <q-item-section>{{ t$('global.menu.entities.voteLot') }}</q-item-section>
  </q-item>
</template>

<script lang="ts" setup>
import { useI18n } from 'vue-i18n';

const { t: t$ } = useI18n();
</script>
