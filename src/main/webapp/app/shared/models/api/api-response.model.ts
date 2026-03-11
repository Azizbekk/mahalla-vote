export interface ApiResponse<T = any> {
  message?: string;
  data: T;
}

export interface PagingConfig {
  page: number;
  size: number;
  sortBy?: string;
  descending?: boolean;
}

export interface PagingRequest<T = any> {
  paging: PagingConfig;
  columnFilters?: any[];
  globalFilters?: T;
}

export interface PagingResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  first: boolean;
  last: boolean;
}
