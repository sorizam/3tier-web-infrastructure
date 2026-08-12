#!/bin/bash
# 간단한 오토스케일링 스크립트 (초심자용)

# web1의 node_exporter 주소 (VM IP에 맞게 수정)
TARGET="192.168.11.40:9100"

# CPU 임계값 (0.8 = 80%)
THRESHOLD=0.8

# web2의 상태를 확인해서 중복 실행 방지
STATUS=$(vagrant status web2 | grep "web2" | awk '{print $2}')

# 현재 CPU 사용률 가져오기
cpu_idle=$(curl -s http://$TARGET/metrics | grep "node_cpu_seconds_total{mode=\"idle\"}" | awk '{sum+=$2} END {print sum/NR}')
cpu_total=$(curl -s http://$TARGET/metrics | grep "node_cpu_seconds_total" | awk '{sum+=$2} END {print sum/NR}')
cpu_usage=$(echo "1 - ($cpu_idle / $cpu_total)" | bc -l)

echo "현재 CPU 사용률: $cpu_usage"

# CPU가 임계값 이상이면 web2 실행
if (( $(echo "$cpu_usage > $THRESHOLD" | bc -l) )); then
    if [[ "$STATUS" == "running" ]]; then
        echo "⚙️ 이미 web2가 실행 중입니다."
    else
        echo "🔥 CPU 부하 감지! web2를 실행합니다..."
        vagrant up web2
    fi
else
    echo "✅ 시스템 안정 — web2 실행 불필요."
fi
