import { type IntlDateTimeFormats, createI18n } from 'vue-i18n';

const datetimeFormats: IntlDateTimeFormats = {
  'uz-Latn-uz': {
    short: { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' },
    medium: { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short', hour: 'numeric', minute: 'numeric' },
    long: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long', hour: 'numeric', minute: 'numeric' },
  },
  en: {
    short: { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' },
    medium: { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short', hour: 'numeric', minute: 'numeric' },
    long: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long', hour: 'numeric', minute: 'numeric' },
  },
  ru: {
    short: { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' },
    medium: { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short', hour: 'numeric', minute: 'numeric' },
    long: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long', hour: 'numeric', minute: 'numeric' },
  },
  'uz-Cyrl-uz': {
    short: { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' },
    medium: { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short', hour: 'numeric', minute: 'numeric' },
    long: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long', hour: 'numeric', minute: 'numeric' },
  },
  'kr-Latn-kr': {
    short: { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' },
    medium: { year: 'numeric', month: 'short', day: 'numeric', weekday: 'short', hour: 'numeric', minute: 'numeric' },
    long: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long', hour: 'numeric', minute: 'numeric' },
  },
};

export function initI18N(opts: any = {}) {
  return createI18n({
    missingWarn: false,
    fallbackWarn: false,
    legacy: false,
    datetimeFormats,
    silentTranslationWarn: true,
    ...opts,
  });
}
