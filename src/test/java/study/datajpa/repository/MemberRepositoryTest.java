package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        List<Member> findMember = memberRepository.findByUsername("memberA");

        Assertions.assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindUser(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        List<Member> findMember = memberRepository.findUser("memberA");

        Assertions.assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindByUsernameAndAge(){
        Member member = new Member("memberA",1,null);
        Member savedMember = memberRepository.save(member);
        List<Member> findMember = memberRepository.findByUsernameAndAge("memberA",1);

        Assertions.assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindByUsernames(){
        Member member = new Member("memberA");
        Member member2 = new Member("memberB");
        Member savedMember = memberRepository.save(member);
        Member savedMember2 = memberRepository.save(member2);

        List<Member> findMember = memberRepository.findByUsernames(Arrays.asList(new String[]{"memberA", "memberB"}));

        for (Member member0 : findMember) {
            System.out.println("member = " + member0);
        }

        Assertions.assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMember.get(1).getId()).isEqualTo(savedMember2.getId());
    }

    @Test
    public void testFindMemberDto(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        List<MemberDto> findMember = memberRepository.findMemberDto("memberA");

        Assertions.assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindMemberByUsername(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findMemberByUsername("memberA");//단건 조회(없으면 NULL, N건이면 Exception)

        List<Member> findMemberList = memberRepository.findUser("memberA");//다건 조회(없으면 empty)

        Assertions.assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMemberList.get(0).getId()).isEqualTo(savedMember.getId());
    }
}