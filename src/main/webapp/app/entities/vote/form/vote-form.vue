<script setup lang="ts">
import { useVoteForm } from './vote-form';
import type { VoteDetail } from '../models/vote-detail.model';
import type { VoteForm } from '../models/vote-form.model';

const props = defineProps<{
  item?: VoteDetail | null;
  mode: 'create' | 'edit';
}>();

const emit = defineEmits<{
  (e: 'created', data: VoteForm): void;
  (e: 'updated', data: VoteForm): void;
  (e: 'cancel'): void;
}>();

const { state, meta, actions } = useVoteForm({ props, emit });
</script>

<template>
  <q-form :ref="el => (state.formRef = el)" @submit.prevent="actions.submit" greedy class="column no-wrap" style="max-height: 80vh">
    <q-inner-loading :showing="state.loadingData">
      <q-spinner-gears size="50px" color="primary" />
    </q-inner-loading>

    <div class="col q-pa-md" style="overflow-y: auto; min-height: 0">
      <div class="column q-gutter-md">
        <q-input
          v-model.number="state.formData.voterId"
          :rules="meta.rules.voterId"
          :label="$t('vote.voterId') + ' *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="person" color="primary" /></template>
        </q-input>

        <q-input
          v-model="state.formData.phoneNumber"
          :rules="meta.rules.phoneNumber"
          :label="$t('vote.phoneNumber') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="phone" color="primary" /></template>
        </q-input>

        <q-input
          v-model.number="state.formData.amount"
          :rules="meta.rules.amount"
          :label="$t('vote.amount') + ' *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="payments" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.status"
          :options="state.statusOptions"
          :label="$t('vote.status')"
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
