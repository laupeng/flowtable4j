package com.ctrip.flowtable4j.core.flowRule.impl;

import java.util.List;
import java.util.Map;

import com.ctrip.flowtable4j.core.flowRule.AbstractCheckRiskCtrip;

public class CheckRiskCtripFlight extends AbstractCheckRiskCtrip {

	enum FlightPassengerProperty {
		PassengerName, 
		PassengerNationality, 
		PassengerCardID,

		// / <summary>
		// / 赋值 PassengerName + PassengerCardID
		// / </summary>
		PassengerNameCardID,
		
		
		// / <summary>
		// / 乘机人证件号前6位
		// / </summary>
		PassengerCardID6,

		// / <summary>
		// / 乘机人证件号长度
		// / </summary>
		PassengerCardIDLengthOne,

		// / <summary>
		// / 赋值 UID+PassengerName
		// / </summary>
		UidPassengerName,

		UidPassengerNameCardID,

		CCardNoCodePassengerNameCardID,

		MobilePhonePassengerCardID,

		EMailPassengerNameCardID
	}

	@Override
	public boolean isCheckEntityInnerList(String columnName) {
		// TODO Auto-generated method stub
		return isFlightPassenger(columnName);
	}

	@Override
	public List<Map> getInnerList(String columnName, Map checkEntity) {
		// TODO Auto-generated method stub
		if(isFlightPassenger(columnName))
			return (List<Map>)checkEntity.get("PassengerList");
		else 
			return null;

	}

	private boolean isFlightPassenger(String columnName) {
		// TODO Auto-generated method stub
		try {
			FlightPassengerProperty.valueOf(FlightPassengerProperty.class, columnName);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

}
