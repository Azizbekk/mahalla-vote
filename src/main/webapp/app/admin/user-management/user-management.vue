<template>
  <div class="q-pa-md page-container">
    <div class="row items-center q-mb-md">
      <div class="text-h5 col" data-cy="userManagementPageHeading">
        {{ t$('userManagement.home.title') }}
      </div>
      <div class="col-auto q-gutter-sm">
        <q-btn
          color="info"
          icon="refresh"
          :label="t$('userManagement.home.refreshListLabel')"
          :loading="isLoading"
          @click="loadAll"
          outline
        />
        <q-btn color="primary" icon="add" :label="t$('userManagement.home.createLabel')" :to="{ name: 'UserCreate' }" />
      </div>
    </div>

    <q-table
      flat
      bordered
      :rows="users"
      :columns="columns"
      row-key="id"
      :loading="isLoading"
      :pagination="{ rowsPerPage: 0 }"
      hide-pagination
      binary-state-sort
    >
      <template #header="props">
        <q-tr :props="props">
          <q-th v-for="col in props.cols" :key="col.name" :props="props" class="cursor-pointer" @click="changeOrder(col.field as string)">
            {{ col.label }}
            <q-icon v-if="propOrder === col.field" :name="reverse ? 'arrow_downward' : 'arrow_upward'" size="xs" class="q-ml-xs" />
          </q-th>
          <q-th auto-width>{{ t$('entity.actions') }}</q-th>
        </q-tr>
      </template>

      <template #body="props">
        <q-tr :props="props">
          <q-td key="id" :props="props">
            <router-link :to="{ name: 'UserView', params: { userId: props.row.login } }">
              {{ props.row.id }}
            </router-link>
          </q-td>
          <q-td key="login" :props="props">{{ props.row.login }}</q-td>
          <q-td key="email" :props="props">{{ props.row.email }}</q-td>
          <q-td key="activated" :props="props">
            <q-badge
              v-if="props.row.activated"
              color="positive"
              :label="t$('userManagement.activated')"
              class="cursor-pointer"
              @click="username !== props.row.login && setActive(props.row, false)"
            />
            <q-badge
              v-else
              color="negative"
              :label="t$('userManagement.deactivated')"
              class="cursor-pointer"
              @click="setActive(props.row, true)"
            />
          </q-td>
          <q-td key="profiles" :props="props">
            <q-badge v-for="authority of props.row.authorities" :key="authority" color="info" :label="authority" class="q-mr-xs q-mb-xs" />
          </q-td>
          <q-td key="createdDate" :props="props">{{ formatDate(props.row.createdDate) }}</q-td>
          <q-td auto-width>
            <q-btn dense flat round icon="visibility" color="info" :to="{ name: 'UserView', params: { userId: props.row.login } }">
              <q-tooltip>{{ t$('entity.action.view') }}</q-tooltip>
            </q-btn>
            <q-btn dense flat round icon="edit" color="primary" :to="{ name: 'UserEdit', params: { userId: props.row.login } }">
              <q-tooltip>{{ t$('entity.action.edit') }}</q-tooltip>
            </q-btn>
            <q-btn
              dense
              flat
              round
              icon="delete"
              color="negative"
              :disable="username === props.row.login"
              @click="prepareRemove(props.row)"
            >
              <q-tooltip>{{ t$('entity.action.delete') }}</q-tooltip>
            </q-btn>
          </q-td>
        </q-tr>
      </template>
    </q-table>

    <div v-if="users && users.length > 0" class="row justify-center items-center q-mt-md q-gutter-md">
      <div class="text-body2 text-grey-7">
        {{
          t$('global.item-count', {
            first: (page - 1) * itemsPerPage + 1,
            second: Math.min(page * itemsPerPage, totalItems),
            total: totalItems,
          })
        }}
      </div>
      <q-pagination
        v-model="page"
        :max="Math.ceil(totalItems / itemsPerPage)"
        :max-pages="7"
        direction-links
        boundary-links
        icon-first="first_page"
        icon-last="last_page"
        icon-prev="chevron_left"
        icon-next="chevron_right"
        color="primary"
        @update:model-value="loadPage"
      />
    </div>

    <q-dialog v-model="showDeleteDialog" persistent>
      <q-card style="min-width: 400px">
        <q-card-section class="row items-center">
          <q-icon name="warning" color="negative" size="md" class="q-mr-sm" />
          <span class="text-h6">{{ t$('entity.delete.title') }}</span>
        </q-card-section>
        <q-card-section>
          {{ t$('userManagement.delete.question', { login: removeId }) }}
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat :label="t$('entity.action.cancel')" color="grey" v-close-popup />
          <q-btn flat :label="t$('entity.action.delete')" color="negative" @click="deleteUser" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup lang="ts">
