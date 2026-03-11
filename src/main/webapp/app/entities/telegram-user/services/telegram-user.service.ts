import axios from 'axios';
import type { ApiResponse } from '@/shared/models/api/api-response.model';
import type { TelegramUserList } from '../models/telegram-user-list.model';
import type { TelegramUserDetail } from '../models/telegram-user-detail.model';
import type { TelegramUserForm } from '../models/telegram-user-form.model';

const BASE_URL = 'api/admin/telegram-users';

export const TelegramUserService = {
  getAll(params?: { page?: number; size?: number; sort?: string }) {
    return axios.get<ApiResponse<TelegramUserList[]>>(BASE_URL, { params });
  },

  getOne(id: number) {
    return axios.get<ApiResponse<TelegramUserDetail>>(`${BASE_URL}/${id}`);
  },

  create(payload: TelegramUserForm) {
    return axios.post<ApiResponse<TelegramUserForm>>(BASE_URL, payload);
  },

  update(id: number, payload: TelegramUserForm) {
    return axios.put<ApiResponse<TelegramUserForm>>(`${BASE_URL}/${id}`, payload);
  },

  deleteById(id: number) {
    return axios.delete<ApiResponse<void>>(`${BASE_URL}/${id}`);
  },
};
