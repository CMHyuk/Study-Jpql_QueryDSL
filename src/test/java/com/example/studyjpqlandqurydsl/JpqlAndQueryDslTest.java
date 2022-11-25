package com.example.studyjpqlandqurydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static com.example.studyjpqlandqurydsl.QMember.member;
import static com.example.studyjpqlandqurydsl.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

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
        Member member3 = new Member();

        member1.setName("member1");
        member1.setAge(10);
        member1.setTeam(team);
        member2.setName("member2");
        member2.setAge(20);
        member2.setTeam(team);
        member3.setName("member3");
        member3.setAge(20);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
    }

    @Test
    void selectJpql() {
        Member findMember = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", "member1")
                .getSingleResult();

        assertThat(findMember.getName()).isEqualTo("member1");
    }

    @Test
    void selectDsl() {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.name.eq("member2"))
                .fetchOne();

        assertThat(findMember.getName()).isEqualTo("member2");
    }

    @Test
    void selectMaxAge() {
        Integer maxAge = em.createQuery("select MAX(m.age) from Member m", Integer.class)
                .getSingleResult();

        assertThat(maxAge).isSameAs(20);
    }

    @Test
    void selectMaxAgeDsl() {
        Integer maxAge = queryFactory
                .select(member.age.max())
                .from(member)
                .fetchOne();

        assertThat(maxAge).isSameAs(20);
    }

    @Test
    void join() {
        List<Member> findMembers = em.createQuery("select m from Member m join m.team where m.team.teamName =:teamName", Member.class)
                .setParameter("teamName", "team1")
                .getResultList();

        for (Member findMember : findMembers) {
            System.out.println("findMember = " + findMember);
        }
    }

    @Test
    void joinDsl() {
        List<Member> findMembers = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.teamName.eq("team1"))
                .fetch();

        for (Member findMember : findMembers) {
            System.out.println("findMember = " + findMember);
        }
    }

    @Test
    void sort() {
        List<Member> findMembers = em.createQuery("select m from Member m" +
                        " where m.age = :age order by m.age DESC, m.name DESC", Member.class)
                .setParameter("age", 20)
                .getResultList();

        assertThat(findMembers.get(0).getName()).isEqualTo("member3");
        assertThat(findMembers.get(0).getAge()).isSameAs(20);
        assertThat(findMembers.get(1).getName()).isEqualTo("member2");
        assertThat(findMembers.get(1).getAge()).isSameAs(20);
    }

    @Test
    void sortDsl() {
        List<Member> findMembers = queryFactory
                .selectFrom(member)
                .where(member.age.eq(20))
                .orderBy(member.age.desc(), member.name.desc())
                .fetch();

        assertThat(findMembers.get(0).getName()).isEqualTo("member3");
        assertThat(findMembers.get(0).getAge()).isSameAs(20);
        assertThat(findMembers.get(1).getName()).isEqualTo("member2");
        assertThat(findMembers.get(1).getAge()).isSameAs(20);
    }
}
