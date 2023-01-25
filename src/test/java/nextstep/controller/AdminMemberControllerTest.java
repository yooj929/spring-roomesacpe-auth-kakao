package nextstep.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import nextstep.E2ETest;
import nextstep.auth.utils.JwtTokenProvider;
import nextstep.entity.Member;
import nextstep.entity.MemberRole;
import nextstep.repository.MemberDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


@E2ETest
class AdminMemberControllerTest {

    @Autowired
    MemberDao memberDao;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("일반_멤버는_삭제할_수_없다")
    void 일반_멤버는_삭제할_수_없다() {
        Member member = createMember("normal", MemberRole.USER);
        Member target = createMember("target", MemberRole.USER);
        memberDao.save(member);
        Long id = memberDao.save(target);
        String token = createToken(MemberRole.USER);

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .when().delete("/admin/member/" + id)
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }

    @Test
    @DisplayName("운영_멤버는_삭제할_수_있다")
    void 운영_멤버는_삭제할_수_있다() {
        Member member = createMember("admin", MemberRole.ADMIN);
        Member target = createMember("target", MemberRole.USER);
        memberDao.save(member);
        Long id = memberDao.save(target);
        String token = createToken(MemberRole.ADMIN);

        Long count = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(token)
                .when().delete("/admin/member/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Long.class);

        assertThat(count).isEqualTo(1L);

    }

    private Member createMember(String normal, MemberRole user) {
        return Member.builder().username(normal)
                .password("PASSWORD")
                .name("NAME")
                .phone("PHONE")
                .role(user)
                .build();
    }

    private String createToken(MemberRole role) {
        Member member = Member.builder().role(role)
                .username("USERNAME")
                .name("NAME")
                .phone("PHONE")
                .password("PASSWORD").build();
        Member.giveId(member, 1L);
        return jwtTokenProvider.createToken(member);
    }
}