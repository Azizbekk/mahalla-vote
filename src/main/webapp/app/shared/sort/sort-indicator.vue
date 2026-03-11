<template>
  <q-icon
    :name="currentOrder === fieldName ? (reverse ? 'arrow_downward' : 'arrow_upward') : 'unfold_more'"
    size="xs"
    :color="currentOrder === fieldName ? 'primary' : 'grey-5'"
  />
</template>

<script lang="ts" setup>
defineProps<{
  currentOrder?: string;
  fieldName?: string;
  reverse?: boolean;
}>();
</script>
