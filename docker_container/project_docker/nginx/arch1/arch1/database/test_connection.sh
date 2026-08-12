#!/bin/bash

# MySQL Connection Test Script for Arch1
# This script tests the connection to the MySQL database

echo "=== Arch1 MySQL Connection Test ==="
echo "Host: 192.168.11.100"
echo "Database: arch1db"
echo "Username: admin"
echo ""

# Test MySQL connection
mysql -h 192.168.11.100 -u admin -p'1qaz@WSX' -e "SELECT VERSION();" 2>/dev/null

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ MySQL connection successful!"
    echo ""
    echo "Testing database access..."
    mysql -h 192.168.11.100 -u admin -p'1qaz@WSX' -e "USE arch1db; SHOW TABLES;" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Database access successful!"
        echo ""
        echo "Current tables in arch1db:"
        mysql -h 192.168.11.100 -u admin -p'1qaz@WSX' -e "USE arch1db; SHOW TABLES;" 2>/dev/null
    else
        echo ""
        echo "❌ Database access failed. Database 'arch1db' might not exist."
        echo "Please run the init.sql script first."
    fi
else
    echo ""
    echo "❌ MySQL connection failed!"
    echo "Please check:"
    echo "1. MySQL server is running on 192.168.11.100"
    echo "2. Port 3306 is accessible"
    echo "3. Username and password are correct"
    echo "4. Network connectivity to 192.168.11.100"
fi

echo ""
echo "=== Connection Test Complete ==="
