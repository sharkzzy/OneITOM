CREATE TABLE `t_order0` (
  `order_id` BIGINT(50) COMMENT '�������',
  `user_id` BIGINT(50) COMMENT '�û����',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '����״̬',
  PRIMARY KEY (`order_id`)
);
CREATE TABLE `t_order1` (
  `order_id` BIGINT(50) COMMENT '�������',
  `user_id` BIGINT(50) COMMENT '�û����',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '����״̬',
  PRIMARY KEY (`order_id`)
);
CREATE TABLE `t_order_item0` (
  `order_item_id` BIGINT(50) COMMENT '�������',
  `user_id` BIGINT(50) COMMENT '�û����',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '����״̬',
  PRIMARY KEY (`order_item_id`)
);
CREATE TABLE `t_order_item1` (
  `order_item_id` BIGINT(50) COMMENT '�������',
  `user_id` BIGINT(50) COMMENT '�û����',
  `status` VARCHAR(32) DEFAULT NULL COMMENT '����״̬',
  PRIMARY KEY (`order_item_id`)
);