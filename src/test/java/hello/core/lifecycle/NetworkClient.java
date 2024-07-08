package hello.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetworkClient {

    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
        connect();
        call("초기화 연결 메시지");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 서비스 시작 시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void call(String message) {
        System.out.println("call: " + url + " message = " + message);
    }

    // 서비스 종료 시 호출
    public void disconnect() {
        System.out.println("close: " + url);
    }

    // 1. 초기화 & 소멸 콜백 메서드 재정의 방식 -> 이젠 잘 사용 안한다.
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("NetworkClient.afterPropertiesSet");
//        connect();
//        call("초기화 연결 메시지");
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        System.out.println("NetworkClient.destroy");
//        disconnect();
//    }

    // 2. 설정 정보 사용 방식 -> 메서드 이름을 자유롭게, 스프링 빈이 스프링 코드에 의존 X, 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 있다.
//    public void init() {
//        System.out.println("NetworkClient.afterPropertiesSet");
//        connect();
//        call("초기화 연결 메시지");
//    }
//
//    public void close() {
//        System.out.println("NetworkClient.destroy");
//        disconnect();
//    }

    // 3. 어노테이션 사용 방식 -> 최신 스프링에서 가장 권장하는 방법이다.
    // -> 스프링에 종속된 기술이 아니라 다른 컨테이너에서도 동작한다. 컴포넌트 스캔과 잘 어울린다.
    // -> 하지만, 외부 라이브러리에는 적용하지 못한다. 외부 라이브러리를 초기화, 종료해야 하면 @Bean의 `initMethod`, `destroyMethod`를 사용하자.
    @PostConstruct
    public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }

    @PreDestroy
    public void close() {
        System.out.println("NetworkClient.close");
        disconnect();
    }


}
