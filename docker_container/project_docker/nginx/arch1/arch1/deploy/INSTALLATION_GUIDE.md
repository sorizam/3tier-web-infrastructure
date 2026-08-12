# Arch1 Web Application - Nginx + Tomcat 설치 가이드

## 개요

이 가이드는 Arch1 웹 애플리케이션을 Nginx와 Tomcat을 사용하여 배포하는 방법을 설명합니다.

## 아키텍처

```
Internet → Nginx (Port 80) → Tomcat (Port 8080) → MySQL (192.168.11.100:3306)
                ↓
            Static Files (HTML, CSS, JS)
```

## 1. 시스템 요구사항

### 하드웨어
- CPU: 2코어 이상
- RAM: 4GB 이상
- 디스크: 20GB 이상

### 소프트웨어
- Ubuntu 20.04 LTS 이상
- Java 17 이상
- MySQL 8.0 이상

## 2. Java 17 설치

```bash
# OpenJDK 17 설치
sudo apt update
sudo apt install openjdk-17-jdk

# Java 버전 확인
java -version
javac -version

# JAVA_HOME 설정
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## 3. Tomcat 10 설치

```bash
# Tomcat 10 다운로드
cd /opt
sudo wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.15/bin/apache-tomcat-10.1.15.tar.gz

# 압축 해제
sudo tar -xzf apache-tomcat-10.1.15.tar.gz
sudo ln -s apache-tomcat-10.1.15 tomcat

