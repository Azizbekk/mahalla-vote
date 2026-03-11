-- Seed data for app_setting table
-- Marker: id < 100 = seed data

DELETE FROM app_setting WHERE id < 100;

INSERT INTO app_setting (id, setting_key, setting_value, value_type, description, status, created_date, last_modified_date)
VALUES
    (1, 'VOTE_AMOUNT', '5000', 'NUMBER', 'Har bir ovoz uchun to''lanadigan summa', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'REFERRAL_VOTE_AMOUNT', '3000', 'NUMBER', 'Referal ovoz berganda beriladigan mukofot', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'WITHDRAW_LIMIT', '50000', 'NUMBER', 'Minimal pul yechish limiti', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'TELEGRAM_BOT_TOKEN', '', 'PASSWORD', 'Telegram bot tokeni', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'TELEGRAM_BOT_USERNAME', '', 'STRING', 'Telegram bot username', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'ANTICAPTCHA_API_KEY', '', 'PASSWORD', 'AntiCaptcha API kaliti', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (setting_key) DO UPDATE SET
    value_type = EXCLUDED.value_type,
    description = EXCLUDED.description,
    last_modified_date = CURRENT_TIMESTAMP;
