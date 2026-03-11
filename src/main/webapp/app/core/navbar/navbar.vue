<template>
  <q-toolbar class="q-px-md">
    <q-btn flat dense no-caps to="/" class="q-mr-sm" data-cy="navbar">
      <q-icon name="how_to_vote" size="28px" class="q-mr-xs text-white" />
      <span class="text-subtitle1 text-weight-bold text-white">Mahalla Vote</span>
      <span class="text-caption text-grey-4 q-ml-xs">{{ version }}</span>
    </q-btn>

    <q-space />

    <!-- Desktop navigation -->
    <div class="gt-sm row items-center q-gutter-x-sm">
      <q-btn flat no-caps to="/" icon="home" :label="t$('global.menu.home')" class="text-white" exact />

      <q-btn-dropdown
        v-if="authenticated"
        flat
        no-caps
        icon="list"
        :label="t$('global.menu.entities.main')"
        class="text-white"
        data-cy="entity"
      >
        <q-list>
          <entities-menu />
        </q-list>
      </q-btn-dropdown>

      <q-btn-dropdown
        v-if="hasAnyAuthority('ROLE_ADMIN') && authenticated"
        flat
        no-caps
        icon="admin_panel_settings"
        :label="t$('global.menu.admin.main')"
        class="text-white"
        data-cy="adminMenu"
      >
        <q-list dense style="min-width: 200px">
          <q-item clickable v-close-popup to="/admin/user-management">
            <q-item-section avatar><q-icon name="people" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.userManagement') }}</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to="/admin/metrics">
            <q-item-section avatar><q-icon name="speed" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.metrics') }}</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to="/admin/health">
            <q-item-section avatar><q-icon name="favorite" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.health') }}</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to="/admin/configuration">
            <q-item-section avatar><q-icon name="settings" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.configuration') }}</q-item-section>
          </q-item>
          <q-item clickable v-close-popup to="/admin/logs">
            <q-item-section avatar><q-icon name="assignment" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.logs') }}</q-item-section>
          </q-item>
          <q-item v-if="openAPIEnabled" clickable v-close-popup to="/admin/docs">
            <q-item-section avatar><q-icon name="menu_book" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.apidocs') }}</q-item-section>
          </q-item>
        </q-list>
      </q-btn-dropdown>

      <q-btn-dropdown
        v-if="languages && Object.keys(languages).length > 1"
        flat
        no-caps
        icon="translate"
        :label="t$('global.menu.language')"
        class="text-white"
      >
        <q-list dense style="min-width: 180px">
          <q-item
            v-for="(value, key) in languages"
            :key="`lang-${key}`"
            clickable
            v-close-popup
            @click="changeLanguage(key)"
            :active="isActiveLanguage(key)"
          >
            <q-item-section>{{ value.name }}</q-item-section>
            <q-item-section side v-if="isActiveLanguage(key)">
              <q-icon name="check" color="primary" />
            </q-item-section>
          </q-item>
        </q-list>
      </q-btn-dropdown>

      <q-btn-dropdown flat no-caps icon="account_circle" :label="t$('global.menu.account.main')" class="text-white" data-cy="accountMenu">
        <q-list dense style="min-width: 200px">
          <q-item v-if="authenticated" clickable v-close-popup to="/account/settings" data-cy="settings">
            <q-item-section avatar><q-icon name="tune" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.settings') }}</q-item-section>
          </q-item>
          <q-item v-if="authenticated" clickable v-close-popup to="/account/password" data-cy="passwordItem">
            <q-item-section avatar><q-icon name="lock" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.password') }}</q-item-section>
          </q-item>
          <q-separator v-if="authenticated" />
          <q-item v-if="authenticated" clickable v-close-popup @click="logout()" data-cy="logout">
            <q-item-section avatar><q-icon name="logout" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.logout') }}</q-item-section>
          </q-item>
          <q-item v-if="!authenticated" clickable v-close-popup @click="showLogin()" data-cy="login">
            <q-item-section avatar><q-icon name="login" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.login') }}</q-item-section>
          </q-item>
          <q-item v-if="!authenticated" clickable v-close-popup to="/register" data-cy="register">
            <q-item-section avatar><q-icon name="person_add" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.register') }}</q-item-section>
          </q-item>
        </q-list>
      </q-btn-dropdown>
    </div>

    <!-- Mobile hamburger -->
    <q-btn flat dense round icon="menu" class="lt-md text-white" @click="drawerOpen = !drawerOpen" aria-label="Toggle navigation" />
  </q-toolbar>

  <!-- Mobile drawer -->
  <q-drawer v-model="drawerOpen" side="right" overlay bordered class="lt-md" :width="280">
    <q-list padding>
      <q-item clickable v-close-popup to="/" exact @click="drawerOpen = false">
        <q-item-section avatar><q-icon name="home" /></q-item-section>
        <q-item-section>{{ t$('global.menu.home') }}</q-item-section>
      </q-item>

      <q-expansion-item v-if="authenticated" icon="list" :label="t$('global.menu.entities.main')">
        <q-list class="q-pl-md">
          <entities-menu />
        </q-list>
      </q-expansion-item>

      <q-expansion-item
        v-if="hasAnyAuthority('ROLE_ADMIN') && authenticated"
        icon="admin_panel_settings"
        :label="t$('global.menu.admin.main')"
      >
        <q-list class="q-pl-md" dense>
          <q-item clickable to="/admin/user-management" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="people" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.userManagement') }}</q-item-section>
          </q-item>
          <q-item clickable to="/admin/metrics" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="speed" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.metrics') }}</q-item-section>
          </q-item>
          <q-item clickable to="/admin/health" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="favorite" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.health') }}</q-item-section>
          </q-item>
          <q-item clickable to="/admin/configuration" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="settings" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.configuration') }}</q-item-section>
          </q-item>
          <q-item clickable to="/admin/logs" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="assignment" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.logs') }}</q-item-section>
          </q-item>
          <q-item v-if="openAPIEnabled" clickable to="/admin/docs" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="menu_book" /></q-item-section>
            <q-item-section>{{ t$('global.menu.admin.apidocs') }}</q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>

      <q-expansion-item v-if="languages && Object.keys(languages).length > 1" icon="translate" :label="t$('global.menu.language')">
        <q-list class="q-pl-md" dense>
          <q-item
            v-for="(value, key) in languages"
            :key="`lang-${key}`"
            clickable
            @click="
              changeLanguage(key);
              drawerOpen = false;
            "
            :active="isActiveLanguage(key)"
          >
            <q-item-section>{{ value.name }}</q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>

      <q-separator class="q-my-sm" />

      <q-expansion-item icon="account_circle" :label="t$('global.menu.account.main')">
        <q-list class="q-pl-md" dense>
          <q-item v-if="authenticated" clickable to="/account/settings" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="tune" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.settings') }}</q-item-section>
          </q-item>
          <q-item v-if="authenticated" clickable to="/account/password" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="lock" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.password') }}</q-item-section>
          </q-item>
          <q-item
            v-if="authenticated"
            clickable
            @click="
              logout();
              drawerOpen = false;
            "
          >
            <q-item-section avatar><q-icon name="logout" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.logout') }}</q-item-section>
          </q-item>
          <q-item
            v-if="!authenticated"
            clickable
            @click="
              showLogin();
              drawerOpen = false;
            "
          >
            <q-item-section avatar><q-icon name="login" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.login') }}</q-item-section>
          </q-item>
          <q-item v-if="!authenticated" clickable to="/register" @click="drawerOpen = false">
            <q-item-section avatar><q-icon name="person_add" /></q-item-section>
            <q-item-section>{{ t$('global.menu.account.register') }}</q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>
    </q-list>
  </q-drawer>
