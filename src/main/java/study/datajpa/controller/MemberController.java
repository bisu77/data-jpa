package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMembers(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMembers2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    /**
     * http://localhost:8080/members?page=1&size=10&sort=age,desc&sort=username
     * 나이순으로 내림차순 정렬, 이름 순으로 오름차순 정렬 후
     * 2번 페이지 10개 출력
     * application.yaml에서 글로벌 페이징 size 세팅 가능(default : size=20)
     * @PageableDefault 어노테이션을 사용해 각 페이징 size 세팅 가능
     * @Qualifier 어노테이션 :: 복수 개의 페이징 처리가 필요할 때 접두사 + '_page'로 처리 가능
     *
     * Page 시작 기준을 1로 처리하고 싶으면
     * 1) Pageable, Page 구현체를 사용하지 않고 직접 구현
     * 2) 설정파일에 spring.data.web.pageable.one-indexed-parameters = true로 설정
     *    단, 요청 값을 -1처리 해주기 때문에 PageNumber 관련 응답 필드는 요청 값에서 -1 처리된 값으로 넘어 감.
     * @param pageable
     * @return
     */
    @GetMapping("/members")
    public Page<MemberDto> findAll(@PageableDefault(size=10, sort="age", direction = Sort.Direction.DESC) Pageable pageable){
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @PostConstruct
    private void init(){
        for(int i=0;i<100;i++){
            Member member = new Member("member"+i,i);
            memberRepository.save(member);
        }
    }
}
