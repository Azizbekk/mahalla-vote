<script setup lang="ts">
import { useProjectSettingForm } from './project-setting-form';
import type { ProjectSettingDetail } from '../models/project-setting-detail.model';
import type { ProjectSettingForm } from '../models/project-setting-form.model';

const props = defineProps<{
  item?: ProjectSettingDetail | null;
  mode: 'create' | 'edit';
}>();

const emit = defineEmits<{
  (e: 'created', data: ProjectSettingForm): void;
  (e: 'updated', data: ProjectSettingForm): void;
  (e: 'cancel'): void;
}>();

const { state, meta, actions } = useProjectSettingForm({ props, emit });
</script>

<template>
  <q-form :ref="el => (state.formRef = el)" @submit.prevent="actions.submit" greedy class="column no-wrap" style="max-height: 80vh">
    <q-inner-loading :showing="state.loadingData">
      <q-spinner-gears size="50px" color="primary" />
    </q-inner-loading>

    <div class="col q-pa-md" style="overflow-y: auto; min-height: 0">
      <div class="column q-gutter-md">
        <q-input
          v-model="state.formData.settingKey"
          :rules="meta.rules.settingKey"
          :label="$t('projectSetting.settingKey') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="vpn_key" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.valueType"
          :options="state.valueTypeOptions"
          :rules="meta.rules.valueType"
          :label="$t('projectSetting.valueType') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="category" color="primary" /></template>
        </q-select>

        <!-- Dynamic value input based on valueType -->
        <q-input
          v-if="state.formData.valueType === 'PASSWORD'"
          v-model="state.formData.settingValue"
          :rules="meta.rules.settingValue"
          :label="$t('projectSetting.settingValue') + ' *'"
          :type="state.showPassword ? 'text' : 'password'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="lock" color="primary" /></template>
          <template #append>
            <q-icon
              :name="state.showPassword ? 'visibility_off' : 'visibility'"
              class="cursor-pointer"
              @click="state.showPassword = !state.showPassword"
            />
          </template>
        </q-input>

        <q-input
          v-else-if="state.formData.valueType === 'NUMBER'"
          v-model="state.formData.settingValue"
          :rules="meta.rules.settingValue"
          :label="$t('projectSetting.settingValue') + ' *'"
          type="number"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="pin" color="primary" /></template>
        </q-input>

        <q-toggle
          v-else-if="state.formData.valueType === 'BOOLEAN'"
          v-model="state.formData.settingValue"
          true-value="true"
          false-value="false"
          :label="$t('projectSetting.settingValue')"
          :disable="state.loading"
        />

        <q-input
          v-else-if="state.formData.valueType === 'JSON'"
          v-model="state.formData.settingValue"
          :rules="meta.rules.settingValue"
          :label="$t('projectSetting.settingValue') + ' *'"
          type="textarea"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="data_object" color="primary" /></template>
        </q-input>

        <q-input
          v-else
          v-model="state.formData.settingValue"
          :rules="meta.rules.settingValue"
          :label="$t('projectSetting.settingValue') + ' *'"
          outlined
          dense
          :disable="state.loading"
        >
          <template #prepend><q-icon name="text_fields" color="primary" /></template>
        </q-input>

        <q-input v-model="state.formData.description" :label="$t('projectSetting.description')" outlined dense :disable="state.loading">
          <template #prepend><q-icon name="description" color="primary" /></template>
        </q-input>

        <q-select
          v-model="state.formData.status"
          :options="state.statusOptions"
          :rules="meta.rules.status"
          :label="$t('projectSetting.status') + ' *'"
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
