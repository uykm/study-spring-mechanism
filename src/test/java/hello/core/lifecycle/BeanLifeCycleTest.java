package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);

        NetworkClient client = ac.getBean(NetworkClient.class);
        ac.close();
    }

    @Configuration
    static class LifeCycleConfig {

        // @Bean의 `destroyMethod` 속성엔 기본값이 `(inferred)`으로 등록되어 있다.
        // 이 (추론) 기능은 라이브러리 대부분 갖고 있는`close`, `shutdown`이라는 이름의 메서드를 자동으로 호출해준다.
        // -> 스프링 빈으로 직접 등록하면 종료 메서드는 따로 적어주지 않아도 잘 작동한다.(단, 공백으로 넣으면 프로그램이 망가진다.)
//        @Bean(initMethod = "init", destroyMethod = "close")
        @Bean
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
}
