<template>
  <div class="q-pa-md page-container">
    <q-card flat bordered v-if="userAccount">
      <q-card-section>
        <div class="text-h5">{{ t$('userManagement.home.createOrEditLabel') }}</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="save" class="q-gutter-md" style="max-width: 600px">
          <q-input v-if="userAccount.id" v-model="userAccount.id" :label="t$('global.field.id')" readonly outlined dense />

          <q-input
            v-model="v$.userAccount.login.$model"
            :label="t$('userManagement.login')"
            :error="v$.userAccount.login.$dirty && v$.userAccount.login.$invalid"
            :error-message="loginErrorMessage"
            outlined
            dense
          />

          <q-input
            v-model="v$.userAccount.firstName.$model"
            :label="t$('userManagement.firstName')"
            :placeholder="t$('settings.form.firstnamePlaceholder')"
            :error="v$.userAccount.firstName.$dirty && v$.userAccount.firstName.$invalid"
            :error-message="t$('entity.validation.maxlength', { max: 50 })"
            outlined
            dense
          />

          <q-input
            v-model="v$.userAccount.lastName.$model"
            :label="t$('userManagement.lastName')"
            :placeholder="t$('settings.form.lastnamePlaceholder')"
            :error="v$.userAccount.lastName.$dirty && v$.userAccount.lastName.$invalid"
            :error-message="t$('entity.validation.maxlength', { max: 50 })"
            outlined
            dense
          />

          <q-input
            v-model="v$.userAccount.email.$model"
            type="email"
            :label="t$('userManagement.email')"
            :placeholder="t$('global.form.emailPlaceholder')"
            :error="v$.userAccount.email.$dirty && v$.userAccount.email.$invalid"
            :error-message="emailErrorMessage"
            outlined
            dense
          />

          <q-toggle
            v-model="userAccount.activated"
            :label="t$('userManagement.activated')"
            :disable="userAccount.id === null"
            color="positive"
          />

          <q-select
            v-if="languageOptions.length > 0"
            v-model="userAccount.langKey"
            :options="languageOptions"
            :label="t$('userManagement.langKey')"
            emit-value
            map-options
            outlined
            dense
          />

          <q-select
            v-model="userAccount.authorities"
            :options="authorities"
            :label="t$('userManagement.profiles')"
            multiple
            use-chips
            outlined
            dense
          />

          <div class="q-gutter-sm">
            <q-btn :label="t$('entity.action.cancel')" icon="close" color="grey" outline @click="previousState" />
            <q-btn
              type="submit"
              :label="t$('entity.action.save')"
              icon="save"
              color="primary"
              :disable="v$.userAccount.$invalid || isSaving"
              :loading="isSaving"
            />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useVuelidate } from '@vuelidate/core';
import { email, maxLength, minLength, required } from '@vuelidate/validators';
import { useRoute, useRouter } from 'vue-router';
import UserManagementService from './user-management.service';
import { type IUser, User } from '@/shared/model/user.model';
import { useAlertService } from '@/shared/alert/alert.service';
import languages from '@/shared/config/languages';

const { t: t$ } = useI18n();
const route = useRoute();
const router = useRouter();
const alertService = inject('alertService', () => useAlertService(), true);
const userManagementService = inject('userManagementService', () => new UserManagementService(), true);

const loginValidator = (value: string) => {
  if (!value) return true;
  return /^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/.test(value);
};

const userAccount = ref<IUser>({ ...new User(), authorities: [] });
const isSaving = ref(false);
const authorities = ref<string[]>([]);

const langs = languages();
const languageOptions = Object.entries(langs).map(([key, val]) => ({
  label: (val as any).name,
  value: key,
}));

const rules = {
  userAccount: {
    login: { required, maxLength: maxLength(254), pattern: loginValidator },
    firstName: { maxLength: maxLength(50) },
    lastName: { maxLength: maxLength(50) },
    email: { required, email, minLength: minLength(5), maxLength: maxLength(50) },
  },
};

const v$ = useVuelidate(rules, { userAccount });

const loginErrorMessage = computed(() => {
  if (!v$.value.userAccount.login.$dirty) return '';
  if (!v$.value.userAccount.login.required) return t$('entity.validation.required');
  if (!v$.value.userAccount.login.maxLength) return t$('entity.validation.maxlength', { max: 254 });
  if (!v$.value.userAccount.login.pattern) return t$('entity.validation.patternLogin');
  return '';
});

const emailErrorMessage = computed(() => {
  if (!v$.value.userAccount.email.$dirty) return '';
  if (!v$.value.userAccount.email.required) return t$('validate.email.required');
  if (!v$.value.userAccount.email.email) return t$('validate.email.invalid');
  if (!v$.value.userAccount.email.minLength) return t$('validate.email.minlength');
  if (!v$.value.userAccount.email.maxLength) return t$('validate.email.maxlength');
  return '';
});

function previousState(): void {
  router.go(-1);
}

function getToastMessageFromHeader(res: any): string {
  return t$(res.headers['x-mahallavoteapp-alert'], {
    param: decodeURIComponent(res.headers['x-mahallavoteapp-params'].replace(/\+/g, ' ')),
  }).toString();
}

function save(): void {
  isSaving.value = true;
  if (userAccount.value.id) {
    userManagementService
      .update(userAccount.value)
      .then(res => {
        isSaving.value = false;
        alertService.showInfo(getToastMessageFromHeader(res));
        previousState();
      })
      .catch(error => {
        isSaving.value = false;
        alertService.showHttpError(error.response);
      });
  } else {
    userManagementService
      .create(userAccount.value)
      .then(res => {
        isSaving.value = false;
        alertService.showSuccess(getToastMessageFromHeader(res));
        previousState();
      })
      .catch(error => {
        isSaving.value = false;
        alertService.showHttpError(error.response);
      });
  }
}

async function initAuthorities() {
  const response = await userManagementService.retrieveAuthorities();
  authorities.value = response.data;
}

async function loadUser(userId: string) {
  const response = await userManagementService.get(userId);
  userAccount.value = response.data;
}

initAuthorities();
const userId = route.params?.userId as string;
if (userId) {
  loadUser(userId);
}
</script>
