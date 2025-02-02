#!/bin/bash

# Define variables
table_name="usage_event"
table_name_aggregator="usage_event_aggregator"
table_name_job_status="job_status"

column_family="cf"

# HBase shell commands to create the table and insert data
create_table() {
  echo "Creating HBase table..."
  echo "create '$table_name', '$column_family'" | hbase shell
  echo "create '$table_name_aggregator', '$column_family'" | hbase shell
  echo "create '$table_name_job_status', '$column_family'" | hbase shell
}

insert_dummy_data() {
  echo "Inserting dummy data into HBase table..."
  echo "put '$table_name', 'row1', '$column_family:id', '1'" | hbase shell
  echo "put '$table_name', 'row1', '$column_family:user_id', 'user-1'" | hbase shell
  echo "put '$table_name', 'row1', '$column_family:date', '2025-01-01'" | hbase shell
  echo "put '$table_name', 'row1', '$column_family:amount', '1'" | hbase shell

  echo "put '$table_name', 'row2', '$column_family:id', '2'" | hbase shell
  echo "put '$table_name', 'row2', '$column_family:user_id', 'user-2'" | hbase shell
  echo "put '$table_name', 'row2', '$column_family:date', '2025-01-02'" | hbase shell
  echo "put '$table_name', 'row2', '$column_family:amount', '1'" | hbase shell
}

check_hbase_running() {
  echo "Checking if HBase is running..."
  ps aux | grep -q "org.apache.hadoop.hbase.master.HMaster" && \
  ps aux | grep -q "org.apache.hadoop.hbase.rest.RESTServer" && \
  ps aux | grep -q "org.apache.hadoop.hbase.thrift.ThriftServer"
}

check_table_exists() {
  echo "Checking if table exists..."
  echo "list" | hbase shell | grep "$table_name"
}

# Main script logic
check_hbase_running

if check_table_exists; then
  echo "Table $table_name already exists. Skipping creation."
else
  create_table
fi

insert_dummy_data

echo "Dummy data insertion completed."