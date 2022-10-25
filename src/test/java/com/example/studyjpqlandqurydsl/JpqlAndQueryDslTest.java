package com.example.studyjpqlandqurydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static com.example.studyjpqlandqurydsl.QMember.member;

@SpringBootTest
@Transactional
public class JpqlAndQueryDslTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void init() {
        queryFactory = new JPAQueryFactory(em);

        Team team = new Team();
        team.setTeamName("team1");

        Member member1 = new Member();
        Member member2 = new Member();

        member1.setName("member1");
        member1.setAge(10);
        member1.setTeam(team);
        member2.setName("member2");
        member2.setAge(20);
        member2.setTeam(team);

        em.persist(member1);
        em.persist(member2);
    }

    @Test
    void selectJpql() {
        Member findMember = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getName()).isEqualTo("member1");
    }

    @Test
    void selectDsl() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.name.eq("member2"))
                .fetchOne();

        Assertions.assertThat(findMember.getName()).isEqualTo("member2");
    }


}
