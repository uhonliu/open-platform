# base.sql
ALTER TABLE `base_app` ADD COLUMN `is_sign` TINYINT(3) NOT NULL DEFAULT 0 COMMENT '是否验签:0-否 1-是 不允许删除' AFTER `is_persist`;
ALTER TABLE `base_app` ADD COLUMN `is_encrypt` TINYINT(3) NOT NULL DEFAULT 0 COMMENT '是否加密:0-否 1-是 不允许删除' AFTER `is_sign`;
ALTER TABLE `base_app` ADD COLUMN `encrypt_type` VARCHAR(10) NOT NULL DEFAULT 'AES' COMMENT '加密类型:DES TripleDES AES RSA' AFTER `is_encrypt`;
ALTER TABLE `base_app` ADD COLUMN `public_key` VARCHAR(2048) NOT NULL DEFAULT '' COMMENT 'RSA加解密公钥' AFTER `encrypt_type`;

# gateway.sql
ALTER TABLE `gateway_access_logs` MODIFY `error` VARCHAR(2000) DEFAULT NULL COMMENT '错误信息';
ALTER TABLE `gateway_access_logs` MODIFY `authentication` MEDIUMTEXT DEFAULT NULL COMMENT '认证信息';