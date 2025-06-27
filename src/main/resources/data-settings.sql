
-- Settings Management Module Schema and Sample Data

-- Create tables
CREATE TABLE IF NOT EXISTS setting_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS setting_subcategories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES setting_categories(id) ON DELETE CASCADE,
    UNIQUE KEY unique_subcategory_per_category (name, category_id)
);

CREATE TABLE IF NOT EXISTS setting_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_key VARCHAR(100) NOT NULL,
    label VARCHAR(255) NOT NULL,
    field_value TEXT,
    field_type ENUM('text', 'textarea', 'radio', 'dropdown', 'multi-select', 'checkbox', 'boolean') NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    options JSON,
    subcategory_id BIGINT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subcategory_id) REFERENCES setting_subcategories(id) ON DELETE CASCADE,
    UNIQUE KEY unique_key_per_subcategory (field_key, subcategory_id)
);

-- Insert sample data
INSERT INTO setting_categories (name, description) VALUES
('GENERAL', 'General application settings'),
('PAYMENT', 'Payment gateway configurations'),
('COMMUNICATION', 'Email and notification settings');

INSERT INTO setting_subcategories (name, category_id, description) VALUES
('SMTP', 1, 'Email server configuration'),
('APPLICATION', 1, 'General application settings'),
('STRIPE', 2, 'Stripe payment gateway settings'),
('RAZORPAY', 2, 'Razorpay payment gateway settings'),
('EMAIL_TEMPLATES', 3, 'Email template configurations'),
('NOTIFICATIONS', 3, 'Push notification settings');

INSERT INTO setting_fields (field_key, label, field_value, field_type, is_required, options, subcategory_id, description) VALUES
-- SMTP Settings
('smtp_host', 'SMTP Host', 'smtp.gmail.com', 'text', true, null, 1, 'SMTP server hostname'),
('smtp_port', 'SMTP Port', '587', 'dropdown', true, '["25", "465", "587", "993"]', 1, 'SMTP server port'),
('smtp_username', 'SMTP Username', '', 'text', true, null, 1, 'SMTP authentication username'),
('smtp_password', 'SMTP Password', '', 'text', true, null, 1, 'SMTP authentication password'),
('smtp_ssl_enabled', 'Enable SSL', 'true', 'boolean', false, null, 1, 'Enable SSL encryption'),

-- Application Settings
('app_name', 'Application Name', 'RBAC System', 'text', true, null, 2, 'Application display name'),
('app_logo_url', 'Logo URL', '/assets/logo.png', 'text', false, null, 2, 'Application logo URL'),
('maintenance_mode', 'Maintenance Mode', 'false', 'boolean', false, null, 2, 'Enable maintenance mode'),
('max_login_attempts', 'Max Login Attempts', '5', 'dropdown', true, '["3", "5", "10", "unlimited"]', 2, 'Maximum failed login attempts'),

-- Stripe Settings
('stripe_public_key', 'Stripe Public Key', '', 'text', true, null, 3, 'Stripe publishable key'),
('stripe_secret_key', 'Stripe Secret Key', '', 'text', true, null, 3, 'Stripe secret key'),
('stripe_webhook_secret', 'Webhook Secret', '', 'text', false, null, 3, 'Stripe webhook endpoint secret'),
('stripe_mode', 'Mode', 'test', 'radio', true, '["test", "live"]', 3, 'Stripe operation mode'),

-- Razorpay Settings
('razorpay_key_id', 'Key ID', '', 'text', true, null, 4, 'Razorpay key ID'),
('razorpay_key_secret', 'Key Secret', '', 'text', true, null, 4, 'Razorpay key secret'),
('razorpay_webhook_secret', 'Webhook Secret', '', 'text', false, null, 4, 'Razorpay webhook secret'),

-- Email Templates
('welcome_email_subject', 'Welcome Email Subject', 'Welcome to {{app_name}}', 'text', true, null, 5, 'Subject for welcome emails'),
('welcome_email_template', 'Welcome Email Template', 'Dear {{user_name}}, welcome to our platform!', 'textarea', true, null, 5, 'Welcome email HTML template'),
('password_reset_subject', 'Password Reset Subject', 'Reset your password', 'text', true, null, 5, 'Subject for password reset emails'),

-- Notifications
('push_notifications_enabled', 'Enable Push Notifications', 'true', 'boolean', false, null, 6, 'Enable push notifications'),
('notification_types', 'Notification Types', '["email", "sms"]', 'multi-select', false, '["email", "sms", "push", "in-app"]', 6, 'Enabled notification types');
