package org.afdemp.uisux.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;

import org.afdemp.uisux.domain.Address;
import org.afdemp.uisux.domain.CartItem;
import org.afdemp.uisux.domain.CreditCard;
import org.afdemp.uisux.domain.ShoppingCart;
import org.afdemp.uisux.domain.User;
import org.afdemp.uisux.domain.security.UserRole;
import org.afdemp.uisux.service.AddressService;
import org.afdemp.uisux.service.CartItemService;
import org.afdemp.uisux.service.CreditCardService;
import org.afdemp.uisux.service.UserRoleService;
import org.afdemp.uisux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

	private Address currentShippingAddress = new Address();
	private Address currentBillingAddress = new Address();
	private CreditCard currentCreditCard = new CreditCard();
//
//	@Autowired
//	private JavaMailSender mailSender;
//	
//	@Autowired
//	private MailConstructor mailConstructor;
//	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private CartItemService cartItemService;
//	
//	@Autowired
//	private ShoppingCartService shoppingCartService;
//
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private CreditCardService creditCardService;
//
//	@Autowired
//	private UserShippingService userShippingService;
//
//	@Autowired
//	private UserPaymentService userPaymentService;
//	
//	@Autowired
//	private OrderService orderService;

	@RequestMapping("/checkout")
	public String checkout(@RequestParam("shoppingCartId") Long shoppingCartId,
			@RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField, Model model,
			Principal principal) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		ShoppingCart shoppingCart = userRole.getShoppingCart();

		if (shoppingCartId != shoppingCart.getId()) {
			return "badRequestPage";
		}

		HashSet<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

		if (cartItemList.size() == 0) {
			model.addAttribute("emptyCart", true);
			return "forward:/shoppintCart/cart";
		}

		for (CartItem cartItem : cartItemList) {
			if (cartItem.getProduct().getInStockNumber() < cartItem.getQty()) {
				model.addAttribute("notEnoughStock", true);
				return "forward:/shoppingCart/cart";
			}
		}

		List<Address> shippingAddressList = userRole.getUserShippingAddressList();
		List<CreditCard> creditCardList = userRole.getCreditCardList();

		model.addAttribute("userShippingList", shippingAddressList);
		model.addAttribute("userPaymentList", creditCardList);

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

		model.addAttribute("shippingAddress", currentShippingAddress);
		model.addAttribute("payment", currentCreditCard);
		model.addAttribute("billingAddress", currentBillingAddress);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);

		model.addAttribute("classActiveShipping", true);

		if (missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}

		return "checkout";

	}

