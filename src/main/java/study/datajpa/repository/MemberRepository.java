package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

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
}
