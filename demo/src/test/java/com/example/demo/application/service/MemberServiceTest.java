package com.example.demo.application.service;

import com.example.demo.SpringConfig;
import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
@Transactional
class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void signIn() throws Exception {
        Member member = new Member();
        member.setName("kim");

        Long saveId = memberService.join(member);

        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    public void duplicateException() throws Exception{
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);
        memberService.join(member2);

        fail("예외 발생!");
    }
}