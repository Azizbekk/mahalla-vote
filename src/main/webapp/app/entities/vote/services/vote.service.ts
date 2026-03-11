import axios from 'axios';
import type { ApiResponse } from '@/shared/models/api/api-response.model';
import type { VoteList } from '../models/vote-list.model';
import type { VoteDetail } from '../models/vote-detail.model';
import type { VoteForm } from '../models/vote-form.model';

const BASE_URL = 'api/admin/votes';

export const VoteService = {
  getAll(params?: { page?: number; size?: number; sort?: string }) {
    return axios.get<ApiResponse<VoteList[]>>(BASE_URL, { params });
  },

  getOne(id: number) {
    return axios.get<ApiResponse<VoteDetail>>(`${BASE_URL}/${id}`);
  },

  create(payload: VoteForm) {
    return axios.post<ApiResponse<VoteForm>>(BASE_URL, payload);
  },

  update(id: number, payload: VoteForm) {
    return axios.put<ApiResponse<VoteForm>>(`${BASE_URL}/${id}`, payload);
  },

  deleteById(id: number) {
    return axios.delete<ApiResponse<void>>(`${BASE_URL}/${id}`);
  },

  getAllStatusList() {
    return axios.get<ApiResponse<string[]>>(`${BASE_URL}/status-list`);
  },
};