# 권한 설정
sudo chown -R tomcat:tomcat tomcat
sudo chmod +x tomcat/bin/*.sh

# Tomcat 사용자 생성
sudo useradd -r -s /bin/false tomcat
sudo chown -R tomcat:tomcat /opt/tomcat

# systemd 서비스 파일 생성
sudo tee /etc/systemd/system/tomcat.service > /dev/null <<EOF
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking
User=tomcat
Group=tomcat
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_BASE=/opt/tomcat"
ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

# 서비스 활성화 및 시작
sudo systemctl daemon-reload
sudo systemctl enable tomcat
sudo systemctl start tomcat

# 상태 확인
sudo systemctl status tomcat
```

## 4. Nginx 설치

```bash
# Nginx 설치
sudo apt update
sudo apt install nginx

# Nginx 시작 및 활성화
sudo systemctl start nginx
sudo systemctl enable nginx

# 상태 확인
sudo systemctl status nginx

# 방화벽 설정 (필요시)
sudo ufw allow 'Nginx Full'
```

## 5. MySQL 연결 확인

```bash
# MySQL 클라이언트 설치
sudo apt install mysql-client

# 연결 테스트
mysql -h 192.168.11.100 -u admin -p

# 데이터베이스 초기화
source /path/to/arch1/database/init.sql
```

## 6. 애플리케이션 배포

### 6.1 WAR 파일 빌드

```bash
cd /path/to/arch1/backend
./gradlew clean war
```

### 6.2 자동 배포 스크립트 실행

```bash
cd /path/to/arch1
chmod +x deploy/deploy.sh
sudo ./deploy/deploy.sh
```

### 6.3 수동 배포 (선택사항)

```bash
# WAR 파일 복사
sudo cp backend/build/libs/arch1-webapp-0.0.1-SNAPSHOT-plain.war /opt/tomcat/webapps/

# Tomcat 설정 파일 복사
sudo cp tomcat/server.xml /opt/tomcat/conf/
sudo cp tomcat/context.xml /opt/tomcat/conf/

# 프론트엔드 파일 복사
sudo mkdir -p /var/www/arch1/frontend
sudo cp -r frontend/* /var/www/arch1/frontend/

# Nginx 설정 복사
sudo cp nginx/nginx.conf /etc/nginx/
sudo cp nginx/sites-available/arch1 /etc/nginx/sites-available/
sudo ln -s /etc/nginx/sites-available/arch1 /etc/nginx/sites-enabled/

# 서비스 재시작
sudo systemctl restart tomcat
sudo systemctl reload nginx
```

## 7. 설정 파일 상세 설명

### 7.1 Nginx 설정

**주요 기능:**
- 정적 파일 서빙 (포트 80)
- Tomcat으로의 프록시 (/backend/ 경로)
- Gzip 압축
- 보안 헤더
- Rate limiting

**프록시 설정:**
```nginx
location /backend/ {
    rewrite ^/backend/(.*)$ /$1 break;
    proxy_pass http://127.0.0.1:8080;
    # ... 기타 설정
}
```

### 7.2 Tomcat 설정

**주요 설정:**
- 포트 8080에서 HTTP 서비스
- 포트 8009에서 AJP 서비스
- 데이터베이스 연결 풀
- JNDI 환경 변수

**데이터베이스 연결:**
```xml
<Resource name="jdbc/arch1db" 
          auth="Container" 
          type="javax.sql.DataSource"
          username="admin" 
          password="1qaz@WSX"
          driverClassName="com.mysql.cj.jdbc.Driver"
          url="jdbc:mysql://192.168.11.100:3306/arch1db?..."
          />
```

## 8. 접속 및 테스트

### 8.1 접속 URL

- **프론트엔드**: http://localhost/
- **백엔드**: http://localhost:8080/arch1-webapp/
- **건강 체크**: http://localhost:8080/arch1-webapp/health

### 8.2 로그 확인

```bash
# Tomcat 로그
sudo tail -f /opt/tomcat/logs/catalina.out

# Nginx 로그
sudo tail -f /var/log/nginx/arch1_access.log
sudo tail -f /var/log/nginx/arch1_error.log
```

### 8.3 서비스 상태 확인

```bash
# Tomcat 상태
sudo systemctl status tomcat

# Nginx 상태
sudo systemctl status nginx

# 포트 확인
sudo netstat -tlnp | grep -E ':(80|8080)'
```

## 9. 문제 해결

### 9.1 Tomcat 시작 실패

```bash
# 로그 확인
sudo tail -f /opt/tomcat/logs/catalina.out

# 권한 확인
sudo chown -R tomcat:tomcat /opt/tomcat

# Java 버전 확인
java -version
```

### 9.2 Nginx 설정 오류

```bash
# 설정 파일 문법 검사
sudo nginx -t

# 설정 파일 재로드
sudo systemctl reload nginx
```

### 9.3 데이터베이스 연결 실패

```bash
# MySQL 연결 테스트
mysql -h 192.168.11.100 -u admin -p

# 방화벽 확인
sudo ufw status
```

## 10. 보안 고려사항

### 10.1 방화벽 설정

```bash
# 필요한 포트만 열기
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS (선택사항)
sudo ufw enable
```

### 10.2 SSL/TLS 설정 (선택사항)

```bash
# Let's Encrypt 인증서 발급
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d yourdomain.com
```

## 11. 모니터링 및 유지보수

### 11.1 로그 로테이션

```bash
# logrotate 설정
sudo tee /etc/logrotate.d/arch1 > /dev/null <<EOF
/var/log/nginx/arch1_*.log {
    daily
    missingok
    rotate 52
    compress
    delaycompress
    notifempty
    create 644 www-data www-data
    postrotate
        systemctl reload nginx
    endscript
}
EOF
```

### 11.2 백업 스크립트

```bash
#!/bin/bash
# 백업 디렉토리 생성
BACKUP_DIR="/backup/arch1/$(date +%Y%m%d)"
mkdir -p $BACKUP_DIR

# WAR 파일 백업
cp /opt/tomcat/webapps/arch1-webapp-0.0.1-SNAPSHOT-plain.war $BACKUP_DIR/

# 설정 파일 백업
cp -r /opt/tomcat/conf $BACKUP_DIR/tomcat_conf
cp -r /etc/nginx $BACKUP_DIR/nginx_conf

# 프론트엔드 백업
cp -r /var/www/arch1 $BACKUP_DIR/frontend

echo "Backup completed: $BACKUP_DIR"
```

## 12. 성능 튜닝

### 12.1 JVM 튜닝

```bash
# Tomcat JVM 옵션 설정
export CATALINA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"
```

### 12.2 Nginx 튜닝

```nginx
# worker_processes 설정
worker_processes auto;

# worker_connections 설정
events {
    worker_connections 1024;
}
```

이 가이드를 따라하면 Arch1 웹 애플리케이션을 Nginx와 Tomcat을 사용하여 성공적으로 배포할 수 있습니다.
