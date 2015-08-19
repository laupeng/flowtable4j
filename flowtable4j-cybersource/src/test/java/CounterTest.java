
import com.ctrip.infosec.flowtable4j.master.*;
import com.ctrip.infosec.flowtable4j.visa.*;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
public class CounterTest {

    protected static JsonMapper mapper = new JsonMapper();
    @Test
    public void testVisa() throws Exception {
        VisaRequest request = new VisaRequest();
        request.setMerchantID("cybersource_ctrip");
        request.setMerchantReferenceCode("MRC-123");
        VisaCard card=new VisaCard();
        card.setAccountNumber("5555555555554444");
        card.setCardType("MASTER");
        card.setExpirationMonth(new BigInteger("8"));
        card.setExpirationYear(new BigInteger("2018"));
        request.setCard(card);
        VisaClient client= new VisaClient();
        VisaResponse map =client.requestVisa(request);
        System.out.println(mapper.toJson(map));

    }

    @Test
    public void testMaster() throws Exception {
        MasterRequest request = new MasterRequest();
        request.setMerchantreference("DC_Test_03");

        FraudOnlyTxn fraudOnlyTxn=new FraudOnlyTxn();
        request.setFraudOnlyTxn(fraudOnlyTxn);
        //FraudOnlyTxn include MasterCard && BankResponse
        MasterCard card = new MasterCard();
        BankResponse response=new BankResponse();
        fraudOnlyTxn.setCard(card);
        fraudOnlyTxn.setResponse(response);


        Risk risk=new Risk();
        request.setRisk(risk);
        //Risk include CustomerDetail
        CustomerDetails customerDetails=new CustomerDetails();
        risk.setCustomerDetails(customerDetails);

        //CustomerDetail include BillingDetail,RiskDetail,PersonalDetail,Jouney
        BillingDetails billingDetails=new BillingDetails();
        RiskDetails riskDetails=new RiskDetails();
        PersonalDetails personalDetails=new PersonalDetails();
        Journey journey= new Journey();
        customerDetails.setRiskDetails(riskDetails);
        customerDetails.setBillingDetails(billingDetails);
        customerDetails.setPersonalDetails(personalDetails);
        customerDetails.setJourney(journey);
        //Jouney include Legs and Passengers
        List<Leg> legs=new ArrayList<Leg>();
        List<Passenger> passengers=new ArrayList<Passenger>();
        journey.setLegs(legs);
        journey.setPassengers(passengers);

        card.setExpirydate("0116");

        response.setAuth_code("11223344");
        response.setAvs_address_response("2");
        response.setAvs_postcode_response("1");
        response.setCv2_response("2");

        fraudOnlyTxn.setEci("01");
        fraudOnlyTxn.setAmount("700.00");
        fraudOnlyTxn.setCurrency("826");
        fraudOnlyTxn.setTran_type("pre");

        risk.setChannel("M");
        risk.setMerchant_location("Edinburgh");

        billingDetails.setName("Jono Nel");
        billingDetails.setCity("Chobham");
        billingDetails.setCountry("GB");
        billingDetails.setAddress_line1("Unit 3, Studley Court");
        billingDetails.setAddress_line2("Chobham");
        billingDetails.setZip_code("GU24 8EB");
        billingDetails.setState_province("Surrey");

        customerDetails.setPayment_method("CC");
        customerDetails.setTransaction_type("Other");

        riskDetails.setAccount_number("123");
        riskDetails.setIp_address("207.232.39.2");
        riskDetails.setEmail_address("refer@datacash.com");

        personalDetails.setDate_of_birth("1964-01-01");
        personalDetails.setFirst_name("Bob");
        personalDetails.setSurname("Nell");

        journey.setPnr("ABC123");
        journey.setTicket_number("478935793");

        Leg leg=new Leg();
        leg.setDepart_airport("EDI");
        leg.setDepart_country("UK");
        leg.setDepart_datetime("2030-10-01 13:23:11");
        leg.setDepart_airport_timezone("+00:00");
        leg.setArrival_airport("WRD");
        leg.setCarrier("BA");
        leg.setFlight_number("508");
        leg.setFare_basiscode("LWC");
        leg.setFare_class("Y");
        leg.setBase_fare("3000");
        leg.setCurrency_code("826");
        legs.add(leg);
        Passenger passenger=new Passenger();
        passenger.setFirst_name("Daniel");
        passenger.setSurname("Moore");
        passenger.setPassenger_type("A");
        passenger.setNationality("GB");
        passenger.setId_number("666");
        passenger.setLoyalty_number("32794");
        passenger.setLoyalty_type("Super Loyal");
        passenger.setLoyalty_tier("3");
        passengers.add(passenger);

        MasterClient client= new MasterClient();
        MasterResponse masterResponse = client.requestMaster(request);
        System.out.println(masterResponse.getResponseBody());


    }
}
