package study.datajpa.dto;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age + ' ' + target.team.name}") //open projections 전체 select 후 WAS에서 조립
    String getUsername();
}
