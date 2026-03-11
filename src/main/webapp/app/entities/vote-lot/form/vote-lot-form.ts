import { onMounted, reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { VoteLotService } from '../services/vote-lot.service';
import type { VoteLotForm } from '../models/vote-lot-form.model';
import type { VoteLotDetail } from '../models/vote-lot-detail.model';

interface UseOptions {
  props: {
    item?: VoteLotDetail | null;
    mode: 'create' | 'edit';
  };
  emit: {
    (event: 'created', data: VoteLotForm): void;
    (event: 'updated', data: VoteLotForm): void;
    (event: 'cancel'): void;
  };
}

const getDefaultFormData = (): VoteLotForm => ({
  name: '',
  openBudgetUrl: '',
  targetVoteCount: 100,
  status: 'INACTIVE',
});

export const useVoteLotForm = ({ props, emit }: UseOptions) => {
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
      name: [
        (v: any) => !!v || t('entity.validation.required'),
        (v: any) => !v || v.length <= 500 || t('entity.validation.maxlength', { max: 500 }),
      ],
      openBudgetUrl: [
        (v: any) => !!v || t('entity.validation.required'),
        (v: any) => !v || v.startsWith('https://') || 'URL https:// bilan boshlanishi kerak',
      ],
      targetVoteCount: [
        (v: any) => (v !== null && v !== undefined && v !== '') || t('entity.validation.required'),
        (v: any) => !v || v > 0 || "Kamida 1 bo'lishi kerak",
      ],
    },
  };

  const actions = {
    resetForm() {
      Object.assign(state.formData, getDefaultFormData());
      state.formRef?.resetValidation();
    },

    async loadOptions() {
      try {
        const { data: res } = await VoteLotService.getStatusList();
        state.statusOptions = res.data;
      } catch {
        state.statusOptions = ['ACTIVE', 'COMPLETED', 'INACTIVE'];
      }
    },

    async loadOneForEdit() {
      try {
        state.loadingData = true;
        const { data: apiResponse } = await VoteLotService.getOne(props.item!.id!);
        const data = apiResponse.data;
        state.formData = {
          id: data.id,
          name: data.name,
          openBudgetUrl: data.openBudgetUrl,
          targetVoteCount: data.targetVoteCount,
          status: data.status,
        };
      } catch (error: any) {
        $q.notify({ type: 'negative', message: error.message || 'Failed to load' });
        emit('cancel');
      } finally {
        state.loadingData = false;
      }
    },

    async create() {
      try {
        state.loading = true;
        const { data: apiResponse } = await VoteLotService.create(state.formData);
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
        const { data: apiResponse } = await VoteLotService.update(state.formData.id!, state.formData);
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

  onMounted(async () => {
    await actions.loadOptions();
    if (props.mode === 'edit' && props.item) {
      await actions.loadOneForEdit();
    }
  });

  return { state, meta, actions };
};