//	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
//	public String checkoutPost(@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
//			@ModelAttribute("billingAddress") BillingAddress billingAddress, @ModelAttribute("payment") Payment payment,
//			@ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
//			@ModelAttribute("shippingMethod") String shippingMethod, Principal principal, Model model) {
//		ShoppingCart shoppingCart = userService.findByUsername(principal.getName()).getShoppingCart();
//
//		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
//		model.addAttribute("cartItemList", cartItemList);
//
//		if (billingSameAsShipping.equals("true")) {
//			billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
//			billingAddress.setBillingAddressStreet1(shippingAddress.getShippingAddressStreet1());
//			billingAddress.setBillingAddressStreet2(shippingAddress.getShippingAddressStreet2());
//			billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
//			billingAddress.setBillingAddressState(shippingAddress.getShippingAddressState());
//			billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
//			billingAddress.setBillingAddressZipcode(shippingAddress.getShippingAddressZipcode());
//		}
//
//		if (shippingAddress.getShippingAddressStreet1().isEmpty() 
//				|| shippingAddress.getShippingAddressCity().isEmpty()
//				|| shippingAddress.getShippingAddressState().isEmpty()
//				|| shippingAddress.getShippingAddressName().isEmpty()
//				|| shippingAddress.getShippingAddressZipcode().isEmpty() 
//				|| payment.getCardNumber().isEmpty()
//				|| payment.getCvc() == 0 || billingAddress.getBillingAddressStreet1().isEmpty()
//				|| billingAddress.getBillingAddressCity().isEmpty() 
//				|| billingAddress.getBillingAddressState().isEmpty()
//				|| billingAddress.getBillingAddressName().isEmpty()
//				|| billingAddress.getBillingAddressZipcode().isEmpty())
//			return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";
//		
//		User user = userService.findByUsername(principal.getName());
//		
//		Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, payment, shippingMethod, user);
//		
//		mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, order, Locale.ENGLISH));
//		
//		shoppingCartService.clearShoppingCart(shoppingCart);
//		
//		LocalDate today = LocalDate.now();
//		LocalDate estimatedDeliveryDate;
//		
//		if (shippingMethod.equals("groundShipping")) {
//			estimatedDeliveryDate = today.plusDays(5);
//		} else {
//			estimatedDeliveryDate = today.plusDays(3);
//		}
//		
//		model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);
//		
//		return "orderSubmittedPage";
//	}
//
//	@RequestMapping("/setShippingAddress")
//	public String setShippingAddress(@RequestParam("userShippingId") Long userShippingId, Principal principal,
//			Model model) {
//		User user = userService.findByUsername(principal.getName());
//		UserShipping userShipping = userShippingService.findById(userShippingId);
//
//		if (userShipping.getUser().getId() != user.getId()) {
//			return "badRequestPage";
//		} else {
//			shippingAddressService.setByUserShipping(userShipping, shippingAddress);
//
//			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
//
//			model.addAttribute("shippingAddress", shippingAddress);
//			model.addAttribute("payment", payment);
//			model.addAttribute("billingAddress", billingAddress);
//			model.addAttribute("cartItemList", cartItemList);
//			model.addAttribute("shoppingCart", user.getShoppingCart());
//
//			List<String> stateList = USConstants.listOfUSStatesCode;
//			Collections.sort(stateList);
//			model.addAttribute("stateList", stateList);
//
//			List<UserShipping> userShippingList = user.getUserShippingList();
//			List<UserPayment> userPaymentList = user.getUserPaymentList();
//
//			model.addAttribute("userShippingList", userShippingList);
//			model.addAttribute("userPaymentList", userPaymentList);
//
//			model.addAttribute("shippingAddress", shippingAddress);
//
//			model.addAttribute("classActiveShipping", true);
//
//			if (userPaymentList.size() == 0) {
//				model.addAttribute("emptyPaymentList", true);
//			} else {
//				model.addAttribute("emptyPaymentList", false);
//			}
//
//			model.addAttribute("emptyShippingList", false);
//
//			return "checkout";
//		}
//	}
//
//	@RequestMapping("/setPaymentMethod")
//	public String setPaymentMethod(@RequestParam("userPaymentId") Long userPaymentId, Principal principal,
//			Model model) {
//		User user = userService.findByUsername(principal.getName());
//		UserPayment userPayment = userPaymentService.findById(userPaymentId);
//		UserBilling userBilling = userPayment.getUserBilling();
//
//		if (userPayment.getUser().getId() != user.getId()) {
//			return "badRequestPage";
//		} else {
//			paymentService.setByUserPayment(userPayment, payment);
//
//			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
//
//			billingAddressService.setByUserBilling(userBilling, billingAddress);
//
//			model.addAttribute("shippingAddress", shippingAddress);
//			model.addAttribute("payment", payment);
//			model.addAttribute("billingAddress", billingAddress);
//			model.addAttribute("cartItemList", cartItemList);
//			model.addAttribute("shoppingCart", user.getShoppingCart());
//
//			List<String> stateList = USConstants.listOfUSStatesCode;
//			Collections.sort(stateList);
//			model.addAttribute("stateList", stateList);
//
//			List<UserShipping> userShippingList = user.getUserShippingList();
//			List<UserPayment> userPaymentList = user.getUserPaymentList();
//
//			model.addAttribute("userShippingList", userShippingList);
//			model.addAttribute("userPaymentList", userPaymentList);
//
//			model.addAttribute("shippingAddress", shippingAddress);
//
//			model.addAttribute("classActivePayment", true);
//
//			model.addAttribute("emptyPaymentList", false);
//
//			if (userShippingList.size() == 0) {
//				model.addAttribute("emptyShippingList", true);
//			} else {
//				model.addAttribute("emptyShippingList", false);
//			}
//
//			return "checkout";
//		}
//	}

}
