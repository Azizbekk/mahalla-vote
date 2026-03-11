<template>
  <div class="q-pa-md page-container">
    <div class="row items-center q-mb-md">
      <div class="text-h5 col" data-cy="metricsPageHeading">
        {{ t$('metrics.title') }}
      </div>
      <div class="col-auto">
        <q-btn color="primary" icon="refresh" :label="t$('metrics.refreshButton')" @click="refresh" outline />
      </div>
    </div>

    <div v-if="!updatingMetrics" class="q-gutter-md">
      <!-- JVM Memory -->
      <q-card flat bordered>
        <q-card-section>
          <div class="text-h6">{{ t$('metrics.jvm.memory.title') }}</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <div v-for="(entry, key) of metrics.jvm" :key="key" class="q-mb-md">
            <div class="text-body2 q-mb-xs">
              <span v-if="entry.max !== -1">
                {{ key }} ({{ formatNumber1(entry.used / 1048576) }}M / {{ formatNumber1(entry.max / 1048576) }}M)
              </span>
              <span v-else> {{ key }} {{ formatNumber1(entry.used / 1048576) }}M </span>
            </div>
            <div class="text-caption text-grey-7 q-mb-xs">Committed: {{ formatNumber1(entry.committed / 1048576) }}M</div>
            <q-linear-progress v-if="entry.max !== -1" :value="entry.used / entry.max" color="positive" stripe rounded size="20px">
              <div class="absolute-full flex flex-center">
                <span class="text-caption text-white text-weight-bold">{{ formatNumber1((entry.used * 100) / entry.max) }}%</span>
              </div>
            </q-linear-progress>
          </div>
        </q-card-section>
      </q-card>

      <!-- Threads -->
      <q-card flat bordered>
        <q-card-section>
          <div class="text-h6">{{ t$('metrics.jvm.threads.title') }}</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <div class="q-mb-sm">
            <span class="text-body2">{{ t$('metrics.jvm.threads.runnable') }} {{ threadStats.threadDumpRunnable }}</span>
            <q-linear-progress
              :value="threadStats.threadDumpAll ? threadStats.threadDumpRunnable / threadStats.threadDumpAll : 0"
              color="positive"
              stripe
              rounded
              size="16px"
              class="q-mt-xs"
            />
          </div>
          <div class="q-mb-sm">
            <span class="text-body2">{{ t$('metrics.jvm.threads.timedwaiting') }} ({{ threadStats.threadDumpTimedWaiting }})</span>
            <q-linear-progress
              :value="threadStats.threadDumpAll ? threadStats.threadDumpTimedWaiting / threadStats.threadDumpAll : 0"
              color="warning"
              stripe
              rounded
              size="16px"
              class="q-mt-xs"
            />
          </div>
          <div class="q-mb-sm">
            <span class="text-body2">{{ t$('metrics.jvm.threads.waiting') }} ({{ threadStats.threadDumpWaiting }})</span>
            <q-linear-progress
              :value="threadStats.threadDumpAll ? threadStats.threadDumpWaiting / threadStats.threadDumpAll : 0"
              color="info"
              stripe
              rounded
              size="16px"
              class="q-mt-xs"
            />
          </div>
          <div class="q-mb-sm">
            <span class="text-body2">{{ t$('metrics.jvm.threads.blocked') }} ({{ threadStats.threadDumpBlocked }})</span>
            <q-linear-progress
              :value="threadStats.threadDumpAll ? threadStats.threadDumpBlocked / threadStats.threadDumpAll : 0"
              color="negative"
              stripe
              rounded
              size="16px"
              class="q-mt-xs"
            />
          </div>
          <div class="q-mt-md">
            Total: {{ threadStats.threadDumpAll }}
            <q-btn dense flat round icon="visibility" color="info" @click="showThreadDumpModal = true" class="q-ml-sm" />
          </div>
        </q-card-section>
      </q-card>

      <!-- System -->
      <q-card flat bordered>
        <q-card-section>
          <div class="text-h6">System</div>
        </q-card-section>
        <q-card-section class="q-pt-none" v-if="metrics.processMetrics">
          <q-list dense separator>
            <q-item>
              <q-item-section>Uptime</q-item-section>
              <q-item-section side>{{ convertMillisecondsToDuration(metrics.processMetrics['process.uptime']) }}</q-item-section>
            </q-item>
            <q-item>
              <q-item-section>Start time</q-item-section>
              <q-item-section side>{{ formatDate(metrics.processMetrics['process.start.time']) }}</q-item-section>
            </q-item>
            <q-item>
              <q-item-section>Process CPU usage</q-item-section>
              <q-item-section side>{{ formatNumber2(100 * metrics.processMetrics['process.cpu.usage']) }} %</q-item-section>
            </q-item>
          </q-list>
          <q-linear-progress
            :value="metrics.processMetrics['process.cpu.usage']"
            color="positive"
            stripe
            rounded
            size="16px"
            class="q-my-sm"
          />
          <q-list dense separator>
            <q-item>
              <q-item-section>System CPU usage</q-item-section>
              <q-item-section side>{{ formatNumber2(100 * metrics.processMetrics['system.cpu.usage']) }} %</q-item-section>
            </q-item>
          </q-list>
          <q-linear-progress
            :value="metrics.processMetrics['system.cpu.usage']"
            color="positive"
            stripe
            rounded
            size="16px"
            class="q-my-sm"
          />
          <q-list dense separator>
            <q-item>
              <q-item-section>System CPU count</q-item-section>
              <q-item-section side>{{ metrics.processMetrics['system.cpu.count'] }}</q-item-section>
            </q-item>
            <q-item>
              <q-item-section>System 1m Load average</q-item-section>
              <q-item-section side>{{ formatNumber2(metrics.processMetrics['system.load.average.1m']) }}</q-item-section>
            </q-item>
            <q-item>
              <q-item-section>Process files max</q-item-section>
              <q-item-section side>{{ formatNumber1(metrics.processMetrics['process.files.max']) }}</q-item-section>
            </q-item>
            <q-item>
              <q-item-section>Process files open</q-item-section>
              <q-item-section side>{{ formatNumber1(metrics.processMetrics['process.files.open']) }}</q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Garbage Collector -->
      <q-card flat bordered v-if="isObjectExisting(metrics, 'garbageCollector')">
        <q-card-section>
          <div class="text-h6">{{ t$('metrics.jvm.gc.title') }}</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <div class="row q-col-gutter-md q-mb-md">
            <div class="col-12 col-md-4">
              <div class="text-body2 q-mb-xs">
                GC Live Data Size / GC Max Data Size ({{ formatNumber1(metrics.garbageCollector['jvm.gc.live.data.size'] / 1048576) }}M /
                {{ formatNumber1(metrics.garbageCollector['jvm.gc.max.data.size'] / 1048576) }}M)
              </div>
              <q-linear-progress
                :value="
                  metrics.garbageCollector['jvm.gc.max.data.size']
                    ? metrics.garbageCollector['jvm.gc.live.data.size'] / metrics.garbageCollector['jvm.gc.max.data.size']
                    : 0
                "
                color="positive"
                stripe
                rounded
                size="16px"
              />
            </div>
            <div class="col-12 col-md-4">
              <div class="text-body2 q-mb-xs">
                GC Memory Promoted / GC Memory Allocated ({{ formatNumber1(metrics.garbageCollector['jvm.gc.memory.promoted'] / 1048576) }}M
                / {{ formatNumber1(metrics.garbageCollector['jvm.gc.memory.allocated'] / 1048576) }}M)
              </div>
              <q-linear-progress
                :value="
                  metrics.garbageCollector['jvm.gc.memory.allocated']
                    ? metrics.garbageCollector['jvm.gc.memory.promoted'] / metrics.garbageCollector['jvm.gc.memory.allocated']
                    : 0
                "
                color="positive"
                stripe
                rounded
                size="16px"
              />
            </div>
            <div class="col-12 col-md-4">
              <q-list dense>
                <q-item>
                  <q-item-section>Classes loaded</q-item-section>
                  <q-item-section side>{{ metrics.garbageCollector.classesLoaded }}</q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>Classes unloaded</q-item-section>
                  <q-item-section side>{{ metrics.garbageCollector.classesUnloaded }}</q-item-section>
                </q-item>
              </q-list>
            </div>
          </div>

          <q-table
            v-if="metrics.garbageCollector['jvm.gc.pause']"
            flat
            bordered
            dense
            :rows="[metrics.garbageCollector['jvm.gc.pause']]"
            :columns="gcColumns"
            row-key="count"
            hide-pagination
            :pagination="{ rowsPerPage: 0 }"
          />
        </q-card-section>
      </q-card>

      <!-- HTTP Requests -->
      <q-card flat bordered v-if="isObjectExisting(metrics, 'http.server.requests')">
        <q-card-section>
          <div class="text-h6">{{ t$('metrics.jvm.http.title') }}</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-table
            flat
            bordered
            dense
            :rows="httpRows"
            :columns="httpColumns"
            row-key="code"
            hide-pagination
            :pagination="{ rowsPerPage: 0 }"
          >
            <template #body-cell-count="cellProps">
              <q-td :props="cellProps">
                <q-linear-progress
                  :value="
                    metrics['http.server.requests']['all'].count ? cellProps.row.count / metrics['http.server.requests']['all'].count : 0
                  "
                  color="positive"
                  rounded
                  size="20px"
                >
                  <div class="absolute-full flex flex-center">
                    <span class="text-caption text-white text-weight-bold">{{ formatNumber1(cellProps.row.count) }}</span>
                  </div>
                </q-linear-progress>
              </q-td>
            </template>
          </q-table>
        </q-card-section>
      </q-card>

      <!-- Endpoints -->
      <q-card flat bordered v-if="metrics.services">
        <q-card-section>
          <div class="text-h6">Endpoints requests (time in millisecond)</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-table
            flat
            bordered
            dense
            :rows="endpointRows"
            :columns="endpointColumns"
            row-key="id"
            hide-pagination
            :pagination="{ rowsPerPage: 0 }"
          />
        </q-card-section>
      </q-card>

      <!-- Cache -->
      <q-card flat bordered v-if="isObjectExisting(metrics, 'cache')">
        <q-card-section>
          <div class="text-h6">{{ t$('metrics.cache.title') }}</div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-table
            flat
            bordered
            dense
            :rows="cacheRows"
            :columns="cacheColumns"
            row-key="name"
            hide-pagination
            :pagination="{ rowsPerPage: 0 }"
          />
        </q-card-section>
      </q-card>

      <!-- Database -->
      <q-card flat bordered v-if="isObjectExistingAndNotEmpty(metrics, 'databases')">
        <q-card-section>
          <div class="text-h6">
            {{ t$('metrics.datasource.title') }}
            <span class="text-body2 text-grey-7 q-ml-sm">
              (active: {{ metrics.databases.active.value }}, min: {{ metrics.databases.min.value }}, max: {{ metrics.databases.max.value }},
              idle: {{ metrics.databases.idle.value }})
            </span>
          </div>
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-table
            flat
            bordered
            dense
            :rows="dbRows"
            :columns="dbColumns"
            row-key="name"
            hide-pagination
            :pagination="{ rowsPerPage: 0 }"
          />
        </q-card-section>
      </q-card>
    </div>

    <div v-else class="flex flex-center q-pa-xl">
      <q-spinner color="primary" size="3em" />
    </div>

    <!-- Thread Dump Modal -->
    <q-dialog v-model="showThreadDumpModal" maximized>
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">{{ t$('metrics.jvm.threads.dump.title') }}</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>
        <q-card-section>
          <metrics-modal :thread-dump="threadData" />
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, computed, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import MetricsService from './metrics.service';
import MetricsModal from './metrics-modal.vue';
import { useDateFormat } from '@/shared/composables';

