<script setup lang="ts">
import type { VoteLotDetail } from '../models/vote-lot-detail.model';

defineProps<{ item: VoteLotDetail | null }>();
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'edit'): void;
  (e: 'delete'): void;
}>();
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
        <q-item-section side><q-icon name="campaign" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.name') }}</q-item-section>
        <q-item-section>{{ item.name }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="link" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.openBudgetUrl') }}</q-item-section>
        <q-item-section>
          <a :href="item.openBudgetUrl" target="_blank" class="text-primary">{{ item.openBudgetUrl }}</a>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="flag" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.targetVoteCount') }}</q-item-section>
        <q-item-section>{{ item.targetVoteCount }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="how_to_vote" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.currentVoteCount') }}</q-item-section>
        <q-item-section>
          <div class="row items-center q-gutter-sm">
            <span>{{ item.currentVoteCount }} / {{ item.targetVoteCount }}</span>
            <q-linear-progress
              :value="(item.currentVoteCount ?? 0) / (item.targetVoteCount || 1)"
              color="primary"
              style="width: 150px"
              rounded
              size="16px"
            />
          </div>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="toggle_on" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.status') }}</q-item-section>
        <q-item-section>
          <q-badge :color="item.status === 'ACTIVE' ? 'green' : item.status === 'COMPLETED' ? 'blue' : 'grey'" :label="item.status" />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="schedule" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.createdDate') }}</q-item-section>
        <q-item-section>{{ item.createdDate }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="update" /></q-item-section>
        <q-item-section caption>{{ $t('voteLot.lastModifiedDate') }}</q-item-section>
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
