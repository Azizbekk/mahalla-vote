import { onMounted, reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { VoteService } from '../services/vote.service';
import type { VoteForm } from '../models/vote-form.model';
import type { VoteDetail } from '../models/vote-detail.model';

interface UseOptions {
  props: {
    item?: VoteDetail | null;
    mode: 'create' | 'edit';
  };
  emit: {
    (event: 'created', data: VoteForm): void;
    (event: 'updated', data: VoteForm): void;
    (event: 'cancel'): void;
  };
}

const getDefaultFormData = (): VoteForm => ({
  voterId: undefined,
  phoneNumber: '',
  amount: undefined,
  status: 'PENDING',
});

export const useVoteForm = ({ props, emit }: UseOptions) => {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    formRef: ref(),
    formData: getDefaultFormData(),
    loading: false,
    loadingData: false,
    statusOptions: [] as string[],
  });

  const meta = {
    rules: {
      voterId: [(v: any) => !!v || t('entity.validation.required')],
      phoneNumber: [(v: any) => !!v || t('entity.validation.required')],
      amount: [(v: any) => !!v || t('entity.validation.required')],
    },
  };

  const actions = {
    resetForm() {
      Object.assign(state.formData, getDefaultFormData());
      state.formRef?.resetValidation();
    },

    async loadStatusList() {
      try {
        const { data: apiResponse } = await VoteService.getAllStatusList();
        state.statusOptions = apiResponse.data;
      } catch (error: any) {
        state.statusOptions = ['PENDING', 'VOTED', 'PAID'];
      }
    },

    async loadOneForEdit() {
      try {
        state.loadingData = true;
        const { data: apiResponse } = await VoteService.getOne(props.item!.id!);
        state.formData = apiResponse.data;
      } catch (error: any) {
        $q.notify({ type: 'negative', message: error.message || t('error.loadFailed') });
        emit('cancel');
      } finally {
        state.loadingData = false;
      }
    },

    async create() {
      try {
        state.loading = true;
        const { data: apiResponse } = await VoteService.create(state.formData);
        $q.notify({ type: 'positive', message: t('entity.createSuccess'), icon: 'check_circle_outline' });
        emit('created', apiResponse.data);
      } catch (error: any) {
        $q.notify({ type: 'negative', message: error.message || t('entity.createFailed'), icon: 'error_outline' });
      } finally {
        state.loading = false;
      }
    },

    async update() {
      try {
        state.loading = true;
        const { data: apiResponse } = await VoteService.update(state.formData.id!, state.formData);
        $q.notify({ type: 'positive', message: t('entity.updateSuccess'), icon: 'check_circle_outline' });
        emit('updated', apiResponse.data);
      } catch (error: any) {
        $q.notify({ type: 'negative', message: error.message || t('entity.updateFailed'), icon: 'error_outline' });
      } finally {
        state.loading = false;
      }
    },

    async submit() {
      const valid = await state.formRef?.validate();
      if (!valid) return;
      if (props.mode === 'edit') {
        await actions.update();
      } else {
        await actions.create();
      }
    },

    cancel() {
      actions.resetForm();
      emit('cancel');
    },
  };

  onMounted(() => {
    actions.loadStatusList();
    if (props.mode === 'edit' && props.item) {
      actions.loadOneForEdit();
    }
  });

  return { state, meta, actions };
};
