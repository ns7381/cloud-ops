DROP TABLE IF EXISTS `application_env_meta`;
CREATE TABLE `application_env_meta` (
  `id` varchar(36) NOT NULL DEFAULT '',
  `name` varchar(36) DEFAULT NULL,
  `value` varchar(256) DEFAULT NULL,
  `env_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

