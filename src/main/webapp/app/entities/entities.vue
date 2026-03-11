<template>
  <router-view />
</template>

<script lang="ts" setup>
import { provide } from 'vue';
import UserService from '@/entities/user/user.service';

provide('userService', () => new UserService());
</script>
