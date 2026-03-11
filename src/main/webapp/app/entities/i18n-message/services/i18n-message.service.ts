import axios from 'axios';
import type { ApiResponse } from '@/shared/models/api/api-response.model';
import type { I18nMessageList } from '../models/i18n-message-list.model';
import type { I18nMessageDetail } from '../models/i18n-message-detail.model';
import type { I18nMessageForm } from '../models/i18n-message-form.model';

const ADMIN_URL = 'api/admin/i18n-messages';
const PUBLIC_URL = 'api/public/i18n-messages';

export const I18nMessageService = {
  getAll() {
    return axios.get<ApiResponse<I18nMessageDetail[]>>(ADMIN_URL);
  },

  getOne(id: number) {
    return axios.get<ApiResponse<I18nMessageDetail>>(`${ADMIN_URL}/${id}`);
  },

  create(payload: I18nMessageForm) {
    return axios.post<ApiResponse<I18nMessageForm>>(ADMIN_URL, payload);
  },

  update(id: number, payload: I18nMessageForm) {
    return axios.put<ApiResponse<I18nMessageForm>>(`${ADMIN_URL}/${id}`, payload);
  },

  deleteById(id: number) {
    return axios.delete<ApiResponse<void>>(`${ADMIN_URL}/${id}`);
  },

  getStatusList() {
    return axios.get<ApiResponse<string[]>>(`${ADMIN_URL}/status-list`);
  },

  getAllForLang(lang: string) {
    return axios.get<ApiResponse<I18nMessageList[]>>(`${PUBLIC_URL}/all`, { params: { lang } });
  },
};
