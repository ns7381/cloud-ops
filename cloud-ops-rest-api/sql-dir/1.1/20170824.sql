CREATE TABLE `resource_package_file` (
  `id` varchar(36) NOT NULL DEFAULT '',
  `name` varchar(36) DEFAULT NULL,
  `path` varchar(256) DEFAULT NULL,
  `application_id` varchar(36) DEFAULT NULL,
  `type` varchar(36) DEFAULT NULL,
  `seed` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `location` (
  `id` varchar(36) NOT NULL DEFAULT '',
  `name` varchar(36) DEFAULT NULL,
  `type` varchar(36) DEFAULT NULL,
  `yaml_file_path` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE resource_package
  ADD build VARCHAR(256) NULL;
ALTER TABLE resource_package
  ADD branch VARCHAR(36) NULL;
ALTER TABLE work_flow
  ADD package_id VARCHAR(36) NULL;
ALTER TABLE work_flow
  ADD node_name VARCHAR(256) NULL;
ALTER TABLE work_flow_step
  ADD `index` int(11) NULL;
