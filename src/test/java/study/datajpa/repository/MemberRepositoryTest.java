package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
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

        assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindUser(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        List<Member> findMember = memberRepository.findUser("memberA");

        assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindByUsernameAndAge(){
        Member member = new Member("memberA",1,null);
        Member savedMember = memberRepository.save(member);
        List<Member> findMember = memberRepository.findByUsernameAndAge("memberA",1);

        assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
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

        assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.get(1).getId()).isEqualTo(savedMember2.getId());
    }

    @Test
    public void testFindMemberDto(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        List<MemberDto> findMember = memberRepository.findMemberDto("memberA");

        assertThat(findMember.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void testFindMemberByUsername(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findMemberByUsername("memberA");//단건 조회(없으면 NULL, N건이면 Exception)

        List<Member> findMemberList = memberRepository.findUser("memberA");//다건 조회(없으면 empty)

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMemberList.get(0).getId()).isEqualTo(savedMember.getId());
    }

    @Test
    public void paging(){
        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",10);
        Member memberC = new Member("memberC",10);
        Member memberD = new Member("memberD",10);
        Member memberE = new Member("memberE",10);

        Member savedMemberA = memberRepository.save(memberA);
        Member savedMemberB = memberRepository.save(memberB);
        Member savedMemberC = memberRepository.save(memberC);
        Member savedMemberD = memberRepository.save(memberD);
        Member savedMemberE = memberRepository.save(memberE);

        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));

        Page<Member> memberPage = memberRepository.findByAge2(10,pageRequest);

        List<Member> members = memberPage.getContent();

        for (Member member : members) {
            System.out.println("member = " + member);
        }



        assertThat(memberPage.getNumber()).isEqualTo(0);
        assertThat(memberPage.getTotalElements()).isEqualTo(5);
        assertThat(memberPage.hasNext()).isTrue();
        assertThat(memberPage.isFirst()).isTrue();
        assertThat(memberPage.getTotalPages()).isEqualTo(2);
    }
}