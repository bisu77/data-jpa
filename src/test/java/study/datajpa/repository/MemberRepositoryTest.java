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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

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

    @Test
    public void bulkUpdate() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 11);
        Member memberC = new Member("memberC", 20);
        Member memberD = new Member("memberD", 32);
        Member memberE = new Member("memberE", 43);

        Member savedMemberA = memberRepository.save(memberA);
        Member savedMemberB = memberRepository.save(memberB);
        Member savedMemberC = memberRepository.save(memberC);
        Member savedMemberD = memberRepository.save(memberD);
        Member savedMemberE = memberRepository.save(memberE);

        //Process 1. 영속성컨텍스트 DB flush();
        int result = memberRepository.bulkUpdate(20);

        //******************* JPA 기본제공 findById *******************//
        //Process 2-1. 영속성컨텍스트 조회
        //Process 2-2. 영속성컨텍스트 없으면 DB SELECT, 있으면 영속성컨텍스트 리턴
        Optional<Member> member1 = memberRepository.findById(1l);
        System.out.println("member1 = " + member1.get().toString());

        //******************* 개발자 custom JPQL *******************//
        //Process 2-1. 영속성 조회 없이 선 DB SELECT
        //Process 2-2. 조회 리턴 값 영속성컨텍스트 저장 시도
        //Process 2-3. 영속성컨텍스트 존재하면 SELECT 조회 데이터 삭제
        List<Member> members = memberRepository.findAll();

        //Process 3. 영속성컨텍스트 존재하기 때문에 벌크연산 수행 전 영속성컨텍스트 값 리턴
        for (Member member : members) {
            System.out.println("member.getAge() = " + member.getAge());
        }

        assertThat(result).isEqualTo(3);
    }

    @Test
    public void entityGraphTest(){
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 11);
        Member memberC = new Member("memberC", 20);
        Member memberD = new Member("memberD", 32);
        Member memberE = new Member("memberA", 43);

        Member savedMemberA = memberRepository.save(memberA);
        Member savedMemberB = memberRepository.save(memberB);
        Member savedMemberC = memberRepository.save(memberC);
        Member savedMemberD = memberRepository.save(memberD);
        Member savedMemberE = memberRepository.save(memberE);

        List<Member> all = memberRepository.findAll();

        for (Member member : all) {
            System.out.println("memberFetch1 = " + member);
        }

        List<Member> fetchAll = memberRepository.findFetchByUsername("memberA");

        for (Member member : fetchAll) {
            System.out.println("memberFetch2 = " + member);
        }
    }

    @Test
    public void queryHintTest() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 11);
        Member memberC = new Member("memberC", 20);
        Member memberD = new Member("memberD", 32);
        Member memberE = new Member("memberA", 43);

        Member savedMemberA = memberRepository.save(memberA);
        Member savedMemberB = memberRepository.save(memberB);
        Member savedMemberC = memberRepository.save(memberC);
        Member savedMemberD = memberRepository.save(memberD);
        Member savedMemberE = memberRepository.save(memberE);

        // ======================== dirty checking ======================== //
        Member findMemberB = memberRepository.findById(memberB.getId()).get();
        findMemberB.setUsername("memberA");// updated
        em.flush();
        em.clear();

        // ======================== readOnly JPA HINT ======================== //
        Member findMemberD = memberRepository.findReadOnlyByUsername("memberD").get(0);
        findMemberD.setUsername("memberA");// not updated
        em.flush();
        em.clear();
    }

    @Test
    public void customRepositoryTest() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 11);
        Member memberC = new Member("memberC", 20);
        Member memberD = new Member("memberD", 32);
        Member memberE = new Member("memberA", 43);

        Member savedMemberA = memberRepository.save(memberA);
        Member savedMemberB = memberRepository.save(memberB);
        Member savedMemberC = memberRepository.save(memberC);
        Member savedMemberD = memberRepository.save(memberD);
        Member savedMemberE = memberRepository.save(memberE);

        List<Member> memberCustom = memberRepository.findMemberCustom();

        for (Member member : memberCustom) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void auditTest() throws InterruptedException {
        Member memberA = new Member("memberA", 10);
        Member savedMemberA = memberRepository.save(memberA);

        Thread.sleep(100);
        savedMemberA.setUsername("memberB");

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(savedMemberA.getId()).get();

        System.out.println("findMember.getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedBy = " + findMember.getLastModifiedBy());
        System.out.println("findMember.getLastModifiedDate = " + findMember.getLastModifedDate());

    }
}