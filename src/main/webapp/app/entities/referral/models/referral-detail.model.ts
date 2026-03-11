export interface ReferralDetail {
  id: number;
  referrerId?: number;
  referredId?: number;
  status?: string;
  createdDate?: string;
  lastModifiedDate?: string;
  votedAt?: string;
  paidAt?: string;
}
