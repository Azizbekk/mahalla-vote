import { onMounted, reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { ReferralService } from '../services/referral.service';
import type { ReferralForm } from '../models/referral-form.model';
import type { ReferralDetail } from '../models/referral-detail.model';

interface UseOptions {
  props: {
    item?: ReferralDetail | null;
    mode: 'create' | 'edit';
  };
  emit: {
    (event: 'created', data: ReferralForm): void;
    (event: 'updated', data: ReferralForm): void;
    (event: 'cancel'): void;
  };
}

const getDefaultFormData = (): ReferralForm => ({
  referrerId: undefined,
  referredId: undefined,
  status: 'REGISTERED',
});

export const useReferralForm = ({ props, emit }: UseOptions) => {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    formRef: ref(),
    formData: getDefaultFormData(),
    loading: false,
    loadingData: false,
    statusOptions: ['REGISTERED', 'VOTED', 'PAID'] as string[],
  });

  const meta = {
    rules: {
      referrerId: [(v: any) => !!v || t('entity.validation.required')],
      referredId: [(v: any) => !!v || t('entity.validation.required')],
    },
  };

  const actions = {
    resetForm() {
      Object.assign(state.formData, getDefaultFormData());
      state.formRef?.resetValidation();
    },

    async loadStatusList() {
      try {
        const { data: apiResponse } = await ReferralService.getStatusList();
        state.statusOptions = apiResponse.data;
      } catch {
        // fallback to defaults
      }
    },

    async loadOneForEdit() {
      try {
        state.loadingData = true;
        const { data: apiResponse } = await ReferralService.getOne(props.item!.id!);
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
        const { data: apiResponse } = await ReferralService.create(state.formData);
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
        const { data: apiResponse } = await ReferralService.update(state.formData.id!, state.formData);
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
