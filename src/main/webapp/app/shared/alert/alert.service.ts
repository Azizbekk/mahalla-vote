import { Notify } from 'quasar';
import { type Composer, useI18n } from 'vue-i18n';

export const useAlertService = () => {
  const i18n = useI18n();
  return new AlertService({ i18n });
};

export default class AlertService {
  private i18n: Composer;

  constructor({ i18n }: { i18n: Composer }) {
    this.i18n = i18n;
  }

  showInfo(message: string, options?: any) {
    Notify.create({
      message,
      type: 'info',
      position: 'top',
      timeout: 5000,
      icon: 'info',
      ...options,
    });
  }

  showSuccess(message: string) {
    Notify.create({
      message,
      type: 'positive',
      position: 'top',
      timeout: 5000,
      icon: 'check_circle',
    });
  }

  showError(message: string) {
    Notify.create({
      message,
      type: 'negative',
      position: 'top',
      timeout: 5000,
      icon: 'error',
    });
  }

  showHttpError(httpErrorResponse: any) {
    let errorMessage: string | null = null;
    switch (httpErrorResponse.status) {
      case 0:
        errorMessage = this.i18n.t('error.server.not.reachable').toString();
        break;

      case 400: {
        const arr = Object.keys(httpErrorResponse.headers);
        let entityKey: string | null = null;
        for (const entry of arr) {
          if (entry.toLowerCase().endsWith('app-error')) {
            errorMessage = httpErrorResponse.headers[entry];
          } else if (entry.toLowerCase().endsWith('app-params')) {
            entityKey = httpErrorResponse.headers[entry];
          }
        }
        if (errorMessage && entityKey) {
          errorMessage = this.i18n.t(errorMessage, { entityName: this.i18n.t(`global.menu.entities.${entityKey}`) }).toString();
        } else if (!errorMessage) {
          errorMessage = this.i18n.t(httpErrorResponse.data.message).toString();
        }
        break;
      }

      case 404:
        errorMessage = this.i18n.t('error.http.404').toString();
        break;

      default:
        errorMessage = this.i18n.t(httpErrorResponse.data.message).toString();
    }
    this.showError(errorMessage);
  }
}
