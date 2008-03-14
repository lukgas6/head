DROP TABLE IF EXISTS `time_dim`;
CREATE TABLE  `time_dim` (
  `id` smallint(4) AUTO_INCREMENT NOT NULL,
  `date` date NOT NULL,
  `day_num_in_week` smallint(2) NOT NULL,
  `week_num_in_year` smallint(2) NOT NULL,
  `week_num_overall` smallint(4) NOT NULL,
  `day_of_week` varchar(50) NOT NULL,
  `day_num_in_month` smallint(2) NOT NULL,
  `day_num_overall` smallint(4) NOT NULL,
  `month` varchar(50) NOT NULL,
  `month_num_in_year` smallint(4) NOT NULL,
  `month_num_overall` smallint(4) NOT NULL,
  `quarter_num_year` smallint(4) NOT NULL,
  `quarter_name` varchar(50) NOT NULL,
  `year` year(4) NOT NULL,
  `week_day_flag` bit(1) NOT NULL,
  `last_day_in_month_flag` bit(1) NOT NULL,
  `holiday_flag` bit(1) NOT NULL,
  `holiday_name` varchar(200) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
