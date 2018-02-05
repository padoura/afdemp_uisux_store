package org.afdemp.uisux.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.afdemp.uisux.domain.Category;
import org.afdemp.uisux.domain.Product;
import org.afdemp.uisux.domain.User;
import org.afdemp.uisux.service.CategoryService;
import org.afdemp.uisux.service.ProductService;
import org.afdemp.uisux.service.UserService;

@Controller
public class SearchController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@RequestMapping("/searchByCategory")
	public String searchByCategory(
			@RequestParam("category") Long categoryId,
			Model model, Principal principal
			){
		if(principal!=null) {
			String username = principal.getName();
			User user = userService.findByUsername(username);
			model.addAttribute("user", user);
		}
		
		List<Category> categoryList = categoryService.findAll();
		model.addAttribute("categoryList", categoryList);
		
		Category category = categoryService.findOne(categoryId);
		
		
		String classActiveCategory = "active"+category.getType();
		classActiveCategory = classActiveCategory.replaceAll("\\s+", "");
		classActiveCategory = classActiveCategory.replaceAll("&", "");
		model.addAttribute(classActiveCategory, true);
		
		List<Product> productList = productService.findByCategoryAndActiveTrue(category);
		
		if (productList.isEmpty()) {
			model.addAttribute("emptyList", true);
			return "productShelf";
		}
		
		model.addAttribute("productList", productList);
		
		return "productShelf";
	}
	
	@RequestMapping("/searchProduct")
	public String searchProduct(
			@ModelAttribute("keyword") String keyword,
			Principal principal, Model model
			) {
		if(principal!=null) {
			String username = principal.getName();
			User user = userService.findByUsername(username);
			model.addAttribute("user", user);
		}
		
		List<Category> categoryList = categoryService.findAll();
		model.addAttribute("categoryList", categoryList);
		
		List<Product> productList = productService.searchActive(keyword);
		
		if (productList.isEmpty()) {
			model.addAttribute("emptyList", true);
			return "productShelf";
		}
		
		model.addAttribute("productList", productList);
		
		return "productShelf";
	}
}
