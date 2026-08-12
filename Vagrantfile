# -*- mode: ruby -*-
# vi: set ft=ruby :

# RDP
# Windows Client VM
Vagrant.configure("2") do |config|
 config.vm.define "clientys" do |clientys|
  clientys.vm.box = "stromweld/windows-11"
  clientys.vm.hostname = "clientys"
  clientys.vm.provider "vmware_desktop" do |vb|
     vb.cpus = 2
     vb.memory = 4096
   end
  #  clientys.vm.network "public_network", bridge: "VMnet0", ip: "200.23.45.50" # 외부 접속용
  clientys.vm.network "private_network", ip: "10.10.10.10"                # 내부망용   clientys.vm.network "forwarded_port", guest: "3389", host: "33089", auto_correct: true # auto_correct 자동으로 포트 충돌 방지
  clientys.vm.network "forwarded_port", guest: 3389, host: 33089, host_ip: "10.10.10.10", auto_correct: true
  #  clientys.vm.network "forwarded_port", guest: "3389", host: "33089", auto_correct: true # auto_correct 자동으로 포트 충돌 방지
  clientys.vm.provision "shell", inline: <<-SCRIPT
   SCRIPT
end


  config.vm.define "db_master" do |db_master|
    db_master.vm.box = "bento/ubuntu-22.04"
    db_master.vm.hostname = "db-master"
    db_master.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    db_master.vm.network "private_network", ip: "192.168.11.100"
    db_master.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y mysql-server

    sudo systemctl start mysql
    sudo systemctl enable mysql

    mysql
    SCRIPT
  end

  config.vm.define "db_replica" do |db_replica|
    db_replica.vm.box = "bento/ubuntu-22.04"
    db_replica.vm.hostname = "db-replica"
    db_replica.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    db_replica.vm.network "private_network", ip: "192.168.11.101"
    db_replica.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y mysql-server

    sudo systemctl start mysql
    sudo systemctl enable mysql
    SCRIPT
  end

  config.vm.define "db_lb" do |db_lb|
    db_lb.vm.box = "bento/ubuntu-22.04"
    db_lb.vm.hostname = "db-lb"
    db_lb.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    db_lb.vm.network "private_network", ip: "192.168.11.109"
    db_lb.vm.provision "shell", inline: <<-SCRIPT

      # 기본 패키지
      sudo apt-get update -y
      sudo apt-get install -y mysql-client lsb-release wget apt-transport-https ca-certificates gnupg

      # ProxySQL 레포지토리 추가
      sudo wget -nv -O /usr/share/keyrings/proxysql-3.0.x-keyring.gpg \
        https://repo.proxysql.com/ProxySQL/proxysql-3.0.x/repo_pub_key.gpg

      echo "deb [signed-by=/usr/share/keyrings/proxysql-3.0.x-keyring.gpg] \
        https://repo.proxysql.com/ProxySQL/proxysql-3.0.x/$(lsb_release -sc)/ ./" \
        | sudo tee /etc/apt/sources.list.d/proxysql.list

      sudo apt-get update -y
      sudo apt-get install -y proxysql

      # ProxySQL 서비스 활성화
      sudo systemctl enable --now proxysql

    SCRIPT
  end

  config.vm.define "web1" do |web1|
    web1.vm.box = "bento/ubuntu-22.04"
    web1.vm.hostname = "web1"
    web1.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    web1.vm.network "private_network", ip: "192.168.11.20"
    web1.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y nginx nfs-common

    sudo systemctl start nginx
    sudo systemctl enable nginx

    SCRIPT
  end

  config.vm.define "web2" do |web2|
    web2.vm.box = "bento/ubuntu-22.04"
    web2.vm.hostname = "web2"
    web2.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    web2.vm.network "private_network", ip: "192.168.11.21"
    web2.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y apache2 nfs-common

    sudo systemctl start apache2
    sudo systemctl enable apache2

    SCRIPT
  end


  #-----Web Load Balancer
  config.vm.define "web_lb" do |web_lb|
    web_lb.vm.box = "bento/ubuntu-22.04"
    web_lb.vm.hostname = "web-lb"
    web_lb.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    web_lb.vm.network "private_network", ip: "192.168.11.29"
    # web_lb.vm.network "forwarded_port", guest: 22, host: 2205, auto_correct: true
    web_lb.vm.network "forwarded_port", guest: 8080, host: 8090, auto_correct: true
    web_lb.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y haproxy

    sudo systemctl start haproxy
    sudo systemctl enable haproxy

    SCRIPT
  end

  config.vm.define "app1" do |app1|
    app1.vm.box = "bento/ubuntu-22.04"
    app1.vm.hostname = "app1"
    app1.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    app1.vm.network "private_network", ip: "192.168.11.30"
    app1.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y python3-venv mysql-client python3-dev \
    default-libmysqlclient-dev pkg-config libmysqlclient-dev \
    build-essential nfs-common


    SCRIPT
  end

  config.vm.define "app2" do |app2|
    app2.vm.box = "bento/ubuntu-22.04"
    app2.vm.hostname = "app2"
    app2.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    app2.vm.network "private_network", ip: "192.168.11.31"
    app2.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y python3-venv mysql-client python3-dev \
    default-libmysqlclient-dev pkg-config libmysqlclient-dev \
    build-essential nfs-common


    SCRIPT
  end

  config.vm.define "app_lb" do |app_lb|
    app_lb.vm.box = "bento/ubuntu-22.04"
    app_lb.vm.hostname = "app-lb"
    app_lb.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    app_lb.vm.network "private_network", ip: "192.168.11.39"
    app_lb.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y haproxy

    sudo systemctl start haproxy
    sudo systemctl enable haproxy

    SCRIPT
  end

  config.vm.define "nfs" do |nfs|
    nfs.vm.box = "bento/ubuntu-22.04"
    nfs.vm.hostname = "nfs"
    nfs.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    nfs.vm.network "private_network", ip: "192.168.11.200"
    nfs.vm.provision "shell", inline: <<-SCRIPT

    sudo apt-get update -y
    sudo apt-get install -y nfs-kernel-server

    sudo systemctl start nfs-kernel-server
    sudo systemctl enable nfs-kernel-server

    sudo apt-get install -y python3-venv mysql-client python3-dev \
    default-libmysqlclient-dev pkg-config libmysqlclient-dev \
    build-essential nfs-common

    SCRIPT
  end

  config.vm.define "prometheus" do |prometheus|
    prometheus.vm.box = "bento/ubuntu-22.04"
    prometheus.vm.hostname = "prometheus"
    prometheus.vm.network "private_network", ip: "192.168.11.40"
    
    prometheus.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end

    prometheus.vm.provision "shell", inline: <<-SHELL
      apt-get update -y
      apt-get install -y wget tar
      cd /opt
      wget https://github.com/prometheus/prometheus/releases/download/v3.7.1/prometheus-3.7.1.linux-amd64.tar.gz
      tar -xvzf prometheus-3.7.1.linux-amd64.tar.gz
      mv prometheus-3.7.1.linux-amd64 prometheus
    SHELL
  end
  
  config.vm.define "k6" do |k6|
    k6.vm.box = "bento/ubuntu-22.04"
    k6.vm.hostname = "k6"
    k6.vm.network "private_network", ip: "192.168.11.50"

    k6.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 1024
    end
    

    k6.vm.provision "shell", inline: <<-SHELL
      sudo apt-get update -y

      # 안정된 버전 명시적 다운로드 (예: v0.45.1)
      sudo wget https://github.com/grafana/k6/releases/download/v0.45.1/k6-v0.45.1-linux-amd64.tar.gz
      
      # 압축 해제
      sudo tar -xvzf k6-v0.45.1-linux-amd64.tar.gz

      # 바이너리 옮기기
      sudo cp k6-v0.45.1-linux-amd64/k6 /usr/local/bin/

      # 확인
      sudo k6 version
    SHELL
  end
  
  config.vm.define "projectcontainer" do |projectcontainer|
    projectcontainer.vm.box = "bento/ubuntu-22.04"
    projectcontainer.vm.hostname = "projectcontainer"
    projectcontainer.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2
      vb.memory = 4096
    end
    projectcontainer.vm.network "private_network", ip: "192.168.11.150"
    projectcontainer.vm.provision "shell", inline: <<-SCRIPT
      # Repository update
      sudo apt-get update -y

      # Remove old packages
      for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc; do sudo apt-get remove $pkg; done
      
      # Add Docker's official GPG key:
      sudo apt-get update -y
      sudo apt-get install ca-certificates curl -y
      sudo install -m 0755 -d /etc/apt/keyrings
      sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
      sudo chmod a+r /etc/apt/keyrings/docker.asc

      # Add the repository to Apt sources:
      echo \
        "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
        $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
        sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
      sudo apt-get update -y

      # Install Docker
      sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y

      echo ”rQSrTmj1QYaBfsDH8RMrPFoiZEY” | docker login --username ys111299 --password-stdin

    SCRIPT
  end

  #dns-server
  config.vm.define "dns_server" do |dns_server| #dns_server 이라는 이름의 가상머신 생성
    dns_server.vm.box = "bento/ubuntu-22.04" #ubuntu-22.04 버전의 박스를 사용
    dns_server.vm.hostname = "dns" #dns 이라는 이름의 가상머신 생성
   
   
    dns_server.vm.provider "vmware_desktop" do |vb|
      vb.cpus = 2 #2개의 코어를 사용
      vb.memory = 2048 #2GB의 메모리를 사용
    end
    dns_server.vm.network "private_network", ip: "192.168.11.250" #192.168.11.250 주소를 사용
   
    #머신이 초기에 생성될 때 실행됩니다. ->AWS User Data 와 비슷한 개념
    dns_server.vm.provision "shell", inline: <<-SCRIPT
    sudo apt-get update -y
    sudo apt-get install bind9 bind9utils bind9-doc -y

    # ubuntu22.04에서 bind9.service는 별칭이고 실제 동작하는 서비스 파일은 named.service
    sudo systemctl start named
    sudo systemctl enable named

    SCRIPT
  end




end