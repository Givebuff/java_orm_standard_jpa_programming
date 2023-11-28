package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.domain.item.Item;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired ItemService itemService;

    public Long order(Long memberId, Long itemId, int count){
        Member member = memberRepository.findOne(memberId);
        Item item = itemService.fineOne(itemId);

        Delivery delivery = new Delivery(member.getAddress());
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);
        return order.getId();
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }
}
