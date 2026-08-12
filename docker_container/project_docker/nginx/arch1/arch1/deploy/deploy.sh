#!/bin/bash

# Arch1 Web Application Deployment Script
# This script deploys the application to Tomcat and configures Nginx

set -e  # Exit on any error

# Configuration
TOMCAT_HOME="/opt/tomcat"
TOMCAT_WEBAPPS="$TOMCAT_HOME/webapps"
NGINX_CONF_DIR="/etc/nginx"
NGINX_SITES_AVAILABLE="$NGINX_CONF_DIR/sites-available"
NGINX_SITES_ENABLED="$NGINX_CONF_DIR/sites-enabled"
APP_NAME="arch1-webapp"
WAR_FILE="arch1-webapp-0.0.1-SNAPSHOT-plain.war"
FRONTEND_DIR="/var/www/arch1/frontend"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running as root
check_root() {
    if [[ $EUID -ne 0 ]]; then
        print_error "This script must be run as root"
        exit 1
    fi
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check if Tomcat is installed
    if [[ ! -d "$TOMCAT_HOME" ]]; then
        print_error "Tomcat not found at $TOMCAT_HOME"
        print_error "Please install Tomcat first"
        exit 1
    fi
    
    # Check if Nginx is installed
    if ! command -v nginx &> /dev/null; then
        print_error "Nginx not found. Please install Nginx first"
        exit 1
    fi
    
    # Check if WAR file exists
    if [[ ! -f "backend/build/libs/$WAR_FILE" ]]; then
        print_error "WAR file not found: backend/build/libs/$WAR_FILE"
        print_error "Please build the application first: ./gradlew war"
        exit 1
    fi
    
    print_status "Prerequisites check passed"
}

# Stop Tomcat
stop_tomcat() {
    print_status "Stopping Tomcat..."
    if systemctl is-active --quiet tomcat; then
        systemctl stop tomcat
        sleep 5
    fi
}

# Deploy WAR file to Tomcat
deploy_war() {
    print_status "Deploying WAR file to Tomcat..."
    
    # Remove existing deployment
    if [[ -d "$TOMCAT_WEBAPPS/$APP_NAME" ]]; then
        print_status "Removing existing deployment..."
        rm -rf "$TOMCAT_WEBAPPS/$APP_NAME"
    fi
    
    # Copy WAR file
    print_status "Copying WAR file..."
    cp "backend/build/libs/$WAR_FILE" "$TOMCAT_WEBAPPS/"
    
    # Set permissions
    chown tomcat:tomcat "$TOMCAT_WEBAPPS/$WAR_FILE"
    chmod 644 "$TOMCAT_WEBAPPS/$WAR_FILE"
    
    print_status "WAR file deployed successfully"
}

# Configure Tomcat
configure_tomcat() {
    print_status "Configuring Tomcat..."
    
    # Copy configuration files
    if [[ -f "tomcat/server.xml" ]]; then
        cp tomcat/server.xml "$TOMCAT_HOME/conf/"
        chown tomcat:tomcat "$TOMCAT_HOME/conf/server.xml"
        print_status "server.xml configured"
    fi
    
    if [[ -f "tomcat/context.xml" ]]; then
        cp tomcat/context.xml "$TOMCAT_HOME/conf/"
        chown tomcat:tomcat "$TOMCAT_HOME/conf/context.xml"
        print_status "context.xml configured"
    fi
    
    print_status "Tomcat configuration completed"
}

# Deploy frontend files
deploy_frontend() {
    print_status "Deploying frontend files..."
    
    # Create frontend directory
    mkdir -p "$FRONTEND_DIR"
    
    # Copy frontend files
    cp -r frontend/* "$FRONTEND_DIR/"
    
    # Set permissions
    chown -R www-data:www-data "$FRONTEND_DIR"
    chmod -R 755 "$FRONTEND_DIR"
    
    print_status "Frontend files deployed successfully"
}

# Configure Nginx
configure_nginx() {
    print_status "Configuring Nginx..."
    
    # Copy Nginx configuration
    if [[ -f "nginx/nginx.conf" ]]; then
        cp nginx/nginx.conf "$NGINX_CONF_DIR/"
        print_status "nginx.conf configured"
    fi
    
    # Copy site configuration
    if [[ -f "nginx/sites-available/arch1" ]]; then
        cp nginx/sites-available/arch1 "$NGINX_SITES_AVAILABLE/"
        
        # Enable site
        if [[ ! -L "$NGINX_SITES_ENABLED/arch1" ]]; then
            ln -s "$NGINX_SITES_AVAILABLE/arch1" "$NGINX_SITES_ENABLED/"
        fi
        
        print_status "Nginx site configuration completed"
    fi
    
    # Test Nginx configuration
    if nginx -t; then
        print_status "Nginx configuration test passed"
    else
        print_error "Nginx configuration test failed"
        exit 1
    fi
}

# Start services
start_services() {
    print_status "Starting services..."
    
    # Start Tomcat
    systemctl start tomcat
    sleep 10
    
    # Check Tomcat status
    if systemctl is-active --quiet tomcat; then
        print_status "Tomcat started successfully"
    else
        print_error "Failed to start Tomcat"
        exit 1
    fi
    
    # Reload Nginx
    systemctl reload nginx
    print_status "Nginx reloaded successfully"
}

# Wait for application to be ready
wait_for_application() {
    print_status "Waiting for application to be ready..."
    
    local max_attempts=30
    local attempt=1
    
    while [[ $attempt -le $max_attempts ]]; do
        if curl -s http://localhost:8080/arch1-webapp/health > /dev/null 2>&1; then
            print_status "Application is ready!"
            break
        fi
        
        print_status "Attempt $attempt/$max_attempts: Application not ready yet..."
        sleep 10
        ((attempt++))
    done
    
    if [[ $attempt -gt $max_attempts ]]; then
        print_warning "Application may not be fully ready. Please check manually."
    fi
}

# Show deployment summary
show_summary() {
    echo ""
    echo "=========================================="
    echo "           DEPLOYMENT SUMMARY"
    echo "=========================================="
    echo "Application: $APP_NAME"
    echo "Frontend URL: http://localhost/"
    echo "Backend URL: http://localhost:8080/arch1-webapp/"
    echo "Health Check: http://localhost:8080/arch1-webapp/health"
    echo ""
    echo "Tomcat Status: $(systemctl is-active tomcat)"
    echo "Nginx Status: $(systemctl is-active nginx)"
    echo ""
    echo "Logs:"
    echo "  Tomcat: $TOMCAT_HOME/logs/catalina.out"
    echo "  Nginx: /var/log/nginx/arch1_access.log"
    echo "=========================================="
}

# Main deployment function
main() {
    print_status "Starting Arch1 deployment..."
    
    check_root
    check_prerequisites
    stop_tomcat
    deploy_war
    configure_tomcat
    deploy_frontend
    configure_nginx
    start_services
    wait_for_application
    show_summary
    
    print_status "Deployment completed successfully!"
}

# Run main function
main "$@"
