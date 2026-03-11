<script setup lang="ts">
import { useVoteLotForm } from './vote-lot-form';
import type { VoteLotDetail } from '../models/vote-lot-detail.model';
import type { VoteLotForm } from '../models/vote-lot-form.model';

const props = defineProps<{
  item?: VoteLotDetail | null;
  mode: 'create' | 'edit';
}>();

const emit = defineEmits<{
  (e: 'created', data: VoteLotForm): void;
  (e: 'updated', data: VoteLotForm): void;
  (e: 'cancel'): void;
}>();

const { state, meta, actions } = useVoteLotForm({ props, emit });
</script>

<template>
  <q-form :ref="el => (state.formRef = el)" @submit.prevent="actions.submit" greedy class="column no-wrap" style="max-height: 80vh">
    <q-inner-loading :showing="state.loadingData">
      <q-spinner-gears size="50px" color="primary" />
    </q-inner-loading>

    <div class="col q-pa-md" style="overflow-y: auto; min-height: 0">
      <div class="column q-gutter-md">
        <q-input
          v-model="state.formData.name"
          :rules="meta.rules.name"
          :label="$t('voteLot.name') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="campaign" color="primary" /></template>
        </q-input>

        <q-input
          v-model="state.formData.openBudgetUrl"
          :rules="meta.rules.openBudgetUrl"
          :label="$t('voteLot.openBudgetUrl') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="link" color="primary" /></template>
        </q-input>

        <q-input
          v-model.number="state.formData.targetVoteCount"
          :rules="meta.rules.targetVoteCount"
          :label="$t('voteLot.targetVoteCount') + ' *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="flag" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.status"
          :options="state.statusOptions"
          :label="$t('voteLot.status')"
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
