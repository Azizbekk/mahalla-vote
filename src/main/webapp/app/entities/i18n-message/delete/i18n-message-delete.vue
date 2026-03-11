<script setup lang="ts">
import { ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { I18nMessageService } from '../services/i18n-message.service';
import type { I18nMessageDetail } from '../models/i18n-message-detail.model';

const props = defineProps<{ item: I18nMessageDetail | null }>();
const emit = defineEmits<{
  (e: 'deleted'): void;
  (e: 'cancel'): void;
}>();

const $q = useQuasar();
const { t } = useI18n();
const loading = ref(false);

async function confirmDelete() {
  if (!props.item) return;
  try {
    loading.value = true;
    await I18nMessageService.deleteById(props.item.id);
    $q.notify({ type: 'positive', message: t('entity.deleteSuccess') });
    emit('deleted');
  } catch (error: any) {
    $q.notify({ type: 'negative', message: error.message || t('entity.deleteFailed') });
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <q-card-section v-if="item">
    <p>{{ $t('entity.deleteConfirmMessage') }}</p>
    <p class="text-weight-bold">{{ item.shortCode }} (ID: {{ item.id }})</p>
  </q-card-section>
  <q-separator />
  <q-card-actions align="right">
    <q-btn flat :label="$t('entity.action.cancel')" @click="emit('cancel')" :disable="loading" />
    <q-btn
      unelevated
      color="negative"
      icon="delete_forever"
      :label="$t('entity.action.delete')"
      :loading="loading"
      @click="confirmDelete"
    />
  </q-card-actions>
</template>
