import { onMounted, reactive } from 'vue';
import { useQuasar, type QTableColumn } from 'quasar';
import { useI18n } from 'vue-i18n';
import { ReferralService } from '../services/referral.service';
import type { ReferralDetail } from '../models/referral-detail.model';

export function useReferralPaging() {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    loading: false,
    rows: [] as ReferralDetail[],
    globalSearch: '',
    pagination: {
      page: 1,
      rowsPerPage: 20,
      rowsNumber: 0,
      sortBy: 'id' as string | undefined,
      descending: true,
    },
    formDialog: {
      show: false,
      mode: 'create' as 'create' | 'edit',
      item: null as ReferralDetail | null,
    },
    deleteDialog: {
      show: false,
      item: null as ReferralDetail | null,
    },
    viewDialog: {
      show: false,
      item: null as ReferralDetail | null,
    },
  });

  const meta = {
    get columns(): QTableColumn[] {
      return [
        { name: 'id', label: 'ID', field: 'id', align: 'left', sortable: true },
        { name: 'referrerId', label: t('referral.referrerId'), field: 'referrerId', align: 'left', sortable: true },
        { name: 'referredId', label: t('referral.referredId'), field: 'referredId', align: 'left', sortable: true },
        { name: 'status', label: t('referral.status'), field: 'status', align: 'center' },
        { name: 'createdDate', label: t('referral.createdDate'), field: 'createdDate', align: 'left', sortable: true },
        { name: 'lastModifiedDate', label: t('referral.lastModifiedDate'), field: 'lastModifiedDate', align: 'left', sortable: true },
        { name: 'votedAt', label: t('referral.votedAt'), field: 'votedAt', align: 'left', sortable: true },
        { name: 'paidAt', label: t('referral.paidAt'), field: 'paidAt', align: 'left', sortable: true },
        { name: 'actions', label: t('entity.actions'), field: 'actions', align: 'center' },
      ];
    },
  };

  const actions = {
    async fetchPaging() {
      try {
        state.loading = true;
        const params = {
          page: state.pagination.page - 1,
          size: state.pagination.rowsPerPage,
          sort: state.pagination.sortBy ? `${state.pagination.sortBy},${state.pagination.descending ? 'desc' : 'asc'}` : undefined,
        };
        const { data, headers } = await ReferralService.getAll(params);
        state.rows = data.data;
        state.pagination.rowsNumber = parseInt(headers['x-total-count'] || '0', 10);
      } catch (error: any) {
        $q.notify({ type: 'negative', message: t('error.loadFailed') });
        state.rows = [];
      } finally {
        state.loading = false;
      }
    },

    handleRequest(props: any) {
      state.pagination.page = props.pagination.page;
      state.pagination.rowsPerPage = props.pagination.rowsPerPage;
      state.pagination.sortBy = props.pagination.sortBy;
      state.pagination.descending = props.pagination.descending;
      actions.fetchPaging();
    },

    refresh() {
      actions.fetchPaging();
    },

    // Form dialog
    openCreateDialog() {
      state.formDialog.show = true;
      state.formDialog.mode = 'create';
      state.formDialog.item = null;
    },
    openEditDialog(item: ReferralDetail) {
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
      actions.fetchPaging();
    },
    handleUpdated() {
      actions.closeFormDialog();
      actions.fetchPaging();
    },

    // Delete dialog
    openDeleteDialog(item: ReferralDetail) {
      state.deleteDialog.show = true;
      state.deleteDialog.item = item;
    },
    closeDeleteDialog() {
      state.deleteDialog.show = false;
      state.deleteDialog.item = null;
    },
    handleDeleted() {
      actions.closeDeleteDialog();
      actions.fetchPaging();
    },

    // View dialog
    openViewDialog(item: ReferralDetail) {
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
    actions.fetchPaging();
  });

  return { state, meta, actions };
}
