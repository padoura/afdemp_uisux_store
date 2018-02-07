package org.afdemp.uisux.utility;

import java.util.ArrayList;
import java.util.List;

import org.afdemp.uisux.domain.AbstractSale;
import org.afdemp.uisux.domain.ClientOrder;
import org.afdemp.uisux.domain.Product;
import org.afdemp.uisux.domain.Wishlist;
import org.afdemp.uisux.domain.WishlistProduct;
import org.springframework.stereotype.Component;

@Component
public class WishlistUtility {
	
	public static List<Product> getProductList(Wishlist wishlist){
		List<WishlistProduct> wishlistProductList = wishlist.getWishlistProductList();
		List<Product> productList = new ArrayList<>();
		for (WishlistProduct wp : wishlistProductList) {
			productList.add(wp.getProduct());
		}
		return productList;
	}

}
