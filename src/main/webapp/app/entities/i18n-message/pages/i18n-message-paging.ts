import { onMounted, reactive } from 'vue';
import { useQuasar, type QTableColumn } from 'quasar';
import { useI18n } from 'vue-i18n';
import { I18nMessageService } from '../services/i18n-message.service';
import type { I18nMessageDetail } from '../models/i18n-message-detail.model';

export function useI18nMessagePaging() {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    loading: false,
    rows: [] as I18nMessageDetail[],
    globalSearch: '',
    formDialog: {
      show: false,
      mode: 'create' as 'create' | 'edit',
      item: null as I18nMessageDetail | null,
    },
    deleteDialog: {
      show: false,
      item: null as I18nMessageDetail | null,
    },
    viewDialog: {
      show: false,
      item: null as I18nMessageDetail | null,
    },
  });

  const meta = {
    get columns(): QTableColumn[] {
      return [
        { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
        { name: 'shortCode', label: t('i18nMessage.shortCode'), field: 'shortCode', align: 'left', sortable: true },
        { name: 'uzLat', label: "O'zbekcha", field: (row: any) => row.localizedName?.uzLat ?? '', align: 'left' },
        { name: 'en', label: 'English', field: (row: any) => row.localizedName?.en ?? '', align: 'left' },
        { name: 'ru', label: 'Русский', field: (row: any) => row.localizedName?.ru ?? '', align: 'left' },
        { name: 'status', label: t('i18nMessage.status'), field: 'status', align: 'center' },
        { name: 'sortOrder', label: t('i18nMessage.sortOrder'), field: 'sortOrder', align: 'center', sortable: true },
        { name: 'actions', label: '', field: 'actions', align: 'center' },
      ];
    },
  };

  const actions = {
    async fetchAll() {
      try {
        state.loading = true;
        const { data } = await I18nMessageService.getAll();
        state.rows = data.data;
      } catch (error: any) {
        $q.notify({ type: 'negative', message: error.message || 'Failed to load' });
        state.rows = [];
      } finally {
        state.loading = false;
      }
    },

    refresh() {
      actions.fetchAll();
    },

    openCreateDialog() {
      state.formDialog.show = true;
      state.formDialog.mode = 'create';
      state.formDialog.item = null;
    },
    openEditDialog(item: I18nMessageDetail) {
      state.formDialog.show = true;
      state.formDialog.mode = 'edit';
      state.formDialog.item = item;
    },
    closeFormDialog() {
      state.formDialog.show = false;
      state.formDialog.item = null;
    },
    handleCreated() {
      actions.closeFormDialog();
      actions.fetchAll();
    },
    handleUpdated() {
      actions.closeFormDialog();
      actions.fetchAll();
    },

    openDeleteDialog(item: I18nMessageDetail) {
      state.deleteDialog.show = true;
      state.deleteDialog.item = item;
    },
    closeDeleteDialog() {
      state.deleteDialog.show = false;
      state.deleteDialog.item = null;
    },
    handleDeleted() {
      actions.closeDeleteDialog();
      actions.fetchAll();
    },

    openViewDialog(item: I18nMessageDetail) {
      state.viewDialog.show = true;
      state.viewDialog.item = item;
    },
    closeViewDialog() {
      state.viewDialog.show = false;
      state.viewDialog.item = null;
    },
    handleViewEdit() {
      const item = state.viewDialog.item;
      state.viewDialog.show = false;
      state.formDialog.item = item;
      state.formDialog.mode = 'edit';
      state.formDialog.show = true;
    },
    handleViewDelete() {
      const item = state.viewDialog.item;
      state.viewDialog.show = false;
      state.deleteDialog.item = item;
      state.deleteDialog.show = true;
    },
  };

  onMounted(() => {
    actions.fetchAll();
  });

  return { state, meta, actions };
}
