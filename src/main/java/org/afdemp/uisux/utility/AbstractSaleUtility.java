package org.afdemp.uisux.utility;

import java.util.ArrayList;
import java.util.List;

import org.afdemp.uisux.domain.AbstractSale;
import org.afdemp.uisux.domain.ClientOrder;
import org.springframework.stereotype.Component;

@Component
public class AbstractSaleUtility {
	
	public static List<ClientOrder> castToClientList(List<AbstractSale> abstractSaleList){
		List<ClientOrder> clientOrderList = new ArrayList<>();
		for (AbstractSale as : abstractSaleList) {
			clientOrderList.add((ClientOrder) as);
		}
		return clientOrderList;
	}

}
