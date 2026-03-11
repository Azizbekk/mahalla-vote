import { onMounted, reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { ProjectSettingService } from '../services/project-setting.service';
import type { ProjectSettingForm } from '../models/project-setting-form.model';
import type { ProjectSettingDetail } from '../models/project-setting-detail.model';

interface UseOptions {
  props: {
    item?: ProjectSettingDetail | null;
    mode: 'create' | 'edit';
  };
  emit: {
    (event: 'created', data: ProjectSettingForm): void;
    (event: 'updated', data: ProjectSettingForm): void;
    (event: 'cancel'): void;
  };
}

const getDefaultFormData = (): ProjectSettingForm => ({
  settingKey: '',
  settingValue: '',
  valueType: 'STRING',
  description: '',
  status: 'ACTIVE',
});

export const useProjectSettingForm = ({ props, emit }: UseOptions) => {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    formRef: ref(),
    formData: getDefaultFormData(),
    loading: false,
    loadingData: false,
    showPassword: false,
    valueTypeOptions: [] as string[],
    statusOptions: [] as string[],
  });

  const meta = {
    rules: {
      settingKey: [
        (v: any) => !!v || t('entity.validation.required'),
        (v: any) => !v || v.length <= 100 || t('entity.validation.maxlength', { max: 100 }),
      ],
      settingValue: [(v: any) => (v !== undefined && v !== null && v !== '') || t('entity.validation.required')],
      valueType: [(v: any) => !!v || t('entity.validation.required')],
      status: [(v: any) => !!v || t('entity.validation.required')],
    },
  };

  const actions = {
    resetForm() {
      Object.assign(state.formData, getDefaultFormData());
      state.formRef?.resetValidation();
      state.showPassword = false;
    },

    async loadOptions() {
      try {
        const [vtRes, stRes] = await Promise.all([ProjectSettingService.getValueTypeList(), ProjectSettingService.getStatusList()]);
        state.valueTypeOptions = vtRes.data.data;
        state.statusOptions = stRes.data.data;
      } catch {
        state.valueTypeOptions = ['STRING', 'NUMBER', 'BOOLEAN', 'PASSWORD', 'JSON'];
        state.statusOptions = ['ACTIVE', 'INACTIVE'];
      }
    },

    async loadOneForEdit() {
      try {
        state.loadingData = true;
        const { data: apiResponse } = await ProjectSettingService.getOne(props.item!.id!);
        state.formData = apiResponse.data;
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
        const { data: apiResponse } = await ProjectSettingService.create(state.formData);
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
        const { data: apiResponse } = await ProjectSettingService.update(state.formData.id!, state.formData);
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