</template>

<script lang="ts" setup>
import { type Ref, computed, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

import { useLoginModal } from '@/account/login-modal';
import type AccountService from '@/account/account.service';
import languages from '@/shared/config/languages';
import EntitiesMenu from '@/entities/entities-menu.vue';
import { useStore } from '@/store';

const { t: t$ } = useI18n();
const { showLogin } = useLoginModal();
const accountService = inject<AccountService>('accountService');
const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);
const changeLanguage = inject<(lang: string) => Promise<void>>('changeLanguage');
const router = useRouter();
const store = useStore();

const drawerOpen = ref(false);
const version = `v${APP_VERSION}`;
const hasAnyAuthorityValues: Ref<Record<string, boolean>> = ref({});

const openAPIEnabled = computed(() => store.activeProfiles.indexOf('api-docs') > -1);
const authenticated = computed(() => store.authenticated);

const isActiveLanguage = (key: string) => {
  return key === currentLanguage.value;
};

const subIsActive = (input: string | string[]) => {
  const paths = Array.isArray(input) ? input : [input];
  return paths.some(path => router.currentRoute.value.path.indexOf(path) === 0);
};

const hasAnyAuthority = (authorities: string): boolean => {
  accountService.hasAnyAuthorityAndCheckAuth(authorities).then((value: boolean) => {
    if (hasAnyAuthorityValues.value[authorities] !== value) {
      hasAnyAuthorityValues.value = { ...hasAnyAuthorityValues.value, [authorities]: value };
    }
  });
  return hasAnyAuthorityValues.value[authorities] ?? false;
};

const logout = async () => {
  localStorage.removeItem('authenticationToken');
  sessionStorage.removeItem('authenticationToken');
  store.logout();
  if (router.currentRoute.value.path !== '/') {
    router.push('/');
  }
};
</script>
