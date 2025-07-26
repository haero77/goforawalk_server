# for AMI: al2023-ami-2023.8.20250707.0-kernel-6.1-x86_64(Amazon Linux 2023)

# 시스템 패키지 업데이트
sudo dnf update -y

# Docker 설치
sudo dnf install -y docker

# Docker 서비스 시작 및 부팅 시 자동 시작 설정
sudo systemctl start docker
sudo systemctl enable docker

# Docker 버전 확인
docker --version

sudo docker run hello-world

# 현재 사용자를 docker 그룹에 추가
sudo usermod -a -G docker $USER

# 새로운 셸 세션 시작 (또는 로그아웃 후 재로그인)
newgrp docker

# 권한 확인
docker ps

# Docker Compose 최신 버전 다운로드
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 실행 권한 부여
sudo chmod +x /usr/local/bin/docker-compose

# 버전 확인
docker-compose --version