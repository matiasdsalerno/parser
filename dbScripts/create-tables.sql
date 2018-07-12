create table access_entry
(
  date_time datetime not null
    primary key,
  ip_address varchar(45) null,
  request varchar(64) null
)
;

create table blocked_ip_address
(
  ip_address varchar(45) not null
    primary key,
  message varchar(100) null
)
;

