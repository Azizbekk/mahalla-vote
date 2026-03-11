<template>
  <div>
    <div class="q-gutter-xs q-mb-md">
      <q-badge color="primary" class="cursor-pointer q-pa-sm" @click="threadDumpFilter = ''">
        All <q-badge color="white" text-color="primary" :label="threadDumpData.threadDumpAll" class="q-ml-xs" />
      </q-badge>
      <q-badge color="positive" class="cursor-pointer q-pa-sm" @click="threadDumpFilter = 'RUNNABLE'">
        Runnable <q-badge color="white" text-color="positive" :label="threadDumpData.threadDumpRunnable" class="q-ml-xs" />
      </q-badge>
      <q-badge color="info" class="cursor-pointer q-pa-sm" @click="threadDumpFilter = 'WAITING'">
        Waiting <q-badge color="white" text-color="info" :label="threadDumpData.threadDumpWaiting" class="q-ml-xs" />
      </q-badge>
      <q-badge color="warning" class="cursor-pointer q-pa-sm" @click="threadDumpFilter = 'TIMED_WAITING'">
        Timed Waiting <q-badge color="white" text-color="warning" :label="threadDumpData.threadDumpTimedWaiting" class="q-ml-xs" />
      </q-badge>
      <q-badge color="negative" class="cursor-pointer q-pa-sm" @click="threadDumpFilter = 'BLOCKED'">
        Blocked <q-badge color="white" text-color="negative" :label="threadDumpData.threadDumpBlocked" class="q-ml-xs" />
      </q-badge>
    </div>

    <q-input v-model="threadDumpFilter" label="Filter" outlined dense clearable class="q-mb-md" />

    <div v-for="(entry, idx) of filteredThreadDump" :key="idx" class="q-mb-md">
      <q-card flat bordered>
        <q-card-section>
          <div class="row items-center">
            <q-badge :color="getBadgeColor(entry.threadState)" :label="entry.threadState" class="q-mr-sm" />
            <span class="text-subtitle2">{{ entry.threadName }} (ID {{ entry.threadId }})</span>
            <q-space />
            <q-btn
              flat
              dense
              :icon="entry.show ? 'expand_less' : 'expand_more'"
              @click="entry.show = !entry.show"
              :label="entry.show ? t$('metrics.jvm.threads.dump.hide') : t$('metrics.jvm.threads.dump.show')"
            />
          </div>
        </q-card-section>

        <q-slide-transition>
          <div v-show="entry.show">
            <q-separator />
            <q-card-section class="bg-grey-1">
              <div v-for="(st, stIdx) of entry.stackTrace" :key="stIdx" class="text-caption" style="font-family: monospace">
                {{ st.className }}.{{ st.methodName }}(<code>{{ st.fileName }}:{{ st.lineNumber }}</code
                >)
              </div>
            </q-card-section>
          </div>
        </q-slide-transition>

        <q-separator />

        <q-card-section class="q-pa-none">
          <q-table
            flat
            dense
            :rows="[entry]"
            :columns="threadColumns"
            row-key="threadId"
            hide-pagination
            :pagination="{ rowsPerPage: 0 }"
          />
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { filterBy } from '@/shared/computables';

const props = defineProps<{
  threadDump: any[];
}>();

const { t: t$ } = useI18n();
const threadDumpFilter = ref('');

const filteredThreadDump = computed(() => filterBy(props.threadDump, { filterByTerm: threadDumpFilter.value }));

const threadDumpData = computed(() => {
  const data = {
    threadDumpAll: 0,
    threadDumpBlocked: 0,
    threadDumpRunnable: 0,
    threadDumpTimedWaiting: 0,
    threadDumpWaiting: 0,
  };
  if (props.threadDump) {
    props.threadDump.forEach(value => {
      if (value.threadState === 'RUNNABLE') data.threadDumpRunnable += 1;
      else if (value.threadState === 'WAITING') data.threadDumpWaiting += 1;
      else if (value.threadState === 'TIMED_WAITING') data.threadDumpTimedWaiting += 1;
      else if (value.threadState === 'BLOCKED') data.threadDumpBlocked += 1;
    });
    data.threadDumpAll = data.threadDumpRunnable + data.threadDumpWaiting + data.threadDumpTimedWaiting + data.threadDumpBlocked;
  }
  return data;
});

const threadColumns = [
  { name: 'blockedTime', label: t$('metrics.jvm.threads.dump.blockedtime'), field: 'blockedTime', align: 'left' as const },
  { name: 'blockedCount', label: t$('metrics.jvm.threads.dump.blockedcount'), field: 'blockedCount', align: 'left' as const },
  { name: 'waitedTime', label: t$('metrics.jvm.threads.dump.waitedtime'), field: 'waitedTime', align: 'left' as const },
  { name: 'waitedCount', label: t$('metrics.jvm.threads.dump.waitedcount'), field: 'waitedCount', align: 'left' as const },
  { name: 'lockName', label: t$('metrics.jvm.threads.dump.lockname'), field: 'lockName', align: 'left' as const },
];

function getBadgeColor(threadState: string): string {
  if (threadState === 'RUNNABLE') return 'positive';
  if (threadState === 'WAITING') return 'info';
  if (threadState === 'TIMED_WAITING') return 'warning';
  if (threadState === 'BLOCKED') return 'negative';
  return 'grey';
}
</script>
