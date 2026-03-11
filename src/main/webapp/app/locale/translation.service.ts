import axios from 'axios';
import { type Composer } from 'vue-i18n';
import dayjs from 'dayjs';
import languages from '@/shared/config/languages';

const LANG_KEY_MAP: Record<string, string> = {
  'uz-Latn-uz': 'uzLat',
  en: 'en',
  ru: 'ru',
  'uz-Cyrl-uz': 'uzCrl',
  'kr-Latn-kr': 'krLat',
};

function setNestedValue(obj: Record<string, any>, path: string, value: string) {
  const keys = path.split('.');
  let current = obj;
  for (let i = 0; i < keys.length - 1; i++) {
    if (!current[keys[i]] || typeof current[keys[i]] !== 'object') {
      current[keys[i]] = {};
    }
    current = current[keys[i]];
  }
  current[keys[keys.length - 1]] = value;
}

export default class TranslationService {
  private readonly i18n: Composer;
  private languages = languages();

  constructor(i18n: Composer) {
    this.i18n = i18n;
  }

  async refreshTranslation(newLanguage: string) {
    await this.loadDbTranslations(newLanguage);
  }

  private async loadDbTranslations(language: string) {
    try {
      const langKey = LANG_KEY_MAP[language] || 'uzLat';
      const { data: apiResponse } = await axios.get('api/public/i18n-messages/all', { params: { lang: langKey } });
      const messages: Array<{ shortCode: string; name: string }> = apiResponse.data || [];

      if (messages.length === 0) return;

      const translations: Record<string, any> = {};

      for (const msg of messages) {
        if (msg.shortCode && msg.name) {
          setNestedValue(translations, msg.shortCode, msg.name);
        }
      }

      this.i18n.setLocaleMessage(language, translations);
    } catch {
      // DB not available yet
    }
  }

  setLocale(lang: string) {
    dayjs.locale(lang);
    this.i18n.locale.value = lang;
    axios.defaults.headers.common['Accept-Language'] = lang;
    document.querySelector('html').setAttribute('lang', lang);
  }

  isLanguageSupported(lang: string) {
    return Boolean(this.languages[lang]);
  }

  getLocalStoreLanguage(): string | null {
    return localStorage.getItem('currentLanguage');
  }
}
