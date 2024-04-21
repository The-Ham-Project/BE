package com.hanghae.theham.domain.rental.repository;

import com.hanghae.theham.domain.member.entity.Member;
import com.hanghae.theham.domain.member.entity.type.RoleType;
import com.hanghae.theham.domain.member.repository.MemberRepository;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.type.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalDistanceRepository rentalDistanceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point location1 = geometryFactory.createPoint(new Coordinate(12.123, 12.123));
        location1.setSRID(4326);
        Point location2 = geometryFactory.createPoint(new Coordinate(23.123, 23.123));
        location2.setSRID(4326);

        Member member = Member.builder()
                .email("test@test.com")
                .password("1234")
                .nickname("더함이9999")
                .profileUrl("url")
                .role(RoleType.ROLE_USER)
                .latitude(12.123)
                .longitude(12.123)
                .kakaoId("kakao")
                .build();

        memberRepository.save(member);

        Rental rental1 = Rental.builder()
                .category(CategoryType.BOOK)
                .title("제목1")
                .content("내용1")
                .rentalFee(100L)
                .deposit(100L)
                .location(location1)
                .district("종로구")
                .member(member)
                .build();

        Rental rental2 = Rental.builder()
                .category(CategoryType.ELECTRONIC)
                .title("제목2")
                .content("내용2")
                .rentalFee(200L)
                .deposit(200L)
                .district("종로구")
                .location(location1)
                .member(member)
                .build();

        Rental rental3 = Rental.builder()
                .category(CategoryType.OTHER)
                .title("제목3")
                .content("내용3")
                .rentalFee(300L)
                .deposit(300L)
                .district("종로구")
                .location(location2)
                .member(member)
                .build();

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);
    }

    @DisplayName("성공 - 카테고리로 게시글 찾기")
    @Test
    void findAllByCategory_01() {
        // given
        CategoryType categoryType = CategoryType.BOOK;
        Pageable pageable = PageRequest.of(0, 6);

        // when
        Page<Rental> rentalPage = rentalRepository.findAllByCategory(categoryType, pageable);
        System.out.println(rentalPage.getContent().get(0).getTitle());

        // then
        assertEquals(1, rentalPage.getContent().size());
        assertEquals("제목1", rentalPage.getContent().get(0).getTitle());
    }

    @DisplayName("성공 - 회원이 작성한 게시글 찾기")
    @Test
    void findByMember_01() {
        // given
        Member member = memberRepository.findByEmail("test@test.com").get();

        Pageable pageable = PageRequest.of(0, 6);

        // when
        Page<Rental> rentalPage = rentalRepository.findByMember(member, pageable);

        // then
        assertEquals(3, rentalPage.getContent().size());
    }

    @DisplayName("성공 - 반경 4km 게시글 찾기")
    @Test
    void findAllByDistance_01() {
        // given
        Pageable pageable = PageRequest.of(0, 6);

        GeometryFactory geometryFactory = new GeometryFactory();
        Point memberLocation = geometryFactory.createPoint(new Coordinate(12.123, 12.123));
        memberLocation.setSRID(4326);

        // when
        List<Rental> rentalList = rentalDistanceRepository.findRentalsNearby(memberLocation, 4000, pageable).getContent();

        // then
        assertEquals(2, rentalList.size());
    }

    @DisplayName("성공 - 카테고리별 반경 4km 게시글 찾기")
    @Test
    void findAllByCategoryAndDistance_01() {
        // given
        Pageable pageable = PageRequest.of(0, 6);

        // when
        GeometryFactory geometryFactory = new GeometryFactory();
        Point memberLocation = geometryFactory.createPoint(new Coordinate(12.123, 12.123));
        memberLocation.setSRID(4326);

        List<Rental> rentalList =
                rentalDistanceRepository.findRentalsByCategoryNearby(CategoryType.BOOK, memberLocation, 4000, pageable).getContent();

        // then
        assertEquals(1, rentalList.size());
    }
}