const { t: t$ } = useI18n();
const { formatDate } = useDateFormat();
const metricsService = inject('metricsService', () => new MetricsService(), true);

const metrics = ref<any>({});
const threadData = ref<any>(null);
const threadStats = ref<any>({});
const updatingMetrics = ref(true);
const showThreadDumpModal = ref(false);

const gcColumns = [
  { name: 'count', label: 'Count', field: 'count', align: 'right' as const },
  { name: 'mean', label: 'Mean', field: 'mean', align: 'right' as const, format: (v: any) => formatNumber2(v) },
  { name: 'min', label: 'Min', field: '0.0', align: 'right' as const, format: (v: any) => formatNumber2(v) },
  { name: 'p50', label: 'p50', field: '0.5', align: 'right' as const, format: (v: any) => formatNumber2(v) },
  { name: 'p75', label: 'p75', field: '0.75', align: 'right' as const, format: (v: any) => formatNumber2(v) },
  { name: 'p95', label: 'p95', field: '0.95', align: 'right' as const, format: (v: any) => formatNumber2(v) },
  { name: 'p99', label: 'p99', field: '0.99', align: 'right' as const, format: (v: any) => formatNumber2(v) },
  { name: 'max', label: 'Max', field: 'max', align: 'right' as const, format: (v: any) => formatNumber2(v) },
];

