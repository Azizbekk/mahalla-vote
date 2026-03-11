<script setup lang="ts">
import type { TelegramUserDetail } from '../models/telegram-user-detail.model';

defineProps<{ item: TelegramUserDetail | null }>();
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'edit'): void;
  (e: 'delete'): void;
}>();
</script>

<template>
  <q-card-section v-if="item">
    <q-list separator>
      <q-item>
        <q-item-section side><q-icon name="key" /></q-item-section>
        <q-item-section caption>ID</q-item-section>
        <q-item-section>{{ item.id }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="person" /></q-item-section>
        <q-item-section caption>User ID</q-item-section>
        <q-item-section>{{ item.userId }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="chat" /></q-item-section>
        <q-item-section caption>Chat ID</q-item-section>
        <q-item-section>{{ item.chatId }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="alternate_email" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.username') }}</q-item-section>
        <q-item-section>{{ item.username }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="badge" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.firstName') }}</q-item-section>
        <q-item-section>{{ item.firstName }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="badge" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.lastName') }}</q-item-section>
        <q-item-section>{{ item.lastName }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="phone" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.phoneNumber') }}</q-item-section>
        <q-item-section>{{ item.phoneNumber }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="toggle_on" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.state') }}</q-item-section>
        <q-item-section>
          <q-badge :color="item.state === 'HOME' ? 'green' : 'orange'" :label="item.state" />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="schedule" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.createdDate') }}</q-item-section>
        <q-item-section>{{ item.createdDate }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="update" /></q-item-section>
        <q-item-section caption>{{ $t('telegramUser.lastModifiedDate') }}</q-item-section>
        <q-item-section>{{ item.lastModifiedDate }}</q-item-section>
      </q-item>
    </q-list>
  </q-card-section>
  <q-separator />
  <q-card-actions align="right">
    <q-btn flat icon="edit_note" color="primary" :label="$t('entity.action.edit')" @click="emit('edit')" />
    <q-btn flat icon="delete_forever" color="negative" :label="$t('entity.action.delete')" @click="emit('delete')" />
    <q-btn flat icon="close" :label="$t('entity.action.close')" @click="emit('close')" />
  </q-card-actions>
</template>
