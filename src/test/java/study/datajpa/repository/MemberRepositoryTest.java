package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.*;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
        Member findMember = memberRepository.findMemberByUsername("memberA");//?????? ??????(????????? NULL, N????????? Exception)

        List<Member> findMemberList = memberRepository.findUser("memberA");//?????? ??????(????????? empty)

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

        //Process 1. ????????????????????? DB flush();
        int result = memberRepository.bulkUpdate(20);

        //******************* JPA ???????????? findById *******************//
        //Process 2-1. ????????????????????? ??????
        //Process 2-2. ????????????????????? ????????? DB SELECT, ????????? ????????????????????? ??????
        Optional<Member> member1 = memberRepository.findById(1l);
        System.out.println("member1 = " + member1.get().toString());

        //******************* ????????? custom JPQL *******************//
        //Process 2-1. ????????? ?????? ?????? ??? DB SELECT
        //Process 2-2. ?????? ?????? ??? ????????????????????? ?????? ??????
        //Process 2-3. ????????????????????? ???????????? SELECT ?????? ????????? ??????
        List<Member> members = memberRepository.findAll();

        //Process 3. ????????????????????? ???????????? ????????? ???????????? ?????? ??? ????????????????????? ??? ??????
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

    @Test
    public void queryByExample(){
        Member memberA = new Member("memberA", 10);
        Team team = new Team("teamA");
        em.persist(team);
        memberA.setTeam(team);

        Member savedMemberA = memberRepository.save(memberA);

        em.flush();
        em.clear();

        Member findMember = new Member("memberA");
        Team findTeam = new Team("teamA");//INNER JOIN??? ??????
        findMember.setTeam(findTeam);

        ExampleMatcher matcher = ExampleMatcher.matching()//???????????? ?????? :: Property ?????? ?????????
                                                .withIgnorePaths(new String[]{"age", "createdDate", "lastModifedDate"});

        Example<Member> example = Example.of(findMember,matcher);
        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
    }

    @Test
    public void findProjections() {
        Member memberA = new Member("memberA", 10);
        Team team = new Team("teamA");
        em.persist(team);
        memberA.setTeam(team);

        Member savedMemberA = memberRepository.save(memberA);

        em.flush();
        em.clear();

        //List<UsernameOnly> memberA1 = memberRepository.findUsernameOnlyByUsername("memberA");//?????????????????????
        //List<UsernameOnlyDto> memberA1 = memberRepository.findUsernameDtoByUsername("memberA");//????????? ??????
        //List<UsernameOnly> memberA1 = memberRepository.findUsernameByUsername("memberA",UsernameOnly.class);//?????? projection ??????
        List<NestedClosedProjection> memberA1 = memberRepository.findNestedProjectionsByUsername("memberA");//?????? projection

        for (NestedClosedProjection usernameOnly : memberA1) {
            System.out.println("usernameOnly name = " + usernameOnly.getUsername());
            System.out.println("usernameOnly team = " + usernameOnly.getTeam());
        }
    }

    @Test
    public void findNativeQuery() {
        Member memberA = new Member("memberA", 10);
        Team team = new Team("teamA");
        em.persist(team);
        memberA.setTeam(team);

        Member savedMemberA = memberRepository.save(memberA);

        em.flush();
        em.clear();

        Member memberA1 = memberRepository.findByNativeQuery("memberA");
        System.out.println("memberA1 = " + memberA1);
    }

    @Test
    public void findNativeProjection() {
        Member memberA = new Member("memberA", 10);
        Team team = new Team("teamA");
        em.persist(team);
        memberA.setTeam(team);

        Member savedMemberA = memberRepository.save(memberA);

        em.flush();
        em.clear();

        Page<MemberProjection> memberA1 = memberRepository.findByNativeProjection("memberA", PageRequest.of(0,10));
        List<MemberProjection> result = memberA1.getContent();
        for (MemberProjection memberProjection : result) {
            System.out.println("memberProjection id = " + memberProjection.getId());
            System.out.println("memberProjection username = " + memberProjection.getUsername());
            System.out.println("memberProjection teamName = " + memberProjection.getTeamName());
        }

    }
}