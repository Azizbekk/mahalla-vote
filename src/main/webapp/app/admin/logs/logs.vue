<template>
  <div class="q-pa-md page-container">
    <div class="row items-center q-mb-md">
      <div class="text-h5 col" data-cy="logsPageHeading">
        {{ t$('logs.title') }}
      </div>
    </div>

    <div v-if="loggers.length > 0">
      <p>{{ t$('logs.nbloggers', { total: loggers.length }) }}</p>

      <q-input v-model="filtered" :label="t$('logs.filter')" outlined dense clearable class="q-mb-md" style="max-width: 400px">
        <template #prepend>
          <q-icon name="search" />
        </template>
      </q-input>

      <q-table
        flat
        bordered
        dense
        :rows="filteredLoggers"
        :columns="columns"
        row-key="name"
        hide-pagination
        :pagination="{ rowsPerPage: 0 }"
        virtual-scroll
        :virtual-scroll-item-size="40"
        style="max-height: 70vh"
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
            <q-td key="name" :props="props">
              <small>{{ props.row.name }}</small>
            </q-td>
            <q-td key="level" :props="props">
              <q-btn-group flat>
                <q-btn
                  v-for="level in levels"
                  :key="level.name"
                  dense
                  no-caps
                  size="sm"
                  :color="props.row.level === level.name ? level.color : 'grey-4'"
                  :text-color="props.row.level === level.name ? 'white' : 'grey-8'"
                  :label="level.name"
                  @click="updateLevel(props.row.name, level.name)"
                />
              </q-btn-group>
            </q-td>
          </q-tr>
        </template>
      </q-table>
    </div>

    <div v-else class="text-center q-pa-xl text-grey-6">
      <q-spinner-dots color="primary" size="40px" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, inject, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import LogsService from './logs.service';
import { orderAndFilterBy } from '@/shared/computables';

const { t: t$ } = useI18n();
const logsService = inject('logsService', () => new LogsService(), true);

const loggers = ref<any[]>([]);
const filtered = ref('');
const orderProp = ref('name');
const reverse = ref(false);

const levels = [
  { name: 'TRACE', color: 'primary' },
  { name: 'DEBUG', color: 'positive' },
  { name: 'INFO', color: 'info' },
  { name: 'WARN', color: 'warning' },
  { name: 'ERROR', color: 'negative' },
  { name: 'OFF', color: 'grey-7' },
];

const columns = [
  { name: 'name', label: t$('logs.table.name'), field: 'name', align: 'left' as const },
  { name: 'level', label: t$('logs.table.level'), field: 'level', align: 'left' as const },
];

const filteredLoggers = computed(() =>
  orderAndFilterBy(loggers.value, {
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

function extractLoggers(response: any): void {
  loggers.value = [];
  if (response.data) {
    for (const key of Object.keys(response.data.loggers)) {
      const logger = response.data.loggers[key];
      loggers.value.push({ name: key, level: logger.effectiveLevel });
    }
  }
}

function init(): void {
  logsService.findAll().then(response => {
    extractLoggers(response);
  });
}

function updateLevel(name: string, level: string): void {
  logsService.changeLevel(name, level).then(() => {
    init();
  });
}

onMounted(() => {
  init();
});
</script>
