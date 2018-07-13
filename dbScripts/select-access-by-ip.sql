SELECT * FROM (SELECT ip_address, count(ip_address) access_count FROM access_entry
WHERE date_time > '2017-01-01 00:00:00'
  AND date_time < '2017-01-01 01:00:00'
GROUP BY (ip_address)) ip_counts
WHERE access_count > 100;