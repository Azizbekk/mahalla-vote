<script setup lang="ts">
import VoteLotFormComponent from '../form/vote-lot-form.vue';
import VoteLotDelete from '../delete/vote-lot-delete.vue';
import VoteLotView from '../view/vote-lot-view.vue';
import { useVoteLotPaging } from './vote-lot-paging';

const { state, meta, actions } = useVoteLotPaging();
</script>

<template>
  <div class="q-pa-md">
    <q-table
      row-key="id"
      :rows="state.rows"
      :columns="meta.columns"
      :loading="state.loading"
      v-model:pagination="state.pagination"
      @request="actions.handleRequest"
      separator="cell"
      flat
      bordered
    >
      <!-- TOOLBAR -->
      <template #top>
        <div class="row items-center full-width">
          <div class="text-h6">{{ $t('voteLot.title') }}</div>
          <q-space />

          <q-input
            dense
            outlined
            debounce="300"
            v-model="state.globalSearch"
            :placeholder="$t('entity.action.search')"
            clearable
            style="width: 220px"
            class="q-mr-sm"
            @update:model-value="actions.refresh()"
          >
            <template #prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <q-btn flat round icon="refresh" :loading="state.loading" @click="actions.refresh()">
            <q-tooltip>{{ $t('entity.action.refresh') }}</q-tooltip>
          </q-btn>

          <q-btn color="primary" icon="add_circle" :label="$t('entity.action.create')" unelevated @click="actions.openCreateDialog()" />
        </div>
      </template>

      <!-- PROGRESS -->
      <template #body-cell-progress="props">
        <q-td :props="props">
          <div class="row items-center no-wrap" style="min-width: 120px">
            <q-linear-progress
              :value="(props.row.currentVoteCount ?? 0) / (props.row.targetVoteCount || 1)"
              color="primary"
              class="q-mr-sm col"
              rounded
              size="20px"
            >
              <div class="absolute-full flex flex-center">
                <q-badge color="white" text-color="primary" :label="props.value" />
              </div>
            </q-linear-progress>
          </div>
        </q-td>
      </template>

      <!-- STATUS BADGE -->
      <template #body-cell-status="props">
        <q-td :props="props">
          <q-badge :color="props.value === 'ACTIVE' ? 'green' : props.value === 'COMPLETED' ? 'blue' : 'grey'" :label="props.value" />
        </q-td>
      </template>

      <!-- ACTIONS -->
      <template #body-cell-actions="props">
        <q-td :props="props">
          <q-btn flat dense round size="sm" icon="visibility" color="info" @click="actions.openViewDialog(props.row)">
            <q-tooltip>{{ $t('entity.action.view') }}</q-tooltip>
          </q-btn>
          <q-btn flat dense round size="sm" icon="edit_note" color="primary" @click="actions.openEditDialog(props.row)">
            <q-tooltip>{{ $t('entity.action.edit') }}</q-tooltip>
          </q-btn>
          <q-btn flat dense round size="sm" icon="delete_forever" color="negative" @click="actions.openDeleteDialog(props.row)">
            <q-tooltip>{{ $t('entity.action.delete') }}</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>

    <!-- FORM DIALOG -->
    <q-dialog v-model="state.formDialog.show" persistent>
      <q-card style="min-width: 600px">
        <q-toolbar class="bg-primary text-white">
          <q-toolbar-title>
            <q-icon :name="state.formDialog.mode === 'edit' ? 'edit_note' : 'add_circle'" class="q-mr-sm" />
            {{ state.formDialog.mode === 'edit' ? $t('voteLot.editTitle') : $t('voteLot.createTitle') }}
          </q-toolbar-title>
          <q-btn flat round dense icon="close" @click="actions.closeFormDialog()" />
        </q-toolbar>
        <VoteLotFormComponent
          :item="state.formDialog.item"
          :mode="state.formDialog.mode"
          @created="actions.handleCreated"
          @updated="actions.handleUpdated"
          @cancel="actions.closeFormDialog()"
        />
      </q-card>
    </q-dialog>

    <!-- DELETE DIALOG -->
    <q-dialog v-model="state.deleteDialog.show" persistent>
      <q-card style="min-width: 450px">
        <q-toolbar class="bg-negative text-white">
          <q-toolbar-title>
            <q-icon name="report_problem" class="q-mr-sm" />
            {{ $t('entity.deleteConfirmTitle') }}
          </q-toolbar-title>
          <q-btn flat round dense icon="close" @click="actions.closeDeleteDialog()" />
        </q-toolbar>
        <VoteLotDelete :item="state.deleteDialog.item" @deleted="actions.handleDeleted" @cancel="actions.closeDeleteDialog()" />
      </q-card>
    </q-dialog>

    <!-- VIEW DIALOG -->
    <q-dialog v-model="state.viewDialog.show" persistent>
      <q-card style="min-width: 650px">
        <q-toolbar class="bg-primary text-white">
          <q-toolbar-title>
            <q-icon name="visibility" class="q-mr-sm" />
            {{ $t('voteLot.viewTitle') }}
          </q-toolbar-title>
          <q-btn flat round dense icon="close" v-close-popup />
        </q-toolbar>
        <VoteLotView
          :item="state.viewDialog.item"
          @close="actions.closeViewDialog"
          @edit="actions.handleViewEdit"
          @delete="actions.handleViewDelete"
        />
      </q-card>
    </q-dialog>
  </div>
</template>
