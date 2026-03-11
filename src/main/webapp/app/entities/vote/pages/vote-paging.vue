<script setup lang="ts">
import VoteFormComponent from '../form/vote-form.vue';
import VoteDelete from '../delete/vote-delete.vue';
import VoteView from '../view/vote-view.vue';
import { useVotePaging } from './vote-paging';

const { state, meta, actions } = useVotePaging();
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
          <div class="text-h6">{{ $t('vote.title') }}</div>
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

      <!-- STATUS BADGE -->
      <template #body-cell-status="props">
        <q-td :props="props">
          <q-badge :color="meta.statusBadgeColor(props.value)" :label="props.value" />
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
      <q-card style="min-width: 550px">
        <q-toolbar class="bg-primary text-white">
          <q-toolbar-title>
            <q-icon :name="state.formDialog.mode === 'edit' ? 'edit_note' : 'add_circle'" class="q-mr-sm" />
            {{ state.formDialog.mode === 'edit' ? $t('vote.editTitle') : $t('vote.createTitle') }}
          </q-toolbar-title>
          <q-btn flat round dense icon="close" @click="actions.closeFormDialog()" />
        </q-toolbar>
        <VoteFormComponent
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
        <VoteDelete :item="state.deleteDialog.item" @deleted="actions.handleDeleted" @cancel="actions.closeDeleteDialog()" />
      </q-card>
    </q-dialog>

    <!-- VIEW DIALOG -->
    <q-dialog v-model="state.viewDialog.show" persistent>
      <q-card style="min-width: 600px">
        <q-toolbar class="bg-primary text-white">
          <q-toolbar-title>
            <q-icon name="visibility" class="q-mr-sm" />
            {{ $t('vote.viewTitle') }}
          </q-toolbar-title>
          <q-btn flat round dense icon="close" v-close-popup />
        </q-toolbar>
        <VoteView
          :item="state.viewDialog.item"
          @close="actions.closeViewDialog"
          @edit="actions.handleViewEdit"
          @delete="actions.handleViewDelete"
        />
      </q-card>
    </q-dialog>
  </div>
</template>
