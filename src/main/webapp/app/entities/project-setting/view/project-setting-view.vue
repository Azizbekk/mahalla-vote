<script setup lang="ts">
import { ref } from 'vue';
import type { ProjectSettingDetail } from '../models/project-setting-detail.model';

defineProps<{ item: ProjectSettingDetail | null }>();
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'edit'): void;
  (e: 'delete'): void;
}>();

const showPassword = ref(false);

function displayValue(item: ProjectSettingDetail): string {
  if (item.valueType === 'PASSWORD') {
    return showPassword.value ? item.settingValue || '' : '••••••••';
  }
  return item.settingValue || '';
}
</script>

<template>
  <q-card-section v-if="item">
    <q-list separator>
      <q-item>
        <q-item-section side><q-icon name="key" /></q-item-section>
        <q-item-section caption>ID</q-item-section>
        <q-item-section>{{ item.id }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="vpn_key" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.settingKey') }}</q-item-section>
        <q-item-section>{{ item.settingKey }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="category" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.valueType') }}</q-item-section>
        <q-item-section>
          <q-badge
            :color="
              item.valueType === 'PASSWORD'
                ? 'deep-purple'
                : item.valueType === 'NUMBER'
                  ? 'blue'
                  : item.valueType === 'BOOLEAN'
                    ? 'teal'
                    : item.valueType === 'JSON'
                      ? 'orange'
                      : 'grey'
            "
            :label="item.valueType"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="text_fields" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.settingValue') }}</q-item-section>
        <q-item-section>
          {{ displayValue(item) }}
          <q-btn
            v-if="item.valueType === 'PASSWORD'"
            flat
            dense
            round
            size="sm"
            :icon="showPassword ? 'visibility_off' : 'visibility'"
            @click="showPassword = !showPassword"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="description" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.description') }}</q-item-section>
        <q-item-section>{{ item.description }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="toggle_on" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.status') }}</q-item-section>
        <q-item-section>
          <q-badge :color="item.status === 'ACTIVE' ? 'green' : 'grey'" :label="item.status" />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="schedule" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.createdDate') }}</q-item-section>
        <q-item-section>{{ item.createdDate }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="update" /></q-item-section>
        <q-item-section caption>{{ $t('projectSetting.lastModifiedDate') }}</q-item-section>
        <q-item-section>{{ item.lastModifiedDate }}</q-item-section>
      </q-item>
    </q-list>
  </q-card-section>
  <q-separator />
  <q-card-actions align="right">
    <q-btn flat icon="edit_note" color="primary" :label="$t('entity.action.edit')" @click="emit('edit')" />
    <q-btn flat icon="delete_forever" color="negative" :label="$t('entity.action.delete')" @click="emit('delete')" />
    <q-btn flat icon="close" :label="$t('entity.action.close')" @click="emit('close')" />
  </q-card-actions>
</template>
