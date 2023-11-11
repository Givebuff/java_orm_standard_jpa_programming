package com.example.demo.repository;

import org.junit.jupiter.api.Test;

import javax.persistence.*;

import java.util.List;

class MemberRepositoryTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    @Test
    void test1() {
        try {
            tx.begin();
            logic();
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    void logic() {
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("지한");
        member.setAge(2);

        em.persist(member);
        member.setAge(20);

        Member findMember = em.find(Member.class, id);
        System.out.println("find Member = " + findMember.getUsername() + ", age : " + findMember.getAge());

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        System.out.println("member.size : " + members.size());

        em.remove(member);
    }

    @Test
    void examMergeMain(){
        Member member = new Member();
        member.setId("memberA");
        member.setUsername("회원1");

        tx.begin();
        em.persist(member);
        tx.commit();
        em.close();

        member.setUsername("회원명 변경");

        tx.begin();
        Member mergeMember = em.merge(member);
        tx.commit();

        System.out.println("username 비교");
        System.out.println(member.getUsername());
        System.out.println(mergeMember.getUsername());

        System.out.println("영속성 컨텍스트 비교");
        System.out.println(em.contains(member));
        System.out.println(em.contains(mergeMember));
    }
}