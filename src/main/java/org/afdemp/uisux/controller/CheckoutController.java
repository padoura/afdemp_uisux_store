package org.afdemp.uisux.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.afdemp.uisux.domain.Address;
import org.afdemp.uisux.domain.CartItem;
import org.afdemp.uisux.domain.ClientOrder;
import org.afdemp.uisux.domain.CreditCard;
import org.afdemp.uisux.domain.ShoppingCart;
import org.afdemp.uisux.domain.User;
import org.afdemp.uisux.domain.security.UserRole;
import org.afdemp.uisux.service.AddressService;
import org.afdemp.uisux.service.CartItemService;
import org.afdemp.uisux.service.CreditCardService;
import org.afdemp.uisux.service.ShoppingCartService;
import org.afdemp.uisux.service.UserRoleService;
import org.afdemp.uisux.service.UserService;
import org.afdemp.uisux.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

	private Address currentShippingAddress = new Address();
	private Address currentBillingAddress = new Address();
	private CreditCard currentCreditCard = new CreditCard();

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailConstructor mailConstructor;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private AddressService addressService;
	
	@Autowired
	private CreditCardService creditCardService;
	
	@Autowired
	private ShoppingCartService shoppingCartService;

	@RequestMapping("/checkout")
	public String checkout(@RequestParam("shoppingCartId") Long shoppingCartId,
			@RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField, Model model,
			Principal principal, RedirectAttributes redirectAttributes ) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		ShoppingCart shoppingCart = userRole.getShoppingCart();
		
		
		currentShippingAddress = new Address();
		currentBillingAddress = new Address();
		currentCreditCard = new CreditCard();
		

		if (shoppingCartId != shoppingCart.getId()) {
			return "badRequestPage";
		}

		HashSet<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		boolean emptyCart = false;
		boolean notEnoughStock = false;
		if (cartItemList.size() == 0) {
			emptyCart = true;
			redirectAttributes.addAttribute("emptyCart", emptyCart);
			redirectAttributes.addAttribute("notEnoughStock", notEnoughStock);
			return "redirect:/shoppingCart/cart2";
		}

		for (CartItem cartItem : cartItemList) {
			if (cartItem.getProduct().getInStockNumber() < cartItem.getQty()) {
				notEnoughStock = true;
				redirectAttributes.addAttribute("emptyCart", emptyCart);
				redirectAttributes.addAttribute("notEnoughStock", notEnoughStock);
				return "redirect:/shoppingCart/cart2";
			}
		}
		
		shoppingCartService.setGrandTotal(shoppingCart);

		List<Address> shippingAddressList = userRole.getUserShippingAddressList();
		List<CreditCard> creditCardList = userRole.getCreditCardList();

		model.addAttribute("userShippingList", shippingAddressList);
		model.addAttribute("creditCardList", creditCardList);

		if (creditCardList.size() == 0) {
			model.addAttribute("emptyPaymentList", true);
		} else {
			model.addAttribute("emptyPaymentList", false);
		}

		if (shippingAddressList.size() == 0) {
			model.addAttribute("emptyShippingList", true);
		} else {
			model.addAttribute("emptyShippingList", false);
		}
		
		
		

		

		for (Address ad : shippingAddressList) {
			if (ad.isUserShippingDefault()) {
				addressService.deepCopyAddress(ad, this.currentShippingAddress);
			}
		}

		for (CreditCard cc : creditCardList) {
			if (cc.isDefaultCreditCard()) {
				creditCardService.deepCopyCreditCard(cc, this.currentCreditCard);
				addressService.deepCopyAddress(cc.getBillingAddress(), this.currentBillingAddress);
			}
		}
		
		System.out.println("1 --- " + this.currentBillingAddress.getReceiverName() + " -----------------------------");
		System.out.println("1 --- " + this.currentShippingAddress.getReceiverName() + " -----------------------------");

		model.addAttribute("shippingAddress", currentShippingAddress);
		model.addAttribute("creditCard", currentCreditCard);
		model.addAttribute("billingAddress", currentBillingAddress);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);

		model.addAttribute("classActiveShipping", true);

		if (missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}

		return "checkout";

	}

	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String checkoutPost(@ModelAttribute("shippingAddress") Address shippingAddress,
			@ModelAttribute("billingAddress") Address billingAddress, @ModelAttribute("creditCard") CreditCard creditCard,
			@ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
			@ModelAttribute("shippingMethod") String shippingMethod, Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		ShoppingCart shoppingCart = userRole.getShoppingCart();
		
		System.out.println("4 --- " + billingAddress.getReceiverName() + " -----------------------------");
		System.out.println("4 --- " + shippingAddress.getReceiverName() + " -----------------------------");

		HashSet<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		model.addAttribute("cartItemList", cartItemList);

		if (billingSameAsShipping.equals("true"))
			addressService.deepCopyAddress(shippingAddress, billingAddress);

		if (shippingAddress.getStreet1().isEmpty() 
				|| shippingAddress.getCity().isEmpty()
				|| shippingAddress.getState().isEmpty()
				|| shippingAddress.getReceiverName().isEmpty()
				|| shippingAddress.getZipcode().isEmpty() 
				|| creditCard.getCardNumber().isEmpty()
				|| creditCard.getCvc() == 0 || billingAddress.getStreet1().isEmpty()
				|| billingAddress.getCity().isEmpty() 
				|| billingAddress.getState().isEmpty()
				|| billingAddress.getZipcode().isEmpty())
			return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";
		
		billingAddress.setUserRole(userRole);
		shippingAddress.setUserRole(userRole);
		creditCard.setUserRole(userRole);
		
		System.out.println("5 --- " + billingAddress.getReceiverName() + " -----------------------------");
		System.out.println("5 --- " + shippingAddress.getReceiverName() + " -----------------------------");
		
		
		billingAddress = addressService.createAddress(billingAddress);
		creditCard.setBillingAddress(billingAddress);
		
		shippingAddress = addressService.createAddress(shippingAddress);
		creditCard = creditCardService.createCreditCard(creditCard);
		
		
		
		ClientOrder clientOrder = cartItemService.commitAndGetSale(shoppingCart, creditCard, billingAddress, shippingAddress, shippingMethod);
		
		mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, clientOrder, Locale.ENGLISH));
		
		
		
		LocalDate today = LocalDate.now();
		LocalDate estimatedDeliveryDate;
		
		if (shippingMethod.equals("groundShipping")) {
			estimatedDeliveryDate = today.plusDays(5);
		} else {
			estimatedDeliveryDate = today.plusDays(3);
		}
		
		model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);
		
		return "orderSubmittedPage";
	}

	@RequestMapping("/setShippingAddress")
	public String setShippingAddress(@RequestParam("shippingAddressId") Long shippingAddressId, Principal principal,
			Model model) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		ShoppingCart shoppingCart = userRole.getShoppingCart();
		
		Address shippingAddress = addressService.findById(shippingAddressId);

		if (shippingAddress.getUserRole().getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {
			addressService.deepCopyAddress(shippingAddress, currentShippingAddress);

			HashSet<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
			
			System.out.println("2 --- " + currentBillingAddress.getReceiverName() + " -----------------------------");
			System.out.println("2 --- " + currentShippingAddress.getReceiverName() + " -----------------------------");

			model.addAttribute("shippingAddress", currentShippingAddress);
			model.addAttribute("creditCard", currentCreditCard);
			model.addAttribute("billingAddress", currentBillingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", shoppingCart);

			List<Address> shippingAddressList = userRole.getUserShippingAddressList();
			List<CreditCard> creditCardList = userRole.getCreditCardList();

			model.addAttribute("shippingAddressList", shippingAddressList);
			model.addAttribute("creditCardList", creditCardList);

			model.addAttribute("classActiveShipping", true);
			model.addAttribute("emptyShippingList", false);

			if (creditCardList.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}


			return "checkout";
		}
	}

	@RequestMapping("/setCreditCard")
	public String setCreditCard(@RequestParam("creditCardId") Long creditCardId, Principal principal,
			Model model) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		ShoppingCart shoppingCart = userRole.getShoppingCart();
		
		CreditCard creditCard = creditCardService.findById(creditCardId);
		Address billingAddress = creditCard.getBillingAddress();

		if (creditCard.getUserRole().getUserRoleId() != userRole.getUserRoleId()) {
			return "badRequestPage";
		} else {
			creditCardService.deepCopyCreditCard(creditCard, this.currentCreditCard);

			HashSet<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

			addressService.deepCopyAddress(billingAddress, currentBillingAddress);

			model.addAttribute("shippingAddress", this.currentShippingAddress);
			model.addAttribute("creditCard", this.currentCreditCard);
			model.addAttribute("billingAddress", this.currentBillingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", shoppingCart);
			
			System.out.println("3 --- " + currentBillingAddress.getReceiverName() + " -----------------------------");
			System.out.println("3 --- " + currentShippingAddress.getReceiverName() + " -----------------------------");

			List<Address> shippingAddressList = userRole.getUserShippingAddressList();
			List<CreditCard> creditCardList = userRole.getCreditCardList();

			model.addAttribute("shippingAddressList", shippingAddressList);
			model.addAttribute("creditCardList", creditCardList);

			model.addAttribute("classActivePayment", true);

			model.addAttribute("emptyPaymentList", false);

			if (shippingAddressList.size() == 0) {
				model.addAttribute("emptyShippingList", true);
			} else {
				model.addAttribute("emptyShippingList", false);
			}
			
			return "checkout";
		}
	}

}
