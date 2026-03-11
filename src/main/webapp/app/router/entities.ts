import { Authority } from '@/shared/security/authority';

const Entities = () => import('@/entities/entities.vue');
const TelegramUserPaging = () => import('@/entities/telegram-user/pages/telegram-user-paging.vue');
const VotePaging = () => import('@/entities/vote/pages/vote-paging.vue');
const ReferralPaging = () => import('@/entities/referral/pages/referral-paging.vue');
const ProjectSettingPaging = () => import('@/entities/project-setting/pages/project-setting-paging.vue');
const I18nMessagePaging = () => import('@/entities/i18n-message/pages/i18n-message-paging.vue');
const VoteLotPaging = () => import('@/entities/vote-lot/pages/vote-lot-paging.vue');

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'telegram-user',
      name: 'TelegramUser',
      component: TelegramUserPaging,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'vote',
      name: 'Vote',
      component: VotePaging,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'referral',
      name: 'Referral',
      component: ReferralPaging,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'project-setting',
      name: 'ProjectSetting',
      component: ProjectSettingPaging,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'i18n-message',
      name: 'I18nMessage',
      component: I18nMessagePaging,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'vote-lot',
      name: 'VoteLot',
      component: VoteLotPaging,
      meta: { authorities: [Authority.ADMIN] },
    },
  ],
};
