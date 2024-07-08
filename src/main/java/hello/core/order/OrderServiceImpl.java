package hello.core.order;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    // 수정자 주입 -> '선택','변경' 가능성이 있는 의존관계에 사용한다.
    // -> 기본으로 '생성자 주입'을 사용하고, 만약 의존성 주입하는 값이 필수 값이 아닌 경우라면, 수정자 주입 방식을 옵션으로 부여하면 된다.
    // '필드 주입'은 사용하지 않는 게 좋다.
//    @Autowired(required = false) // -> 이렇게 하면 생성자 주입과 달리 주입할 대상이 없어도 동작한다.
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
//    @Autowired
//    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
//        this.discountPolicy = discountPolicy;
//    }

    // 필드 주입 (비권장) -> 외부에서 변경하려면 별도의 setXxx 메서드가 필요하다.
    // -> @Configuration 같은 스프링 설정을 목적으로 하는 곳이나 테스트 코드에선 사용해도 상관없다.
    // [ '필드 주입' 방식을 사용했을 때 발생하는 문제 ]
    // -> OrderServiceTest.java
    // 아래처럼 'DI Container' 즉 프레임워크의 도움 없이 순수 자바 코드로 테스트하고자 하는 경우엔, 별도의 메서드 없이는 원하는 값을 넣어줄 방법이 없다.
    // -> 이럴 바에는 '수정자 주입' 방식을 사용하는 것이 맞다. 그리고 순수한 자바 코드로 테스트하는 경우가 많다.
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private DiscountPolicy discountPolicy;

    // 일반 메서드 주입 (비권장) -> 굳이 사용할 필요가 없다.
//    private MemberRepository memberRepository;
//    private DiscountPolicy discountPolicy;

    // 웬만하면 기본으로 생성자 주입을 쓰자~ "단, 주입할 대상이 Spring Bean으로 등록되어 있어야만 에러가 발생하지 않는다."
    // -> MemoryMemberRepository, RateDiscountPolicy 둘 다 Spring Bean으로 등록되어 있어야 한다.
    /*
     * [ 생성자 주입의 장점 - 순수한 자바 언어의 특징을 잘 살리는 방법! ]
     * -> OrderServiceImplTest.java
     * 1. 의존성 주입을 누락한 경우에 컴파일 시점에 오류를 빠르게 잡아낼 수 있다. (수정자 주입 `setXxx`으로는 바로 잡아내기 어려울 수 있다.)
     * 2. final 키워드를 사용할 수 있어, 생성자에서 의존성 주입(new)을 깜빡한 경우에 컴파일러가 오류를 잡아준다.
     */
    // [ 타입으로 빈을 매칭할 때, 매칭 결과가 2개 이상(Fix..., Rate...)인 경우 해결 방법 ]
    // -> FixDiscountPolicy.java, RateDiscountPolicy.java
    // 1. 필드명 또는 파라미터명(`rateDiscountPolicy`)으로 빈 이름을 매칭한다.
    // 2. `@Qualifier`을 붙여주고 명시적으로 빈 이름을 지정해준다.
    // -> 만약 `@Qualifier`끼리 매칭되는 것을 찾지 못하면, 'mainDiscountPolicy'라는 이름의 스프링 빈을 찾는다. 이렇게 찾아도 없으면 에러가 발생하는 것이다.
    // -> `@Qualifier`는 `@Qualifier`를 찾는 용도로만 사용하자!
    // 3. `@Primary`를 사용하여 '우선순위'를 정한다.(자주 사용)
    // -> 메인 데이터베이스와 서브 데이터베이스(아주 가끔 사용)를 구분할 때, `@Qualifier`를 여러 번 사용하기 귀찮으니 메인 데이터베이스에 `@Primary`를 붙여주는 용도로 자주 사용한다.

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {
    // public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    // 일반 메서드 주입
//    @Autowired
//    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }

    @Override
    public  Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

//    public void setMemberRepository(MemoryMemberRepository memoryMemberRepository) {
//        this.memberRepository = memoryMemberRepository;
//    }
//
//    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
//        this.discountPolicy = discountPolicy;
//    }
}
