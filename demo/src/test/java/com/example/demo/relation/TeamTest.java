package com.example.demo.relation;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    @Test
    void test1() {
        try {
            tx.begin();
//            testSave();
//            updateRelation();
//            biDirection();
            testRelation();
//            tx.commit();
            throw new Exception();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    void testSave(){
        Team team1 = new Team();
        team1.setId("team1");
        team1.setName("팀1");
        em.persist(team1);

        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        member1.setTeam(team1);
        em.persist(member1);

        Member member2 = new Member();
        member2.setId("member2");
        member2.setUsername("회원2");
        member2.setTeam(team1);
        em.persist(member2);
    }

    void updateRelation() {
        Team team2 = new Team();
        team2.setId("team2");
        team2.setName("팀2");
        em.persist(team2);

        Member member = em.find(Member.class, "member1");
        member.setTeam(team2);
    }

    void biDirection() {
        Team team = em.find(Team.class, "team1");
        List<Member> members = team.getMembers();

        System.out.println("=================================================================");
        for(Member member:members){
            System.out.println("이름 : " + member.getUsername());
        }
    }

    // 쓰기 지연? 때문에 애플리케이션 딴에선 관계가 바뀌어도 DB에선 데이터가 바뀌지 않아 변경 후 다시 조회해도
    // 변경되기 전의 데이터가 조회된다. 그러므로 연관관계 변경 시 주의사항을 잘 보고 작성해야한다.
    void testRelation(){
        Team team1 = em.find(Team.class, "team1");

        System.out.println("테스트 1 ====");
        for(Member member:team1.getMembers()){
            System.out.println("이름 : " + member.getUsername());
        }

        Member member1 = em.find(Member.class, "member1");
        Team team2 = em.find(Team.class, "team2");
        member1.setTeam(team2);

        team1 = em.find(Team.class, "team1");

        System.out.println("테스트 2 ====");
        for(Member member:team1.getMembers()){
            System.out.println("이름 : " + member.getUsername());
        }
    }
}