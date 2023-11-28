package com.example.demo.web;

import com.example.demo.domain.item.Book;
import com.example.demo.domain.item.Item;
import com.example.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ItemController {
    @Autowired
    ItemService itemService;

    @GetMapping(value = "/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping(value = "/items/new")
    public String createForm() {
        return "items/createItemForm";
    }

    @PostMapping(value = "/items/new")
    public String create(Book item){
        itemService.saveItem(item);
        return "redirect:/items";
    }

    @GetMapping(value = "/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Item item = itemService.fineOne(itemId);
        model.addAttribute("item", item);
        return "items/updateItemForm";
    }

    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("item") Book item){
        itemService.saveItem(item);
        return "redirect:/items";
    }
}
