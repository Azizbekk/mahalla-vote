import { onMounted, reactive } from 'vue';
import { useQuasar, type QTableColumn } from 'quasar';
import { useI18n } from 'vue-i18n';
import { ProjectSettingService } from '../services/project-setting.service';
import type { ProjectSettingDetail } from '../models/project-setting-detail.model';

export function useProjectSettingPaging() {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    loading: false,
    rows: [] as ProjectSettingDetail[],
    globalSearch: '',
    formDialog: {
      show: false,
      mode: 'create' as 'create' | 'edit',
      item: null as ProjectSettingDetail | null,
    },
    deleteDialog: {
      show: false,
      item: null as ProjectSettingDetail | null,
    },
    viewDialog: {
      show: false,
      item: null as ProjectSettingDetail | null,
    },
  });

  const meta = {
    get columns(): QTableColumn[] {
      return [
        { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
        { name: 'settingKey', label: t('projectSetting.settingKey'), field: 'settingKey', align: 'left', sortable: true },
        { name: 'settingValue', label: t('projectSetting.settingValue'), field: 'settingValue', align: 'left' },
        { name: 'valueType', label: t('projectSetting.valueType'), field: 'valueType', align: 'center' },
        { name: 'description', label: t('projectSetting.description'), field: 'description', align: 'left' },
        { name: 'status', label: t('projectSetting.status'), field: 'status', align: 'center' },
        { name: 'actions', label: '', field: 'actions', align: 'center' },
      ];
    },
  };

  const actions = {
    async fetchAll() {
      try {
        state.loading = true;
        const { data } = await ProjectSettingService.getAll();
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

    // Form dialog
    openCreateDialog() {
      state.formDialog.show = true;
      state.formDialog.mode = 'create';
      state.formDialog.item = null;
    },
    openEditDialog(item: ProjectSettingDetail) {
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

    // Delete dialog
    openDeleteDialog(item: ProjectSettingDetail) {
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

    // View dialog
    openViewDialog(item: ProjectSettingDetail) {
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
