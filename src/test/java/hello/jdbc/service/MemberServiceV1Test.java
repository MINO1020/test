package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
*
* 기본 동작, 트랜잭션이 없어서 문제 발생.
* */
class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV1 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach
    void Before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV1(dataSource);
        memberService = new MemberServiceV1(memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }


    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member MemberA = new Member(MEMBER_A, 10000);
        Member MemberB = new Member(MEMBER_B, 10000);
        memberRepository.save(MemberA);
        memberRepository.save(MemberB);
        //when

        memberService.accountTransfer(MemberA.getMemberId(), MemberB.getMemberId(), 2000);


        //then
        Member findMemberA = memberRepository.findById(MemberA.getMemberId());
        Member findMemberB = memberRepository.findById(MemberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외발생")
    void accountTransferEx() throws SQLException {
        //given
        Member MemberA = new Member(MEMBER_A, 10000);
        Member MemberEX = new Member(MEMBER_EX, 10000);
        memberRepository.save(MemberA);
        memberRepository.save(MemberEX);

        //when
        assertThatThrownBy(()-> memberService.accountTransfer(MemberA.getMemberId(), MemberEX.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(MemberA.getMemberId());
        Member findMemberB = memberRepository.findById(MemberEX.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }

}















