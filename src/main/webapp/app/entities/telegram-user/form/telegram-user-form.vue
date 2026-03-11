<script setup lang="ts">
import { useTelegramUserForm } from './telegram-user-form';
import type { TelegramUserDetail } from '../models/telegram-user-detail.model';
import type { TelegramUserForm } from '../models/telegram-user-form.model';

const props = defineProps<{
  item?: TelegramUserDetail | null;
  mode: 'create' | 'edit';
}>();

const emit = defineEmits<{
  (e: 'created', data: TelegramUserForm): void;
  (e: 'updated', data: TelegramUserForm): void;
  (e: 'cancel'): void;
}>();

const { state, meta, actions } = useTelegramUserForm({ props, emit });
</script>

<template>
  <q-form :ref="el => (state.formRef = el)" @submit.prevent="actions.submit" greedy class="column no-wrap" style="max-height: 80vh">
    <q-inner-loading :showing="state.loadingData">
      <q-spinner-gears size="50px" color="primary" />
    </q-inner-loading>

    <div class="col q-pa-md" style="overflow-y: auto; min-height: 0">
      <div class="column q-gutter-md">
        <q-input
          v-model.number="state.formData.userId"
          :rules="meta.rules.userId"
          :label="'User ID *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="person" color="primary" /></template>
        </q-input>

        <q-input
          v-model.number="state.formData.chatId"
          :rules="meta.rules.chatId"
          :label="'Chat ID *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="chat" color="primary" /></template>
        </q-input>

        <q-input v-model="state.formData.username" :label="$t('telegramUser.username')" outlined dense :disable="state.loading">
          <template #prepend><q-icon name="alternate_email" color="primary" /></template>
        </q-input>

        <q-input
          v-model="state.formData.firstName"
          :rules="meta.rules.firstName"
          :label="$t('telegramUser.firstName') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="badge" color="primary" /></template>
        </q-input>

        <q-input v-model="state.formData.lastName" :label="$t('telegramUser.lastName')" outlined dense :disable="state.loading">
          <template #prepend><q-icon name="badge" color="primary" /></template>
        </q-input>

        <q-input v-model="state.formData.phoneNumber" :label="$t('telegramUser.phoneNumber')" outlined dense :disable="state.loading">
          <template #prepend><q-icon name="phone" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.state"
          :options="state.stateOptions"
          :label="$t('telegramUser.state')"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="toggle_on" color="primary" /></template>
        </q-select>
      </div>
    </div>

    <q-separator />
    <div class="row justify-end q-gutter-sm q-pa-md">
      <q-btn flat :label="$t('entity.action.cancel')" color="grey-7" icon="close" :disable="state.loading" @click="actions.cancel" />
      <q-btn
        unelevated
        type="submit"
        :label="props.mode === 'edit' ? $t('entity.action.update') : $t('entity.action.create')"
        color="primary"
        icon="check_circle"
        :loading="state.loading"
      />
    </div>
  </q-form>
</template>
