<script setup lang="ts">
import type { I18nMessageDetail } from '../models/i18n-message-detail.model';

defineProps<{ item: I18nMessageDetail | null }>();
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'edit'): void;
  (e: 'delete'): void;
}>();

const langLabels: Record<string, string> = {
  uzLat: "O'zbekcha (Lotin)",
  en: 'English',
  ru: 'Русский',
  uzCrl: 'Ўзбекча (Кирилл)',
  krLat: 'Qaraqalpaqsha',
};
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
        <q-item-section side><q-icon name="code" /></q-item-section>
        <q-item-section caption>{{ $t('i18nMessage.shortCode') }}</q-item-section>
        <q-item-section
          ><code>{{ item.shortCode }}</code></q-item-section
        >
      </q-item>

      <!-- Localized names -->
      <q-item v-for="(label, key) in langLabels" :key="key">
        <q-item-section side><q-icon name="translate" /></q-item-section>
        <q-item-section caption>{{ label }}</q-item-section>
        <q-item-section>{{ (item.localizedName as any)?.[key] || '—' }}</q-item-section>
      </q-item>

      <q-item>
        <q-item-section side><q-icon name="sort" /></q-item-section>
        <q-item-section caption>{{ $t('i18nMessage.sortOrder') }}</q-item-section>
        <q-item-section>{{ item.sortOrder }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="toggle_on" /></q-item-section>
        <q-item-section caption>{{ $t('i18nMessage.status') }}</q-item-section>
        <q-item-section>
          <q-badge :color="item.status === 'ACTIVE' ? 'green' : 'grey'" :label="item.status" />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="schedule" /></q-item-section>
        <q-item-section caption>{{ $t('i18nMessage.createdDate') }}</q-item-section>
        <q-item-section>{{ item.createdDate }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="update" /></q-item-section>
        <q-item-section caption>{{ $t('i18nMessage.lastModifiedDate') }}</q-item-section>
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
