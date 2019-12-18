CREATE TABLE `t_order0` (
  `order_id` BIGINT(50) COMMENT '¶©µ¥±àºÅ',
  `user_id` BIGINT(50) COMMENT 'ÓÃ»§±àºÅ',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '¶©µ¥×´Ì¬',
  PRIMARY KEY (`order_id`)
);
CREATE TABLE `t_order1` (
  `order_id` BIGINT(50) COMMENT '¶©µ¥±àºÅ',
  `user_id` BIGINT(50) COMMENT 'ÓÃ»§±àºÅ',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '¶©µ¥×´Ì¬',
  PRIMARY KEY (`order_id`)
);
CREATE TABLE `t_order_item0` (
  `order_item_id` BIGINT(50) COMMENT '¶©µ¥±àºÅ',
  `user_id` BIGINT(50) COMMENT 'ÓÃ»§±àºÅ',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '¶©µ¥×´Ì¬',
  PRIMARY KEY (`order_item_id`)
);
CREATE TABLE `t_order_item1` (
  `order_item_id` BIGINT(50) COMMENT '¶©µ¥±àºÅ',
  `user_id` BIGINT(50) COMMENT 'ÓÃ»§±àºÅ',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '¶©µ¥×´Ì¬',
  PRIMARY KEY (`order_item_id`)
);