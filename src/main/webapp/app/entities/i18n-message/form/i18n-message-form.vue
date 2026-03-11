<script setup lang="ts">
import { useI18nMessageForm } from './i18n-message-form';
import type { I18nMessageDetail } from '../models/i18n-message-detail.model';
import type { I18nMessageForm } from '../models/i18n-message-form.model';

const props = defineProps<{
  item?: I18nMessageDetail | null;
  mode: 'create' | 'edit';
}>();

const emit = defineEmits<{
  (e: 'created', data: I18nMessageForm): void;
  (e: 'updated', data: I18nMessageForm): void;
  (e: 'cancel'): void;
}>();

const { state, meta, actions } = useI18nMessageForm({ props, emit });
</script>

<template>
  <q-form :ref="el => (state.formRef = el)" @submit.prevent="actions.submit" greedy class="column no-wrap" style="max-height: 80vh">
    <q-inner-loading :showing="state.loadingData">
      <q-spinner-gears size="50px" color="primary" />
    </q-inner-loading>

    <div class="col q-pa-md" style="overflow-y: auto; min-height: 0">
      <div class="column q-gutter-md">
        <q-input
          v-model="state.formData.shortCode"
          :rules="meta.rules.shortCode"
          :label="$t('i18nMessage.shortCode') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="code" color="primary" /></template>
        </q-input>

        <!-- Localized name inputs for each language -->
        <div class="text-subtitle2 q-mt-sm">{{ $t('i18nMessage.localizedName') }}</div>

        <q-input
          v-for="lang in meta.languages"
          :key="lang.key"
          v-model="(state.formData.localizedName as any)[lang.key]"
          :rules="lang.key === 'uzLat' ? meta.rules.uzLat : []"
          :label="lang.label + (lang.key === 'uzLat' ? ' *' : '')"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon :name="lang.icon" color="primary" /></template>
        </q-input>

        <q-input
          v-model.number="state.formData.sortOrder"
          :label="$t('i18nMessage.sortOrder')"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="sort" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.status"
          :options="state.statusOptions"
          :label="$t('i18nMessage.status')"
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
