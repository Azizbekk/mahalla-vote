<template>
  <div class="q-pa-md page-container">
    <div class="row items-center q-mb-md">
      <div class="text-h5 col" data-cy="configurationPageHeading">
        {{ t$('configuration.title') }}
      </div>
    </div>

    <div v-if="allConfiguration && configuration.length > 0">
      <q-input v-model="filtered" :label="t$('configuration.filter')" outlined dense clearable class="q-mb-md" style="max-width: 400px">
        <template #prepend>
          <q-icon name="search" />
        </template>
      </q-input>

      <div class="text-h6 q-mb-sm">Spring configuration</div>

      <q-table
        flat
        bordered
        dense
        :rows="filteredConfiguration"
        :columns="springColumns"
        row-key="prefix"
        hide-pagination
        :pagination="{ rowsPerPage: 0 }"
        class="q-mb-lg"
      >
        <template #header="props">
          <q-tr :props="props">
            <q-th v-for="col in props.cols" :key="col.name" :props="props" class="cursor-pointer" @click="changeOrder(col.field as string)">
              {{ col.label }}
            </q-th>
          </q-tr>
        </template>

        <template #body="props">
          <q-tr :props="props">
            <q-td key="prefix" :props="props">
              <strong>{{ props.row.prefix }}</strong>
            </q-td>
            <q-td key="properties" :props="props">
              <div v-for="key in keys(props.row.properties)" :key="key" class="row q-py-xs">
                <div class="col-4 text-weight-medium">{{ key }}</div>
                <div class="col-8">
                  <q-badge color="grey-3" text-color="grey-9" class="q-pa-xs" style="word-break: break-all">
                    {{ props.row.properties[key] }}
                  </q-badge>
                </div>
              </div>
            </q-td>
          </q-tr>
        </template>
      </q-table>

      <div v-for="key in keys(allConfiguration)" :key="key" class="q-mb-lg">
        <div class="text-h6 q-mb-sm">{{ key }}</div>
        <q-table
          flat
          bordered
          dense
          :rows="allConfiguration[key]"
          :columns="envColumns"
          row-key="key"
          hide-pagination
          :pagination="{ rowsPerPage: 0 }"
        >
          <template #body="props">
            <q-tr :props="props">
              <q-td key="key" :props="props" style="word-break: break-all">{{ props.row.key }}</q-td>
              <q-td key="val" :props="props">
                <q-badge color="grey-3" text-color="grey-9" class="q-pa-xs" style="word-break: break-all">
                  {{ props.row.val }}
                </q-badge>
              </q-td>
            </q-tr>
          </template>
        </q-table>
      </div>
    </div>

    <div v-else class="text-center q-pa-xl text-grey-6">
      <q-spinner-dots color="primary" size="40px" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import ConfigurationService from './configuration.service';
import { orderAndFilterBy } from '@/shared/computables';

const { t: t$ } = useI18n();
const configurationService = inject('configurationService', () => new ConfigurationService(), true);

const orderProp = ref('prefix');
const reverse = ref(false);
const allConfiguration = ref<any>({});
const configuration = ref<any[]>([]);
const filtered = ref('');

const springColumns = [
  { name: 'prefix', label: t$('configuration.table.prefix'), field: 'prefix', align: 'left' as const, style: 'width: 40%' },
  { name: 'properties', label: t$('configuration.table.properties'), field: 'properties', align: 'left' as const, style: 'width: 60%' },
];

const envColumns = [
  { name: 'key', label: 'Property', field: 'key', align: 'left' as const, style: 'width: 40%' },
  { name: 'val', label: 'Value', field: 'val', align: 'left' as const, style: 'width: 60%' },
];

const filteredConfiguration = computed(() =>
  orderAndFilterBy(configuration.value, {
    filterByTerm: filtered.value,
    orderByProp: orderProp.value,
    reverse: reverse.value,
  }),
);

function changeOrder(prop: string): void {
  if (orderProp.value === prop) {
    reverse.value = !reverse.value;
  } else {
    orderProp.value = prop;
    reverse.value = false;
  }
}

function keys(dict: any): string[] {
  return dict === undefined ? [] : Object.keys(dict);
}

onMounted(() => {
  configurationService.loadConfiguration().then(res => {
    configuration.value = res;
  });
  configurationService.loadEnvConfiguration().then(res => {
    allConfiguration.value = res;
  });
});
</script>
