import axios from 'axios';
import type { ApiResponse } from '@/shared/models/api/api-response.model';
import type { ProjectSettingList } from '../models/project-setting-list.model';
import type { ProjectSettingDetail } from '../models/project-setting-detail.model';
import type { ProjectSettingForm } from '../models/project-setting-form.model';

const BASE_URL = 'api/admin/project-settings';

export const ProjectSettingService = {
  getAll() {
    return axios.get<ApiResponse<ProjectSettingList[]>>(BASE_URL);
  },

  getOne(id: number) {
    return axios.get<ApiResponse<ProjectSettingDetail>>(`${BASE_URL}/${id}`);
  },

  create(payload: ProjectSettingForm) {
    return axios.post<ApiResponse<ProjectSettingForm>>(BASE_URL, payload);
  },

  update(id: number, payload: ProjectSettingForm) {
    return axios.put<ApiResponse<ProjectSettingForm>>(`${BASE_URL}/${id}`, payload);
  },

  deleteById(id: number) {
    return axios.delete<ApiResponse<void>>(`${BASE_URL}/${id}`);
  },

  getValueTypeList() {
    return axios.get<ApiResponse<string[]>>(`${BASE_URL}/value-type-list`);
  },

  getStatusList() {
    return axios.get<ApiResponse<string[]>>(`${BASE_URL}/status-list`);
  },
};
