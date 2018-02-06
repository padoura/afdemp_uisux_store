package org.afdemp.uisux.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.afdemp.uisux.domain.User;
import org.afdemp.uisux.domain.security.PasswordResetToken;
import org.afdemp.uisux.domain.security.Role;
import org.afdemp.uisux.domain.security.UserRole;
import org.afdemp.uisux.domain.AbstractSale;
import org.afdemp.uisux.domain.Address;
import org.afdemp.uisux.domain.CartItem;
import org.afdemp.uisux.domain.Category;
import org.afdemp.uisux.domain.ClientOrder;
import org.afdemp.uisux.domain.CreditCard;
import org.afdemp.uisux.domain.Product;
import org.afdemp.uisux.service.AddressService;
import org.afdemp.uisux.service.CartItemService;
import org.afdemp.uisux.service.CategoryService;
import org.afdemp.uisux.service.ClientOrderService;
import org.afdemp.uisux.service.CreditCardService;
import org.afdemp.uisux.service.ProductService;
import org.afdemp.uisux.service.UserRoleService;
import org.afdemp.uisux.service.UserService;
import org.afdemp.uisux.service.impl.UserSecurityService;
import org.afdemp.uisux.utility.MailConstructor;
import org.afdemp.uisux.utility.SecurityUtility;
import org.afdemp.uisux.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private MailConstructor mailConstructor;
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private ClientOrderService clientOrderService;

	@Autowired
	private CartItemService cartItemService;
	
	@RequestMapping("/myProfile")
	public String myProfile(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		model.addAttribute("user", user);
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		Address userShipping = new Address();
		model.addAttribute("userShipping", userShipping);
		
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);

		model.addAttribute("classActiveEdit", true);
		
		return "myProfile";
	}
	
	@RequestMapping("/listOfCreditCards")
	public String listOfCreditCards(
			Model model, Principal principal, HttpServletRequest request
			) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		model.addAttribute("user", user);
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		return "myProfile";
	}
	
	@RequestMapping("/listOfShippingAddresses")
	public String listOfShippingAddresses(
			Model model, Principal principal, HttpServletRequest request
			) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		model.addAttribute("user", user);
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		return "myProfile";
	}
	
	@RequestMapping("/addNewCreditCard")
	public String addNewCreditCard(
			Model model, Principal principal
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		model.addAttribute("user", user);
		
		model.addAttribute("addNewCreditCard", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		Address billingAddress = new Address();
		CreditCard creditCard = new CreditCard();
		
		
		model.addAttribute("billingAddress", billingAddress);
		model.addAttribute("creditCard", creditCard);

		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		return "myProfile";
	}
	
	@RequestMapping(value="/addNewCreditCard", method=RequestMethod.POST)
	public String addNewCreditCard(
			@ModelAttribute("CreditCard") CreditCard creditCard,
			@ModelAttribute("billingAddress") Address billingAddress,
			Principal principal, Model model
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		billingAddress.setUserRole(userRole);
		billingAddress = addressService.createAddress(billingAddress);
		creditCard.setBillingAddress(billingAddress);
		creditCard.setUserRole(userRole);
		creditCardService.createCreditCard(creditCard);
		
//		userRoleService.updateBillingAddress(billingAddress, creditCard, userRole);
		
		model.addAttribute("user", user);
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		return "myProfile";
	}
	
	
	@RequestMapping("/updateCreditCard")
	public String updateCreditCard(
			@ModelAttribute("creditCardId") Long creditCardId, Principal principal, Model model
			) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		CreditCard creditCard = creditCardService.findById(creditCardId);
		
		if(userRole.getUserRoleId() != creditCard.getUserRole().getUserRoleId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			Address billingAddress = creditCard.getBillingAddress();
			model.addAttribute("creditCard", creditCard);
			model.addAttribute("billingAddress", billingAddress);
			
			model.addAttribute("addNewCreditCard", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);
			
			model.addAttribute("userCreditCartList", userRole.getCreditCardList());
			model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
			List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
			List<ClientOrder> clientOrderList = new ArrayList<>();
			for (AbstractSale as : abstractSaleList) {
				clientOrderList.add((ClientOrder) as);
			}
			
			model.addAttribute("clientOrderList", clientOrderList);
			
			return "myProfile";
		}
	}
	
	@RequestMapping("/addNewShippingAddress")
	public String addNewShippingAddress(
			Model model, Principal principal
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		model.addAttribute("user", user);
		
		model.addAttribute("addNewShippingAddress", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfCreditCards", true);
		
		Address shippingAddress = new Address();
		
		model.addAttribute("shippingAddress", shippingAddress);
		
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		return "myProfile";
	}
	
	@RequestMapping(value="/addNewShippingAddress", method=RequestMethod.POST)
	public String addNewShippingAddressPost(
			@ModelAttribute("shippingAddress") Address shippingAddress,
			Principal principal, Model model
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		userRoleService.updateShippingAddress(shippingAddress, userRole);
		
		model.addAttribute("user", user);
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveShipping", true);
		model.addAttribute("listOfCreditCards", true);
		
		return "myProfile";
	}
	
	@RequestMapping("/updateShippingAddress")
	public String updateShippingAddress(
			@ModelAttribute("shippingAddressId") Long shippingAddressId, Principal principal, Model model
			) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		Address shippingAddress = addressService.findById(shippingAddressId);
		
		if(userRole.getUserRoleId() != shippingAddress.getUserRole().getUserRoleId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			model.addAttribute("shippingAddress", shippingAddress);
			
			model.addAttribute("addNewShippingAddress", true);
			model.addAttribute("classActiveShipping", true);
			model.addAttribute("listOfCreditCards", true);
			
			model.addAttribute("userCreditCartList", userRole.getCreditCardList());
			model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
			List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
			List<ClientOrder> clientOrderList = new ArrayList<>();
			for (AbstractSale as : abstractSaleList) {
				clientOrderList.add((ClientOrder) as);
			}
			
			model.addAttribute("clientOrderList", clientOrderList);
			
			return "myProfile";
		}
	}
	
	@RequestMapping(value="/setDefaultCreditCard", method=RequestMethod.POST)
	public String setDefaultCreditCard(
			@ModelAttribute("defaultCreditCardId") Long defaultCreditCardId, Principal principal, Model model
			) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		creditCardService.setDefaultCreditCard(defaultCreditCardId, userRole);
		
		
		
		model.addAttribute("user", user);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		return "myProfile";
	}
	
	@RequestMapping(value="/setDefaultShippingAddress", method=RequestMethod.POST)
	public String setDefaultShippingAddress(
			@ModelAttribute("defaultShippingAddressId") Long defaultShippingAddressId, Principal principal, Model model
			) {
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		addressService.setDefaultShippingAddress(defaultShippingAddressId, userRole);
		
		model.addAttribute("user", user);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		model.addAttribute("listOfShippingAddresses", true);
		
		model.addAttribute("userCreditCartList", userRole.getCreditCardList());
		model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
		List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		
		model.addAttribute("clientOrderList", clientOrderList);
		
		return "myProfile";
	}
	
	@RequestMapping("/removeCreditCard")
	public String removeCreditCard(
			@ModelAttribute("id") Long creditCardId, Principal principal, Model model
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		CreditCard creditCard = creditCardService.findById(creditCardId);
		
		if(userRole.getUserRoleId() != creditCard.getUserRole().getUserRoleId()) {
			return "badRequestPage";
		} else {
			
			creditCardService.removeFromUserRole(creditCardId, userRole);
			
			model.addAttribute("user", user);
			
			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);
			
			model.addAttribute("userCreditCartList", userRole.getCreditCardList());
			model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
			List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
			List<ClientOrder> clientOrderList = new ArrayList<>();
			for (AbstractSale as : abstractSaleList) {
				clientOrderList.add((ClientOrder) as);
			}
			
			model.addAttribute("clientOrderList", clientOrderList);
			
			return "myProfile";
		}
	}
	
	
	@RequestMapping("/removeShippingAddress")
	public String removeShippingAddress(
			@ModelAttribute("shippingAddressId") Long shippingAddressId, Principal principal, Model model
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		Address shippingAddress = addressService.findById(shippingAddressId);
		
		if(userRole.getUserRoleId() != shippingAddress.getUserRole().getUser().getId()) {
			return "badRequestPage";
		} else {
			model.addAttribute("user", user);
			
			addressService.removeFromUserRole(shippingAddressId, userRole);
			
			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("classActiveBilling", true);
			model.addAttribute("listOfShippingAddresses", true);
			
			model.addAttribute("userCreditCartList", userRole.getCreditCardList());
			model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
			List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
			List<ClientOrder> clientOrderList = new ArrayList<>();
			for (AbstractSale as : abstractSaleList) {
				clientOrderList.add((ClientOrder) as);
			}
			
			model.addAttribute("clientOrderList", clientOrderList);
			
			return "myProfile";
		}
	}
	
	
	@RequestMapping("/orderDetail")
	public String orderDetail(
			@RequestParam("clientOrderId") Long clientOrderId,
			Principal principal, Model model
			){
		User user = userService.findByUsername(principal.getName());
		UserRole userRole = userRoleService.findByUserAndRole(user, "ROLE_CLIENT");
		
		ClientOrder clientOrder = clientOrderService.findOne(clientOrderId);
		
		if(userRole.getUserRoleId() != clientOrder.getUserRole().getUser().getId()) {
			return "badRequestPage";
		} else {
			List<CartItem> cartItemList = cartItemService.findByClientOrder(clientOrder);
			
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("user", user);
			model.addAttribute("order", clientOrder);
			
			model.addAttribute("userCreditCartList", userRole.getCreditCardList());
			model.addAttribute("userShippingAddressList", userRole.getUserShippingAddressList());
			List<AbstractSale> abstractSaleList	= userRole.getAbstractSaleList();
			List<ClientOrder> clientOrderList = new ArrayList<>();
			for (AbstractSale as : abstractSaleList) {
				clientOrderList.add((ClientOrder) as);
			}
			
			model.addAttribute("clientOrderList", clientOrderList);
			
			Address shippingAddress = new Address();
			model.addAttribute("shippingAddress", shippingAddress);
			
			
			model.addAttribute("listOfShippingAddresses", true);
			model.addAttribute("classActiveOrders", true);
			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("displayOrderDetail", true);
			
			return "myProfile";
		}
	}
	
	

	
	
	

	
}



