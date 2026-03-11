import { computed, createApp, h, onMounted, provide, watch } from 'vue';
import { createPinia, storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';
import { Quasar, Notify, Dialog } from 'quasar';

import '@quasar/extras/material-icons/material-icons.css';
import 'quasar/src/css/index.sass';

import App from './app.vue';
import router from './router';
import { useStore, useTranslationStore } from '@/store';
import { setupAxiosInterceptors } from '@/shared/config/axios-interceptor';
import { initI18N } from '@/shared/config/config';
import { useLoginModal } from '@/account/login-modal';
import AccountService from '@/account/account.service';

import '../content/scss/global.scss';
import TranslationService from '@/locale/translation.service';

const pinia = createPinia();
const i18n = initI18N();

const app = createApp({
  name: 'MahallaVoteApp',
  components: { App },
  setup() {
    const { hideLogin, showLogin } = useLoginModal();
    const store = useStore();
    const accountService = new AccountService(store);
    const i18n = useI18n();
    const translationStore = useTranslationStore();
    const translationService = new TranslationService(i18n);

    const changeLanguage = async (newLanguage: string) => {
      if (i18n.locale.value !== newLanguage) {
        await translationService.refreshTranslation(newLanguage);
        translationStore.setCurrentLanguage(newLanguage);
      }
    };

    provide('currentLanguage', i18n.locale);
    provide('changeLanguage', changeLanguage);

    watch(
      () => store.account,
      async value => {
        if (!translationService.getLocalStoreLanguage()) {
          await changeLanguage(value.langKey);
        }
      },
    );

    watch(
      () => translationStore.currentLanguage,
      value => {
        translationService.setLocale(value);
      },
    );

    onMounted(async () => {
      const lang = [translationService.getLocalStoreLanguage(), store.account?.langKey, navigator.language, 'uz-Latn-uz'].find(
        lang => lang && translationService.isLanguageSupported(lang),
      );
      await changeLanguage(lang);
    });

    router.beforeResolve(async (to, from, next) => {
      hideLogin();

      if (!store.authenticated) {
        await accountService.update();
      }
      if (to.meta?.authorities && to.meta.authorities.length > 0) {
        const value = await accountService.hasAnyAuthorityAndCheckAuth(to.meta.authorities);
        if (!value) {
          if (from.path !== '/forbidden') {
            next({ path: '/forbidden' });
            return;
          }
        }
      }
      next();
    });

    setupAxiosInterceptors(
      error => {
        const url = error.response?.config?.url;
        const status = error.status || error.response.status;
        if (status === 401) {
          store.logout();
          if (!url.endsWith('api/account') && !url.endsWith('api/authenticate')) {
            showLogin();
            return;
          }
        }
        return Promise.reject(error);
      },
      error => {
        return Promise.reject(error);
      },
    );

    const { authenticated } = storeToRefs(store);
    provide('authenticated', authenticated);
    provide(
      'currentUsername',
      computed(() => store.account?.login),
    );

    provide('translationService', translationService);
    provide('accountService', accountService);
  },
  render: () => h(App),
});

app.use(Quasar, {
  plugins: { Notify, Dialog },
  config: {
    notify: { position: 'top-right', timeout: 3000 },
  },
});

app.use(router).use(pinia).use(i18n).mount('#app');
