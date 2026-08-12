import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    //vus: 10000,          // 가상 사용자 수 
    //duration: '30s',  // 총 테스트 시간
    stages: [				
	{ duration: '30s', target: 200}, // 30초 동안 200명까지
        { duration: '30s', target: 500}, // 30초 동안 700명까지
        { duration: '30s', target: 800}, // 30초 동안 800명까지
        { duration: '30s', target: 1000 }, // 30초 동안 1000명까지 (최대 부하)
        { duration: '30s', target: 0 }, // 30초 동안 종료
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],  // 95% 요청은 500ms 이하
        'http_req_failed': ['rate<0.01'],  // 실패율 1% 미만 유지
    },
};

export default function () {
    let res = http.get('http://192.168.11.21/');   // 트래픽 발생 위치: web_lb IP
    check(res, { 'status was 200': (r) => r.status === 200 });	//요청 성공여부 확인, 실패율이 높을 경우 프로메테우스/그라파나에서 알림 설정 가능
    sleep(0.5);	//vu가 요청 후 1초 대기 후 다음 요청 수행
}
// 테스트 종료 후 결과 요약 처리
export function handleSummary(data) {
    console.log(JSON.stringify(data, null, 2)); // 데이터 구조 확인용

	// Slack Webhook URL
    //const webhookUrl = process.env.SLACK_WEBHOOK_URL;
    const WEBHOOK_URL = __ENV.SLACK_WEBHOOK_URL;

    //  안전하게 metric 값 가져오기 (undefined 방지)
	const totalReq = 
    data.metrics && data.metrics.http_reqs && data.metrics.http_reqs.values
        ? data.metrics.http_reqs.values.count
        : 0;


	const failRate = 
    data.metrics && data.metrics.http_req_failed && data.metrics.http_req_failed.values
        ? (data.metrics.http_req_failed.values.rate * 100).toFixed(2)
        : '0.00';

	const p95 = (data.metrics && data.metrics.http_req_duration && data.metrics.http_req_duration.values && data.metrics.http_req_duration.values['p(95)'])
    ? data.metrics.http_req_duration.values['p(95)']
    : 0;


    // 알람 메시지 작성
	const message = {
    text: `k6 테스트 종료!
	총 요청: ${totalReq}
	실패율: ${failRate}%
	95% 응답시간: ${p95} ms`
	};


    // Slack으로 POST 요청
    http.post(webhookUrl, JSON.stringify(message), { headers: { 'Content-Type': 'application/json' } });

    return {
        stdout: JSON.stringify(data), // 콘솔 출력도 유지
    };
}