const httpColumns = [
  { name: 'code', label: t$('metrics.jvm.http.table.code'), field: 'code', align: 'left' as const },
  { name: 'count', label: t$('metrics.jvm.http.table.count'), field: 'count', align: 'left' as const },
  {
    name: 'mean',
    label: t$('metrics.jvm.http.table.mean'),
    field: 'mean',
    align: 'right' as const,
    format: (v: any) => formatNumber2(filterNaN(v)),
  },
  { name: 'max', label: t$('metrics.jvm.http.table.max'), field: 'max', align: 'right' as const, format: (v: any) => formatNumber2(v) },
];

const httpRows = computed(() => {
  if (!metrics.value['http.server.requests']?.percode) return [];
  return Object.entries(metrics.value['http.server.requests'].percode).map(([code, entry]: [string, any]) => ({
    code,
    count: entry.count,
    mean: entry.mean,
    max: entry.max,
  }));
});

const endpointColumns = [
  { name: 'method', label: 'Method', field: 'method', align: 'left' as const },
  { name: 'url', label: 'Endpoint url', field: 'url', align: 'left' as const },
  { name: 'count', label: 'Count', field: 'count', align: 'right' as const },
  { name: 'mean', label: 'Mean', field: 'mean', align: 'right' as const, format: (v: any) => formatNumber2(v) },
];