import { type ComputedRef, ref, inject, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import UserManagementService from './user-management.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';

const { t: t$ } = useI18n();
const alertService = inject('alertService', () => useAlertService(), true);
const { formatDateShort: formatDate } = useDateFormat();
const userManagementService = inject('userManagementService', () => new UserManagementService(), true);
const username = inject<ComputedRef<string>>('currentUsername');

const isLoading = ref(false);
const users = ref<any[]>([]);
const totalItems = ref(0);
const itemsPerPage = ref(20);
const page = ref(1);
const propOrder = ref('id');
const reverse = ref(false);
const removeId = ref<any>(null);
const showDeleteDialog = ref(false);

const columns = [
  { name: 'id', label: t$('global.field.id'), field: 'id', align: 'left' as const, sortable: false },
  { name: 'login', label: t$('userManagement.login'), field: 'login', align: 'left' as const, sortable: false },
  { name: 'email', label: t$('userManagement.email'), field: 'email', align: 'left' as const, sortable: false },
  { name: 'activated', label: t$('userManagement.activated'), field: 'activated', align: 'center' as const, sortable: false },
  { name: 'profiles', label: t$('userManagement.profiles'), field: 'authorities', align: 'left' as const, sortable: false },
  { name: 'createdDate', label: t$('userManagement.createdDate'), field: 'createdDate', align: 'left' as const, sortable: false },
];

function sort(): string[] {
  const result = [`${propOrder.value},${reverse.value ? 'desc' : 'asc'}`];
  if (propOrder.value !== 'id') {
    result.push('id');
  }
  return result;
}

function loadAll(): void {
  isLoading.value = true;
  userManagementService
    .retrieve({
      sort: sort(),
      page: page.value - 1,
      size: itemsPerPage.value,
    })
    .then(res => {
      users.value = res.data;
      totalItems.value = Number(res.headers['x-total-count']);
      isLoading.value = false;
    })
    .catch(() => {
      isLoading.value = false;
    });
}

function loadPage(newPage: number): void {
  page.value = newPage;
  loadAll();
}

function changeOrder(field: string): void {
  if (propOrder.value === field) {
    reverse.value = !reverse.value;
  } else {
    propOrder.value = field;
    reverse.value = false;
  }
  loadAll();
}

function setActive(user: any, isActivated: boolean): void {
  user.activated = isActivated;
  userManagementService
    .update(user)
    .then(() => {
      loadAll();
    })
    .catch(() => {
      user.activated = !isActivated;
    });
}

function prepareRemove(instance: any): void {
  removeId.value = instance.login;
  showDeleteDialog.value = true;
}

function deleteUser(): void {
  userManagementService
    .remove(removeId.value)
    .then(res => {
      alertService.showInfo(
        t$(res.headers['x-mahallavoteapp-alert'].toString(), {
          param: decodeURIComponent(res.headers['x-mahallavoteapp-params'].replace(/\+/g, ' ')),
        }),
        { variant: 'danger' },
      );
      removeId.value = null;
      showDeleteDialog.value = false;
      loadAll();
    })
    .catch(error => {
      alertService.showHttpError(error.response);
    });
}

onMounted(() => {
  loadAll();
});
</script>
