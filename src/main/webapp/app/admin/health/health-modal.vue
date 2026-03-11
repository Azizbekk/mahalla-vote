<template>
  <div>
    <div v-if="currentHealth && currentHealth.details">
      <div class="text-subtitle1 text-weight-bold q-mb-sm">{{ t$('health.details.properties') }}</div>
      <q-table
        flat
        bordered
        dense
        :rows="detailRows"
        :columns="detailColumns"
        row-key="key"
        hide-pagination
        :pagination="{ rowsPerPage: 0 }"
      />
    </div>
    <div v-if="currentHealth && currentHealth.error">
      <div class="text-subtitle1 text-weight-bold text-negative q-mb-sm">{{ t$('health.details.error') }}</div>
      <pre class="bg-grey-2 q-pa-sm rounded-borders" style="white-space: pre-wrap; word-break: break-all">{{ currentHealth.error }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import HealthService from './health.service';

const props = defineProps<{
  currentHealth: any;
}>();

const { t: t$ } = useI18n();
const healthService = inject('healthService', () => new HealthService(), true);

const detailColumns = [
  { name: 'key', label: t$('health.details.name'), field: 'key', align: 'left' as const },
  { name: 'value', label: t$('health.details.value'), field: 'value', align: 'left' as const },
];

const detailRows = computed(() => {
  if (!props.currentHealth?.details?.details) return [];
  return Object.entries(props.currentHealth.details.details).map(([key, value]) => ({
    key,
    value: readableValue(value),
  }));
});

function readableValue(value: any): string {
  if (props.currentHealth.name === 'diskSpace') {
    const val = value / 1073741824;
    if (val > 1) return `${val.toFixed(2)} GB`;
    return `${(value / 1048576).toFixed(2)} MB`;
  }
  if (typeof value === 'object') return JSON.stringify(value);
  return value.toString();
}
</script>
