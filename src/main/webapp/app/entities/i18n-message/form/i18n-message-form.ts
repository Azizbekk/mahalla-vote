import { onMounted, reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { I18nMessageService } from '../services/i18n-message.service';
import type { I18nMessageForm } from '../models/i18n-message-form.model';
import type { I18nMessageDetail } from '../models/i18n-message-detail.model';

interface UseOptions {
  props: {
    item?: I18nMessageDetail | null;
    mode: 'create' | 'edit';
  };
  emit: {
    (event: 'created', data: I18nMessageForm): void;
    (event: 'updated', data: I18nMessageForm): void;
    (event: 'cancel'): void;
  };
}

const getDefaultFormData = (): I18nMessageForm => ({
  shortCode: '',
  localizedName: {
    uzLat: '',
    en: '',
    ru: '',
    uzCrl: '',
    krLat: '',
  },
  status: 'ACTIVE',
  sortOrder: 0,
});

export const useI18nMessageForm = ({ props, emit }: UseOptions) => {
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
      shortCode: [
        (v: any) => !!v || t('entity.validation.required'),
        (v: any) => !v || v.length <= 500 || t('entity.validation.maxlength', { max: 500 }),
      ],
      uzLat: [(v: any) => !!v || t('entity.validation.required')],
    },
    languages: [
      { key: 'uzLat', label: "O'zbekcha (Lotin)", icon: 'translate' },
      { key: 'en', label: 'English', icon: 'language' },
      { key: 'ru', label: 'Русский', icon: 'language' },
      { key: 'uzCrl', label: 'Ўзбекча (Кирилл)', icon: 'translate' },
      { key: 'krLat', label: 'Qaraqalpaqsha', icon: 'translate' },
    ],
  };

  const actions = {
    resetForm() {
      Object.assign(state.formData, getDefaultFormData());
      state.formRef?.resetValidation();
    },

    async loadOptions() {
      try {
        const { data: res } = await I18nMessageService.getStatusList();
        state.statusOptions = res.data;
      } catch {
        state.statusOptions = ['ACTIVE', 'INACTIVE'];
      }
    },

    async loadOneForEdit() {
      try {
        state.loadingData = true;
        const { data: apiResponse } = await I18nMessageService.getOne(props.item!.id!);
        const data = apiResponse.data;
        state.formData = {
          id: data.id,
          shortCode: data.shortCode,
          localizedName: data.localizedName || { uzLat: '', en: '', ru: '', uzCrl: '', krLat: '' },
          status: data.status,
          sortOrder: data.sortOrder,
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
        const { data: apiResponse } = await I18nMessageService.create(state.formData);
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
        const { data: apiResponse } = await I18nMessageService.update(state.formData.id!, state.formData);
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
