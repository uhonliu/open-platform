/*
 *初始化表分区
 *表没有数据，添加本月分区
 *表有数据，将所有数据按月分区
 @tableName 表名
 @columnName 时间字段列名
*/
DROP PROCEDURE IF EXISTS partitionInit;
DELIMITER $$
CREATE PROCEDURE partitionInit(IN tableName VARCHAR(50), IN columnName VARCHAR(50))
    COMMENT '初始化表分区'
BEGIN
    SET @s0 = CONCAT('select max(', columnName, ') into @a from ', tableName);
    PREPARE stmt0 FROM @s0;
    EXECUTE stmt0;
    DEALLOCATE PREPARE stmt0;

    SET @s1 = CONCAT('select min(', columnName, ') into @b from ', tableName);
    PREPARE stmt1 FROM @s1;
    EXECUTE stmt1;
    DEALLOCATE PREPARE stmt1;

    SET @maxDate = @a;
    SET @minDate = @b;
    SET @s2 = CONCAT('alter table ', tableName, ' partition by RANGE(to_days(', columnName, '))(');

    SET @endDate = NOW();
    IF @minDate IS NULL
    THEN
        SET @minDate = @endDate;
        SET @maxDate = @endDate;
    END IF;
    SET @minDate = DATE(DATE(@minDate) - DAY(@minDate) + 1);
    SET @maxDate = DATE(DATE(@maxDate) - DAY(@maxDate) + 1);

    WHILE TO_DAYS(@minDate) < TO_DAYS(@maxDate)
        DO
            SET @s2 = CONCAT(@s2, ' PARTITION ', tableName, '_', DATE_FORMAT(@minDate, '%Y%m'),
                             ' VALUES less than (to_days("', DATE_ADD(@minDate, INTERVAL 1 MONTH), '")),');
            SET @minDate = DATE_ADD(@minDate, INTERVAL 1 MONTH);
        END WHILE;

    SET @s2 = CONCAT(@s2, ' PARTITION ', tableName, '_', DATE_FORMAT(@minDate, '%Y%m'), ' VALUES less than (to_days("',
                     DATE_ADD(@minDate, INTERVAL 1 MONTH), '")));');
    PREPARE stmt2 FROM @s2;
    EXECUTE stmt2;
    DEALLOCATE PREPARE stmt2;
END
$$

/*
 *初始化分区
 */
ALTER TABLE `base_account_logs` DROP PRIMARY KEY;
ALTER TABLE `base_account_logs` ADD PRIMARY KEY(`id`, `login_time`);
CALL partitionInit('base_account_logs', 'login_time');

ALTER TABLE `gateway_access_logs` DROP PRIMARY KEY;
ALTER TABLE `gateway_access_logs` ADD PRIMARY KEY(`access_id`, `request_time`);
CALL partitionInit('gateway_access_logs', 'request_time');

ALTER TABLE `msg_webhook_logs` DROP PRIMARY KEY;
ALTER TABLE `msg_webhook_logs` ADD PRIMARY KEY(`msg_id`, `create_time`);
CALL partitionInit('msg_webhook_logs', 'create_time');

ALTER TABLE `scheduler_job_logs` DROP PRIMARY KEY;
ALTER TABLE `scheduler_job_logs` ADD PRIMARY KEY(`log_id`, `create_time`);
CALL partitionInit('scheduler_job_logs', 'create_time');

/*
 *MariaDB创建索引
 */
CREATE INDEX IF NOT EXISTS `login_name` ON `base_account_logs` (`login_time`);
CREATE INDEX IF NOT EXISTS `request_time` ON `gateway_access_logs` (`request_time`);
CREATE INDEX IF NOT EXISTS `create_time` ON `msg_webhook_logs` (`create_time`);
CREATE INDEX IF NOT EXISTS `create_time` ON `scheduler_job_logs` (`create_time`);

/*
 *MySQL创建索引，仅限1次
 */
CREATE INDEX `login_name` ON `base_account_logs` (`login_time`);
CREATE INDEX `request_time` ON `gateway_access_logs` (`request_time`);
CREATE INDEX `create_time` ON `msg_webhook_logs` (`create_time`);
CREATE INDEX `create_time` ON `scheduler_job_logs` (`create_time`);

/*
 *添加下月表分区 前提：表已经分区过
 @tableName 表名
 @columnName 时间字段列名
*/
DROP PROCEDURE IF EXISTS partitionAdd;
DELIMITER $$
CREATE PROCEDURE partitionAdd(IN tableName VARCHAR(50), IN columnName VARCHAR(50))
    COMMENT '每月按时添加表分区的存储过程，由定时任务调用'
BEGIN
    SET @endDate = DATE_ADD(DATE(NOW()) - DAY(NOW()) + 1, INTERVAL 2 MONTH);
    SET @s = CONCAT('alter table ', tableName, ' add partition (partition ', tableName, '_',
                    DATE_FORMAT(DATE_ADD(DATE(NOW()) - DAY(NOW()) + 1, INTERVAL 1 MONTH), '%Y%m'),
                    ' VALUES less than (to_days("', @endDate, '")));');
    PREPARE stmt FROM @s;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END
$$

/*
 *定时任务调度
 */
CREATE EVENT autoPartitionAccountLogs
    ON SCHEDULE EVERY 1 MONTH
        STARTS CURRENT_TIMESTAMP
    ON COMPLETION PRESERVE
    ENABLE DO CALL partitionAdd('base_account_logs', 'login_time');
CREATE EVENT autoPartitionAccessLogs
    ON SCHEDULE EVERY 1 MONTH
        STARTS CURRENT_TIMESTAMP
    ON COMPLETION PRESERVE
    ENABLE DO CALL partitionAdd('gateway_access_logs', 'request_time');
CREATE EVENT autoPartitionWebhookLogs
    ON SCHEDULE EVERY 1 MONTH
        STARTS CURRENT_TIMESTAMP
    ON COMPLETION PRESERVE
    ENABLE DO CALL partitionAdd('msg_webhook_logs', 'create_time');
CREATE EVENT autoPartitionJobLogs
    ON SCHEDULE EVERY 1 MONTH
        STARTS CURRENT_TIMESTAMP
    ON COMPLETION PRESERVE
    ENABLE DO CALL partitionAdd('scheduler_job_logs', 'create_time');

SET GLOBAL event_scheduler = 1;