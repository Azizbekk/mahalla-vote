export interface I18nMessageList {
  id: number;
  shortCode?: string;
  name?: string;
  localizedName?: Record<string, string>;
  status?: string;
  sortOrder?: number;
  createdDate?: string;
  lastModifiedDate?: string;
}
