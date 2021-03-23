package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsername(String username);

    @Query(name="Member.findUser")
    List<Member> findUser(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findByUsernameAndAge(@Param("username") String username, @Param("age") int age);

    @Query("select m from Member m where m.username in :names")
    List<Member> findByUsernames(@Param("names") List<String> names);

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, m.age) from Member m where username = :username")
    List<MemberDto> findMemberDto(@Param("username") String username);

    Member findMemberByUsername(@Param("username") String username);

    Page<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t where age = :age"
            ,countQuery = "select count(m) from Member m where age = :age")
    Page<Member> findByAge2(@Param("age") int age, Pageable pageable);

    @Modifying(clearAutomatically = true)// 해당옵션 설정 시 update 후 영속성컨텍스트 클리어!!
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkUpdate(@Param("age") int age);

    @Override
    @EntityGraph(attributePaths = "team")//left join fetch team
    List<Member> findAll();

    @EntityGraph("Member.team")
    List<Member> findFetchByUsername(String username);

    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value="true"))
    List<Member> findReadOnlyByUsername(String username);
}
