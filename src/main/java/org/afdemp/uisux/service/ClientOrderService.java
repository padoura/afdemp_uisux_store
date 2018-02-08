package org.afdemp.uisux.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.afdemp.uisux.domain.AbstractSale;
import org.afdemp.uisux.domain.ClientOrder;
import org.afdemp.uisux.domain.security.UserRole;

public interface ClientOrderService {

	ClientOrder createClientOrder(ClientOrder clientOrder);
	
	List<ClientOrder> fetchOrdersByPeriod(Timestamp fromTimestamp, Timestamp toTimestamp);
	
	ClientOrder updateOrderStatusToShipped(ClientOrder clientOrder);
	
	ClientOrder updateOrderStatusToDelivered(ClientOrder clientOrder);

	ClientOrder updateShippingMethod(ClientOrder clientOrder, String method);

	ClientOrder updateShippingDate(ClientOrder clientOrder, Date date);

	List<ClientOrder> findAllUndistributedEarnings();

	void distributeEarningsToAllMembers(Long clientOrderId);

	ClientOrder findOne(Long l);

	List<ClientOrder> findByUserRole(UserRole userRole);

}
