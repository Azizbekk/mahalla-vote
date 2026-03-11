export interface LocalizedName {
  uzLat?: string;
  en?: string;
  ru?: string;
  uzCrl?: string;
  krLat?: string;
}

export interface I18nMessageForm {
  id?: number;
  shortCode?: string;
  localizedName: LocalizedName;
  status?: string;
  sortOrder?: number;
}
