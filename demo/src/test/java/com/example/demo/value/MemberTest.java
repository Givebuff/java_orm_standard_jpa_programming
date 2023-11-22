package com.example.demo.value;

import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    @Test
    void test1() {
        try {
            tx.begin();
            test2();
            tx.commit();
        } catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    @Test
    void test(){
        Member member = new Member();
        Address homeAddress = new Address("홈 지역", "홈 길", "홈 우편번호");
        Address companyAddress = new Address("일 지역", "일 길", "일 우편번호");
        Period period = new Period(new Date(2021, Calendar.NOVEMBER, 1), new Date(2023, Calendar.MAY, 1));

        member.setId(123L);
        member.setHomeAddress(homeAddress);
        member.setCompanyAddress(companyAddress);
        member.setPeriod(period);

        em.persist(member);
    }

    @Test
    void test2(){
        Member member = em.find(Member.class, 123L);
        member.setHomeAddress(new Address("A","B", "C"));
    }
}