import axios from 'axios';
import type { ApiResponse } from '@/shared/models/api/api-response.model';
import type { VoteLotDetail } from '../models/vote-lot-detail.model';
import type { VoteLotForm } from '../models/vote-lot-form.model';

const ADMIN_URL = 'api/admin/vote-lots';

export const VoteLotService = {
  getAll(params?: any) {
    return axios.get<ApiResponse<VoteLotDetail[]>>(ADMIN_URL, { params });
  },

  getOne(id: number) {
    return axios.get<ApiResponse<VoteLotDetail>>(`${ADMIN_URL}/${id}`);
  },

  create(payload: VoteLotForm) {
    return axios.post<ApiResponse<VoteLotForm>>(ADMIN_URL, payload);
  },

  update(id: number, payload: VoteLotForm) {
    return axios.put<ApiResponse<VoteLotForm>>(`${ADMIN_URL}/${id}`, payload);
  },

  deleteById(id: number) {
    return axios.delete<ApiResponse<void>>(`${ADMIN_URL}/${id}`);
  },

  getStatusList() {
    return axios.get<ApiResponse<string[]>>(`${ADMIN_URL}/status-list`);
  },
};
