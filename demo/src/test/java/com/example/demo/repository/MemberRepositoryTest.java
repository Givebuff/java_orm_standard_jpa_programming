package com.example.demo.repository;

import org.junit.jupiter.api.Test;

import javax.persistence.*;

import java.util.List;

class MemberRepositoryTest {

    @Test
    void test1() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            logic(em);
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    void logic(EntityManager em) {
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
}