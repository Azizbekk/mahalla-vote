<template>
  <div class="text-caption text-grey-7">
    {{ t$('global.item-count', { first, second, total }) }}
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t: t$ } = useI18n();

const props = defineProps<{
  page: number;
  total: number;
  itemsPerPage: number;
}>();

const first = computed(() => ((props.page - 1) * props.itemsPerPage === 0 ? 1 : (props.page - 1) * props.itemsPerPage + 1));
const second = computed(() => (props.page * props.itemsPerPage < props.total ? props.page * props.itemsPerPage : props.total));
</script>
