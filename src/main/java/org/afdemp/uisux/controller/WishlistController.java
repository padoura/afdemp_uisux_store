package org.afdemp.uisux.controller;

import java.security.Principal;
import java.util.List;

import org.afdemp.uisux.domain.Product;
import org.afdemp.uisux.domain.User;
import org.afdemp.uisux.domain.Wishlist;
import org.afdemp.uisux.domain.security.UserRole;
import org.afdemp.uisux.service.ProductService;
import org.afdemp.uisux.service.UserRoleService;
import org.afdemp.uisux.service.UserService;
import org.afdemp.uisux.service.WishlistProductService;
import org.afdemp.uisux.service.WishlistService;
import org.afdemp.uisux.utility.WishlistUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private WishlistProductService wishlistProductService;
	
	@Autowired
	private ProductService productService;

	@RequestMapping("/")
	public String wishlist(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		Wishlist wishlist = userRole.getWishlist();
		List<Product> productList = WishlistUtility.getProductList(wishlist);
		model.addAttribute("wishlistProductList", wishlist.getWishlistProductList());
		model.addAttribute("productList", productList);
		model.addAttribute("wishlist", wishlist);
		
		for (Product p : productList) {
			if (p.getInStockNumber() > 0 && p.isActive()) {
				model.addAttribute("productsAvailable", true);
				break;
			}
		}
		
		return "wishlist";
	}

	@RequestMapping("/addItem")
	public String addItem(
			@ModelAttribute("product") Product product,
			Model model, Principal principal
			) {
		
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		product = productService.findOne(product.getId());
		
		if (wishlistProductService.addToWishlist(userRole.getWishlist(), product)) {
			model.addAttribute("addProductSuccess", true);
		}else {
			model.addAttribute("addProductFailure", true);
		}
		
		return "redirect:/products/productDetail?id="+product.getId();
	}
	
	
	@RequestMapping("/removeItem")
	public String removeItem(@RequestParam("wishlistProductId") Long wishlistProductId) {
		wishlistProductService.removeFromWishlist(wishlistProductService.findOne(wishlistProductId));
		return "redirect:/wishlist/";
	}
}
