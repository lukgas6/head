drop table if exists `head_office_dim`;
create table `head_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `office_level_id` smallint(6) not null,
  `search_id` varchar(100) not null,
  `max_child_count` int(11) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `status_id` smallint(6) not null,
  `version_no` int(11) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `global_office_num` (`global_office_num`),
  unique key `head_office_global_idx` (`global_office_num`)
--  key `status_id` (`status_id`),
--  key `office_code_id` (`office_code_id`),
--  constraint `office_ibfk_2` foreign key (`parent_office_id`) references `office` (`office_id`) on delete no action on update no action,
--  constraint `office_ibfk_3` foreign key (`status_id`) references `office_status` (`status_id`) on delete no action on update no action,
--  constraint `office_ibfk_4` foreign key (`office_code_id`) references `office_code` (`code_id`) on delete no action on update no action
) engine=innodb auto_increment=4 default charset=utf8;

drop table if exists `regional_office_dim`;
create table `regional_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `office_level_id` smallint(6) not null,
  `search_id` varchar(100) not null,
  `max_child_count` int(11) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `head_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `version_no` int(11) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `global_office_num` (`global_office_num`),
  unique key `_regional_office_global_idx` (`global_office_num`),
  key `head_office_id` (`head_office_id`),
--  key `status_id` (`status_id`),
--  key `office_code_id` (`office_code_id`),
  constraint `regional_office_ibfk_2` foreign key (`head_office_id`) references `head_office_dim` (`office_id`) on delete no action on update no action
--  constraint `office_ibfk_3` foreign key (`status_id`) references `office_status` (`status_id`) on delete no action on update no action,
--  constraint `office_ibfk_4` foreign key (`office_code_id`) references `office_code` (`code_id`) on delete no action on update no action
) engine=innodb auto_increment=4 default charset=utf8;


drop table if exists `divisional_office_dim`;
create table  `divisional_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `office_level_id` smallint(6) not null,
  `search_id` varchar(100) not null,
  `max_child_count` int(11) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `regional_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `version_no` int(11) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `global_office_num` (`global_office_num`),
  unique key `divisional_office_global_idx` (`global_office_num`),
  key `regional_office_id` (`regional_office_id`),
--  key `status_id` (`status_id`),
--  key `office_code_id` (`office_code_id`),
  constraint `divisional_office_ibfk_2` foreign key (`regional_office_id`) references `regional_office_dim` (`office_id`) on delete no action on update no action
--  constraint `office_ibfk_3` foreign key (`status_id`) references `office_status` (`status_id`) on delete no action on update no action,
--  constraint `office_ibfk_4` foreign key (`office_code_id`) references `office_code` (`code_id`) on delete no action on update no action
) engine=innodb auto_increment=4 default charset=utf8;


drop table if exists `area_office_dim`;
create table  `area_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `office_level_id` smallint(6) not null,
  `search_id` varchar(100) not null,
  `max_child_count` int(11) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `divisional_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `version_no` int(11) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `global_office_num` (`global_office_num`),
  unique key `area_office_global_idx` (`global_office_num`),
  key `divisional_office_id` (`divisional_office_id`),
--  key `status_id` (`status_id`),
--  key `office_code_id` (`office_code_id`),
  constraint `area_office_ibfk_2` foreign key (`divisional_office_id`) references `divisional_office_dim` (`office_id`) on delete no action on update no action
--  constraint `office_ibfk_3` foreign key (`status_id`) references `office_status` (`status_id`) on delete no action on update no action,
--  constraint `office_ibfk_4` foreign key (`office_code_id`) references `office_code` (`code_id`) on delete no action on update no action
) engine=innodb auto_increment=4 default charset=utf8;


drop table if exists `branch_office_dim`;
create table  `branch_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `office_level_id` smallint(6) not null,
  `search_id` varchar(100) not null,
  `max_child_count` int(11) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `area_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `version_no` int(11) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `global_office_num` (`global_office_num`),
  unique key `branch_office_global_idx` (`global_office_num`),
  key `area_office_id` (`area_office_id`),
--  key `status_id` (`status_id`),
--  key `office_code_id` (`office_code_id`),
  constraint `branch_office_ibfk_2` foreign key (`area_office_id`) references `area_office_dim` (`office_id`) on delete no action on update no action
--  constraint `office_ibfk_3` foreign key (`status_id`) references `office_status` (`status_id`) on delete no action on update no action,
--  constraint `office_ibfk_4` foreign key (`office_code_id`) references `office_code` (`code_id`) on delete no action on update no action
) engine=innodb auto_increment=4 default charset=utf8;


drop table if exists `center_office_dim`;
create table  `center_office_dim` (
  `office_id` smallint(6) not null auto_increment,
  `global_office_num` varchar(100) not null,
  `office_level_id` smallint(6) not null,
  `search_id` varchar(100) not null,
  `max_child_count` int(11) not null,
  `local_remote_flag` smallint(6) not null,
  `display_name` varchar(200) not null,
  `created_by` smallint(6) default null,
  `created_date` date default null,
  `updated_by` smallint(6) default null,
  `updated_date` date default null,
  `office_short_name` varchar(4) not null,
  `branch_office_id` smallint(6) default null,
  `status_id` smallint(6) not null,
  `version_no` int(11) not null,
  `office_code_id` smallint(6) default null,
  primary key  (`office_id`),
  unique key `global_office_num` (`global_office_num`),
  unique key `center_office_global_idx` (`global_office_num`),
  key `branch_office_id` (`branch_office_id`),
--  key `status_id` (`status_id`),
--  key `office_code_id` (`office_code_id`),
  constraint `center_office_ibfk_2` foreign key (`branch_office_id`) references `branch_office_dim` (`office_id`) on delete no action on update no action
--  constraint `office_ibfk_3` foreign key (`status_id`) references `office_status` (`status_id`) on delete no action on update no action,
--  constraint `office_ibfk_4` foreign key (`office_code_id`) references `office_code` (`code_id`) on delete no action on update no action
) engine=innodb auto_increment=4 default charset=utf8;

