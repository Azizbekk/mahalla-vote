<template>
  <div class="q-pa-md page-container">
    <div class="row items-center q-mb-md">
      <div class="text-h5 col" data-cy="healthPageHeading">
        {{ t$('health.title') }}
      </div>
      <div class="col-auto">
        <q-btn color="primary" icon="refresh" :label="t$('health.refreshButton')" :loading="updatingHealth" @click="refresh" outline />
      </div>
    </div>

    <q-table flat bordered :rows="healthData || []" :columns="columns" row-key="name" hide-pagination :pagination="{ rowsPerPage: 0 }">
      <template #body="props">
        <q-tr :props="props">
          <q-td key="service" :props="props">
            {{ t$('health.indicator.' + baseName(props.row.name)) }} {{ subSystemName(props.row.name) }}
          </q-td>
          <q-td key="status" :props="props" class="text-center">
            <q-badge :color="props.row.status === 'UP' ? 'positive' : 'negative'" :label="t$('health.status.' + props.row.status)" />
          </q-td>
          <q-td key="details" :props="props" class="text-center">
            <q-btn
              v-if="props.row.details || props.row.error"
              dense
              flat
              round
              icon="visibility"
              color="info"
              @click="showHealth(props.row)"
            />
          </q-td>
        </q-tr>
      </template>
    </q-table>

    <q-dialog v-model="showModal">
      <q-card style="min-width: 500px; max-width: 700px">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6" v-if="currentHealth">
            {{ t$('health.indicator.' + baseName(currentHealth.name)) }} {{ subSystemName(currentHealth.name) }}
          </div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section v-if="currentHealth">
          <health-modal :current-health="currentHealth" />
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import HealthService from './health.service';
import HealthModal from './health-modal.vue';

const { t: t$ } = useI18n();
const healthService = inject('healthService', () => new HealthService(), true);

const healthData = ref<any>(null);
const currentHealth = ref<any>(null);
const updatingHealth = ref(false);
const showModal = ref(false);

const columns = [
  { name: 'service', label: t$('health.table.service'), field: 'name', align: 'left' as const },
  { name: 'status', label: t$('health.table.status'), field: 'status', align: 'center' as const },
  { name: 'details', label: t$('health.details.details'), field: 'details', align: 'center' as const },
];

function baseName(name: string): string {
  return healthService.getBaseName(name);
}

function subSystemName(name: string): string {
  return healthService.getSubSystemName(name);
}

function refresh(): void {
  updatingHealth.value = true;
  healthService
    .checkHealth()
    .then(res => {
      healthData.value = healthService.transformHealthData(res.data);
      updatingHealth.value = false;
    })
    .catch((error: any) => {
      if (error.status === 503) {
        healthData.value = healthService.transformHealthData(error.error);
      }
      updatingHealth.value = false;
    });
}

function showHealth(health: any): void {
  currentHealth.value = health;
  showModal.value = true;
}

onMounted(() => {
  refresh();
});
</script>
