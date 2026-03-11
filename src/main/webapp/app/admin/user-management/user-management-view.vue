<template>
  <div class="q-pa-md page-container">
    <q-card v-if="user" flat bordered>
      <q-card-section>
        <div class="text-h5">
          {{ t$('userManagement.detail.title') }} [<strong>{{ user.login }}</strong
          >]
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-list separator>
          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.login') }}
            </q-item-section>
            <q-item-section>
              {{ user.login }}
              <q-badge
                v-if="user.activated"
                color="positive"
                :label="t$('userManagement.activated')"
                class="q-ml-sm"
                style="display: inline"
              />
              <q-badge v-else color="negative" :label="t$('userManagement.deactivated')" class="q-ml-sm" style="display: inline" />
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.firstName') }}
            </q-item-section>
            <q-item-section>{{ user.firstName }}</q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.lastName') }}
            </q-item-section>
            <q-item-section>{{ user.lastName }}</q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.email') }}
            </q-item-section>
            <q-item-section>{{ user.email }}</q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.langKey') }}
            </q-item-section>
            <q-item-section>{{ user.langKey }}</q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.createdBy') }}
            </q-item-section>
            <q-item-section>{{ user.createdBy }}</q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.createdDate') }}
            </q-item-section>
            <q-item-section>
              <span v-if="user.createdDate">{{ formatDate(user.createdDate) }}</span>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.lastModifiedBy') }}
            </q-item-section>
            <q-item-section>{{ user.lastModifiedBy }}</q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.lastModifiedDate') }}
            </q-item-section>
            <q-item-section>
              <span v-if="user.lastModifiedDate">{{ formatDate(user.lastModifiedDate) }}</span>
            </q-item-section>
          </q-item>

          <q-item>
            <q-item-section side class="text-weight-bold" style="min-width: 180px">
              {{ t$('userManagement.profiles') }}
            </q-item-section>
            <q-item-section>
              <div>
                <q-badge v-for="authority of user.authorities" :key="authority" color="info" :label="authority" class="q-mr-xs q-mb-xs" />
              </div>
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-card-actions>
        <q-btn color="info" icon="arrow_back" :label="t$('entity.action.back')" :to="{ name: 'UserManagement' }" />
      </q-card-actions>
    </q-card>
  </div>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import UserManagementService from './user-management.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

const { t: t$ } = useI18n();
const route = useRoute();
const { formatDateLong: formatDate } = useDateFormat();
const alertService = inject('alertService', () => useAlertService(), true);
const userManagementService = inject('userManagementService', () => new UserManagementService(), true);

const user = ref<any>(null);

async function loadUser(userId: string) {
  try {
    const response = await userManagementService.get(userId);
    user.value = response.data;
  } catch (error: any) {
    alertService.showHttpError(error.response);
  }
}

loadUser(route.params?.userId as string);
</script>
