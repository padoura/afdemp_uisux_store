package org.afdemp.uisux.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import org.afdemp.uisux.domain.Address;
import org.afdemp.uisux.domain.CartItem;
import org.afdemp.uisux.domain.ClientOrder;
import org.afdemp.uisux.domain.CreditCard;
import org.afdemp.uisux.domain.Product;
import org.afdemp.uisux.domain.ShoppingCart;

public interface CartItemService {

	boolean addToCart(ShoppingCart shoppingCart, Product product, int qty);
	
	boolean emptyCart(Long shoppingCartId);
	
	HashSet<Product> commitSale(ShoppingCart shoppingCart,CreditCard creditCard,Address billingAddress,Address shippingAddress,String shippingMethod);
	
	ClientOrder commitAndGetSale(ShoppingCart shoppingCart,CreditCard creditCard,Address billingAddress,Address shippingAddress,String shippingMethod);

	List<CartItem> findByClientOrder(ClientOrder clientOrder);

	HashSet<CartItem> findByShoppingCart(ShoppingCart shoppingCart);

	CartItem findById(Long cartItemId);

	void updateToCart(CartItem cartItem, int qty);

	boolean removeCartItem(Long id);
	
	BigDecimal getGrandTotal(ShoppingCart shoppingCart);
	
	
}
