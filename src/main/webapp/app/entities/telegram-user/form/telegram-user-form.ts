import { onMounted, reactive, ref } from 'vue';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';
import { TelegramUserService } from '../services/telegram-user.service';
import type { TelegramUserForm } from '../models/telegram-user-form.model';
import type { TelegramUserDetail } from '../models/telegram-user-detail.model';

interface UseOptions {
  props: {
    item?: TelegramUserDetail | null;
    mode: 'create' | 'edit';
  };
  emit: {
    (event: 'created', data: TelegramUserForm): void;
    (event: 'updated', data: TelegramUserForm): void;
    (event: 'cancel'): void;
  };
}

const getDefaultFormData = (): TelegramUserForm => ({
  userId: undefined,
  chatId: undefined,
  username: '',
  firstName: '',
  lastName: '',
  phoneNumber: '',
  languageCode: 'uz',
  state: 'HOME',
});

export const useTelegramUserForm = ({ props, emit }: UseOptions) => {
  const $q = useQuasar();
  const { t } = useI18n();

  const state = reactive({
    formRef: ref(),
    formData: getDefaultFormData(),
    loading: false,
    loadingData: false,
    stateOptions: ['HOME', 'AWAITING_PHONE', 'AWAITING_OTP'],
  });

  const meta = {
    rules: {
      userId: [(v: any) => !!v || t('entity.validation.required')],
      chatId: [(v: any) => !!v || t('entity.validation.required')],
      firstName: [(v: any) => !!v || t('entity.validation.required')],
    },
  };

  const actions = {
    resetForm() {
      Object.assign(state.formData, getDefaultFormData());
      state.formRef?.resetValidation();
    },

    async loadOneForEdit() {
      try {
        state.loadingData = true;
        const { data: apiResponse } = await TelegramUserService.getOne(props.item!.id!);
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
        const { data: apiResponse } = await TelegramUserService.create(state.formData);
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
        const { data: apiResponse } = await TelegramUserService.update(state.formData.id!, state.formData);
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
    if (props.mode === 'edit' && props.item) {
      actions.loadOneForEdit();
    }
  });

  return { state, meta, actions };
};
