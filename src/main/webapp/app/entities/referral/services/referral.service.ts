import axios from 'axios';
import type { ApiResponse } from '@/shared/models/api/api-response.model';
import type { ReferralList } from '../models/referral-list.model';
import type { ReferralDetail } from '../models/referral-detail.model';
import type { ReferralForm } from '../models/referral-form.model';

const BASE_URL = 'api/admin/referrals';

export const ReferralService = {
  getAll(params?: { page?: number; size?: number; sort?: string }) {
    return axios.get<ApiResponse<ReferralList[]>>(BASE_URL, { params });
  },

  getOne(id: number) {
    return axios.get<ApiResponse<ReferralDetail>>(`${BASE_URL}/${id}`);
  },

  create(payload: ReferralForm) {
    return axios.post<ApiResponse<ReferralForm>>(BASE_URL, payload);
  },

  update(id: number, payload: ReferralForm) {
    return axios.put<ApiResponse<ReferralForm>>(`${BASE_URL}/${id}`, payload);
  },

  deleteById(id: number) {
    return axios.delete<ApiResponse<void>>(`${BASE_URL}/${id}`);
  },

  getStatusList() {
    return axios.get<ApiResponse<string[]>>(`${BASE_URL}/status-list`);
  },
};