const endpointRows = computed(() => {
  if (!metrics.value.services) return [];
  const rows: any[] = [];
  for (const [url, methods] of Object.entries(metrics.value.services)) {
    for (const [method, data] of Object.entries(methods as any)) {
      rows.push({ id: `${url}-${method}`, method, url, count: (data as any).count, mean: (data as any).mean });
    }
  }
  return rows;
});

const cacheColumns = [
  { name: 'name', label: t$('metrics.cache.cachename'), field: 'name', align: 'left' as const },
  { name: 'hits', label: 'Cache Hits', field: 'hits', align: 'right' as const },
  { name: 'misses', label: 'Cache Misses', field: 'misses', align: 'right' as const },
  { name: 'gets', label: 'Cache Gets', field: 'gets', align: 'right' as const },
  { name: 'puts', label: 'Cache Puts', field: 'puts', align: 'right' as const },
  { name: 'removals', label: 'Cache Removals', field: 'removals', align: 'right' as const },
  { name: 'evictions', label: 'Cache Evictions', field: 'evictions', align: 'right' as const },
  { name: 'hitPercent', label: 'Cache Hit %', field: 'hitPercent', align: 'right' as const },
  { name: 'missPercent', label: 'Cache Miss %', field: 'missPercent', align: 'right' as const },
];

const cacheRows = computed(() => {
  if (!metrics.value.cache) return [];
  return Object.entries(metrics.value.cache).map(([name, entry]: [string, any]) => {
    const hits = entry['cache.gets.hit'];
    const misses = entry['cache.gets.miss'];
    const total = hits + misses;
    return {
      name,
      hits,
      misses,
      gets: total,
      puts: entry['cache.puts'],
      removals: entry['cache.removals'],
      evictions: entry['cache.evictions'],
      hitPercent: formatNumber2(filterNaN((100 * hits) / total)),
      missPercent: formatNumber2(filterNaN((100 * misses) / total)),
    };
  });
});