//
//	
//	

//	

//	
//	

//	
//	

//	

//	

//	
//	

//	
//	
//

//	
//	@RequestMapping(value="/updateUserInfo", method=RequestMethod.POST)
//	public String updateUserInfo(
//			@ModelAttribute("user") User user,
//			@ModelAttribute("newPassword") String newPassword,
//			Model model
//			) throws Exception {
//		User currentUser = userService.findById(user.getId());
//		
//		if(currentUser == null) {
//			throw new Exception ("User not found");
//		}
//		
//		/*check email already exists*/
//		if (userService.findByEmail(user.getEmail())!=null) {
//			if(userService.findByEmail(user.getEmail()).getId() != currentUser.getId()) {
//				model.addAttribute("emailExists", true);
//				return "myProfile";
//			}
//		}
//		
//		/*check username already exists*/
//		if (userService.findByUsername(user.getUsername())!=null) {
//			if(userService.findByUsername(user.getUsername()).getId() != currentUser.getId()) {
//				model.addAttribute("usernameExists", true);
//				return "myProfile";
//			}
//		}
//		
////		update password
//		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")){
//			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
//			String dbPassword = currentUser.getPassword();
//			if(passwordEncoder.matches(user.getPassword(), dbPassword)){
//				currentUser.setPassword(passwordEncoder.encode(newPassword));
//			} else {
//				model.addAttribute("incorrectPassword", true);
//				
//				return "myProfile";
//			}
//		}
//		
//		currentUser.setFirstName(user.getFirstName());
//		currentUser.setLastName(user.getLastName());
//		currentUser.setUsername(user.getUsername());
//		currentUser.setEmail(user.getEmail());
//		
//		userService.save(currentUser);
//		
//		model.addAttribute("updateSuccess", true);
//		model.addAttribute("user", currentUser);
//		model.addAttribute("classActiveEdit", true);
//		
//		model.addAttribute("listOfShippingAddresses", true);
//		model.addAttribute("listOfCreditCards", true);
//		
//		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());
//
//		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
//				userDetails.getAuthorities());
//		
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//		model.addAttribute("orderList", user.getOrderList());
//		
//		return "myProfile";
//	}
//	
//	
//	
//	
//	
//}
