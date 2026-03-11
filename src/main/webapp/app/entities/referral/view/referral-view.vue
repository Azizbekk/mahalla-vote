<script setup lang="ts">
import type { ReferralDetail } from '../models/referral-detail.model';

defineProps<{ item: ReferralDetail | null }>();
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
        <q-item-section side><q-icon name="person" /></q-item-section>
        <q-item-section caption>{{ $t('referral.referrerId') }}</q-item-section>
        <q-item-section>{{ item.referrerId }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="person_add" /></q-item-section>
        <q-item-section caption>{{ $t('referral.referredId') }}</q-item-section>
        <q-item-section>{{ item.referredId }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="toggle_on" /></q-item-section>
        <q-item-section caption>{{ $t('referral.status') }}</q-item-section>
        <q-item-section>
          <q-badge
            :color="item.status === 'REGISTERED' ? 'grey' : item.status === 'VOTED' ? 'green' : item.status === 'PAID' ? 'blue' : 'grey'"
            :label="item.status"
          />
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="schedule" /></q-item-section>
        <q-item-section caption>{{ $t('referral.createdDate') }}</q-item-section>
        <q-item-section>{{ item.createdDate }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="update" /></q-item-section>
        <q-item-section caption>{{ $t('referral.lastModifiedDate') }}</q-item-section>
        <q-item-section>{{ item.lastModifiedDate }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="how_to_vote" /></q-item-section>
        <q-item-section caption>{{ $t('referral.votedAt') }}</q-item-section>
        <q-item-section>{{ item.votedAt }}</q-item-section>
      </q-item>
      <q-item>
        <q-item-section side><q-icon name="payments" /></q-item-section>
        <q-item-section caption>{{ $t('referral.paidAt') }}</q-item-section>
        <q-item-section>{{ item.paidAt }}</q-item-section>
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