const dbColumns = [
  { name: 'name', label: '', field: 'name', align: 'left' as const },
  { name: 'count', label: t$('metrics.datasource.count'), field: 'count', align: 'right' as const },
  { name: 'mean', label: t$('metrics.datasource.mean'), field: 'mean', align: 'right' as const },
  { name: 'min', label: 'Min', field: 'min', align: 'right' as const },
  { name: 'p50', label: 'p50', field: 'p50', align: 'right' as const },
  { name: 'p75', label: 'p75', field: 'p75', align: 'right' as const },
  { name: 'p95', label: 'p95', field: 'p95', align: 'right' as const },
  { name: 'p99', label: 'p99', field: 'p99', align: 'right' as const },
  { name: 'max', label: t$('metrics.datasource.max'), field: 'max', align: 'right' as const },
];

const dbRows = computed(() => {
  if (!metrics.value.databases) return [];
  return ['acquire', 'creation', 'usage']
    .map(name => {
      const db = metrics.value.databases[name];
      if (!db) return null;
      return {
        name: name.charAt(0).toUpperCase() + name.slice(1),
        count: db.count,
        mean: formatNumber2(filterNaN(db.mean)),
        min: formatNumber2(db['0.0']),
        p50: formatNumber2(db['0.5']),
        p75: formatNumber2(db['0.75']),
        p95: formatNumber2(db['0.95']),
        p99: formatNumber2(db['0.99']),
        max: formatNumber2(filterNaN(db.max)),
      };
    })
    .filter(Boolean);
});

function formatNumber1(value: any): string {
  return Number(value).toLocaleString('en-US', { maximumFractionDigits: 0 });
}

function formatNumber2(value: any): string {
  return Number(value).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function filterNaN(input: any): number {
  return isNaN(input) ? 0 : input;
}

function isObjectExisting(obj: any, key: string): boolean {
  return obj && obj[key];
}

function isObjectExistingAndNotEmpty(obj: any, key: string): boolean {
  return isObjectExisting(obj, key) && JSON.stringify(obj[key]) !== '{}';
}

function convertMillisecondsToDuration(ms: number): string {
  const times: Record<string, number> = {
    year: 31557600000,
    month: 2629746000,
    day: 86400000,
    hour: 3600000,
    minute: 60000,
    second: 1000,
  };
  let timeString = '';
  for (const key in times) {
    const count = Math.floor(ms / times[key]);
    if (count > 0) {
      timeString += `${count} ${key}${count > 1 ? 's' : ''} `;
      ms = ms - times[key] * count;
    }
  }
  return timeString;
}

function refresh(): void {
  updatingMetrics.value = true;
  metricsService
    .getMetrics()
    .then(resultsMetrics => {
      metrics.value = resultsMetrics.data;
      return metricsService.retrieveThreadDump();
    })
    .then(res => {
      threadData.value = res.data.threads;
      threadStats.value = {
        threadDumpRunnable: 0,
        threadDumpWaiting: 0,
        threadDumpTimedWaiting: 0,
        threadDumpBlocked: 0,
        threadDumpAll: 0,
      };
      threadData.value.forEach((value: any) => {
        if (value.threadState === 'RUNNABLE') threadStats.value.threadDumpRunnable += 1;
        else if (value.threadState === 'WAITING') threadStats.value.threadDumpWaiting += 1;
        else if (value.threadState === 'TIMED_WAITING') threadStats.value.threadDumpTimedWaiting += 1;
        else if (value.threadState === 'BLOCKED') threadStats.value.threadDumpBlocked += 1;
      });
      threadStats.value.threadDumpAll =
        threadStats.value.threadDumpRunnable +
        threadStats.value.threadDumpWaiting +
        threadStats.value.threadDumpTimedWaiting +
        threadStats.value.threadDumpBlocked;
      updatingMetrics.value = false;
    })
    .catch(() => {
      updatingMetrics.value = false;
    });
}

onMounted(() => {
  refresh();
});
</script>
