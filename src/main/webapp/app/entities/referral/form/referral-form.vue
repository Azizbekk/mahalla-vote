<script setup lang="ts">
import { useReferralForm } from './referral-form';
import type { ReferralDetail } from '../models/referral-detail.model';
import type { ReferralForm } from '../models/referral-form.model';

const props = defineProps<{
  item?: ReferralDetail | null;
  mode: 'create' | 'edit';
}>();

const emit = defineEmits<{
  (e: 'created', data: ReferralForm): void;
  (e: 'updated', data: ReferralForm): void;
  (e: 'cancel'): void;
}>();

const { state, meta, actions } = useReferralForm({ props, emit });
</script>

<template>
  <q-form :ref="el => (state.formRef = el)" @submit.prevent="actions.submit" greedy class="column no-wrap" style="max-height: 80vh">
    <q-inner-loading :showing="state.loadingData">
      <q-spinner-gears size="50px" color="primary" />
    </q-inner-loading>

    <div class="col q-pa-md" style="overflow-y: auto; min-height: 0">
      <div class="column q-gutter-md">
        <q-input
          v-model.number="state.formData.referrerId"
          :rules="meta.rules.referrerId"
          :label="$t('referral.referrerId') + ' *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="person" color="primary" /></template>
        </q-input>

        <q-input
          v-model.number="state.formData.referredId"
          :rules="meta.rules.referredId"
          :label="$t('referral.referredId') + ' *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="person_add" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.status"
          :options="state.statusOptions"
          :label="$t('referral.status')"
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
