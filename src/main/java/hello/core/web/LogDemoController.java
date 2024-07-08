package hello.core.web;

import hello.core.common.MyLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;
    private final MyLogger myLogger; // 스프링 컨테이너에 가짜 프록시 객체가 등록(주입)된다.
    // 가짜 프록시 객체는 원본 클래스를 상속 받아서 만들어졌기 때문에 이 객체를 사용하는 클라이언트 입장에선 사실 원본인지 아닌지도 모르게 동일하게 사용 가능하다.(다형성)
//    private final ObjectProvider<MyLogger> myLoggerProvider; // `request` 스코프 빈인데, 스프링 컨테이너에서 주입되는 시점에선 request가 아직 없기 때문에 가져올 빈이 없어 오류가 발생한다.
    // Provider를 사용하든, 프록시를 사용하든 핵심 아이디어는 진짜 객체 조회를 꼭 필요한 시점까지 지연처리 한다는 점이다!
    // -> 단지 애노테이션 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있고, 이것이 바로 다형성과 DI 컨테이너가 가진 가장 큰 강점이다!
    // 주의할 점 -> 매 요청마다 새로운 객체를 생성하는 것이기 때문에, 싱글톤 객체로 착각하고 사용하지 않도록 주의한다!

    @RequestMapping("log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();

//        MyLogger myLogger = myLoggerProvider.getObject(); // 요청이 올 때 주입하도록 하면 문제 X
        System.out.println("myLogger = " + myLogger.getClass()); // 의존관계 주입된 가짜 프록시 객체를 갖고 있다가
        myLogger.setRequestURL(requestURL); // 위임 로직이 내장되어 있어, 진짜 빈이 필요할 때 스프링 컨테이너에 요청해서 가져다 쓴다.

        myLogger.log("controller test");
        logDemoService.logic("testId");
        return "OK";
    }

}
